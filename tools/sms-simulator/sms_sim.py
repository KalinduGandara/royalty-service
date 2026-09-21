#!/usr/bin/env python3
"""A fake SMS gateway that can misbehave on demand.

It stands in for the MT endpoint that SMSService calls:

    GET <sms.api.url>?q=<api key>&destination=<number>&message=<text>

and answers "Success" like the real gateway, until you tell it to do something
else. It is built on raw sockets rather than http.server so that it can also
reproduce failures that live below HTTP: connections that are accepted and then
never answered, resets, refusals, truncated bodies and stale keep-alive sockets.

Two ports are opened:

    --port          (8081) the gateway itself; point sms.api.url at this
    --control-port  (8090) a JSON control API and a status dashboard

The control port keeps working while the gateway is wedged, so you can always
see what is going on and change behaviour without restarting anything.

Quick start:

    ./sms_sim.py                                  # healthy gateway
    ./sms_sim.py --scenario stuck-thread          # start healthy, then hang
    open http://localhost:8090/                   # dashboard

    curl -X POST localhost:8090/state -d '{"mode":"hang"}'
    curl -X POST localhost:8090/scenario -d '{"name":"gateway-restart"}'
    curl localhost:8090/state
    curl -X POST localhost:8090/release           # free any hung connections

Point the application at it with:

    sms.api.url=http://localhost:8081/sms
"""

from __future__ import annotations

import argparse
import json
import random
import socket
import struct
import sys
import threading
import time
import urllib.error
import urllib.request
from collections import Counter, deque
from datetime import datetime
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse

# --------------------------------------------------------------------------
# Behaviour modes
# --------------------------------------------------------------------------

MODES = {
    "healthy": "Answer 200 Success immediately.",
    "slow": "Answer, but only after latency_ms (plus jitter).",
    "hang": "Accept the connection, read the request, never answer. The socket "
            "stays open, so a client with no read timeout blocks forever.",
    "drop": "Accept the connection, then close it without answering (clean FIN).",
    "reset": "Accept the connection, then send a TCP RST.",
    "refuse": "Stop listening altogether, so connects fail with ECONNREFUSED.",
    "error": "Answer 500 Internal Server Error.",
    "ratelimit": "Answer 429 once rate_limit_per_min is exceeded in a window.",
    "partial": "Send the status line, headers and a Content-Length, then only "
               "part of the body, and hold the socket open.",
    "staleconn": "Answer the first request on a connection with keep-alive, then "
                 "never answer again on that same socket. Reproduces a pooled "
                 "connection that the far end has silently abandoned.",
    "leak": "Answer normally but never close the socket, so both ends "
            "accumulate open file descriptors.",
    "flaky": "Pick healthy or failure_mode at random, weighted by failure_rate.",
}

# Presets. Each is just a patch applied over the current state.
SCENARIOS = {
    "healthy": {
        "_doc": "Everything works. The baseline.",
        "mode": "healthy", "latency_ms": 0, "latency_ramp_ms": 0,
    },
    "stuck-thread": {
        "_doc": "Deliver normally for 10 messages, then hang forever. This is the "
                "shape of a gateway that stops answering mid-stream, and the "
                "quickest way to prove whether the sender recovers on its own.",
        "mode": "healthy", "degrade_after_requests": 10, "degrade_to": "hang",
    },
    "stuck-after-time": {
        "_doc": "Healthy for 60 seconds, then hang. Same as stuck-thread but "
                "triggered by the clock rather than by volume.",
        "mode": "healthy", "degrade_after_seconds": 60, "degrade_to": "hang",
    },
    "slow-degrade": {
        "_doc": "Latency climbs by 250ms per message up to 60s. Shows how a "
                "gradually sickening gateway drags down throughput before it "
                "ever fails outright.",
        "mode": "slow", "latency_ms": 100, "latency_ramp_ms": 250,
        "max_latency_ms": 60000,
    },
    "gateway-down": {
        "_doc": "Nothing is listening. Connects fail immediately.",
        "mode": "refuse",
    },
    "gateway-restart": {
        "_doc": "Refuse connections for 45 seconds, then come back healthy. "
                "Use it to check the sender resumes without help.",
        "mode": "refuse", "recover_after_seconds": 45, "recover_to": "healthy",
    },
    "flaky-network": {
        "_doc": "30% of messages hit a TCP reset, the rest are slow.",
        "mode": "flaky", "failure_rate": 0.3, "failure_mode": "reset",
        "latency_ms": 800,
    },
    "stale-pool": {
        "_doc": "Keep-alive connections that go dead after first use. Catches "
                "clients that reuse a pooled socket the far end has dropped.",
        "mode": "staleconn",
    },
    "throttled": {
        "_doc": "429 after 10 messages a minute.",
        "mode": "ratelimit", "rate_limit_per_min": 10,
    },
    "truncated": {
        "_doc": "Promise a body, send half of it, hold the socket open.",
        "mode": "partial",
    },
    "fd-leak": {
        "_doc": "Answer every message but never close the socket.",
        "mode": "leak",
    },
    "errors": {
        "_doc": "Every message gets a 500.",
        "mode": "error",
    },
}

DEFAULT_STATE = {
    "mode": "healthy",
    "latency_ms": 0,
    "jitter_ms": 0,
    "latency_ramp_ms": 0,
    "max_latency_ms": 120000,
    "failure_rate": 0.0,
    "failure_mode": "drop",
    "rate_limit_per_min": 60,
    "degrade_after_requests": 0,
    "degrade_after_seconds": 0,
    "degrade_to": "hang",
    "recover_after_seconds": 0,
    "recover_to": "healthy",
    "max_connections": 0,
    "body": "Success",
}


def now_iso() -> str:
    return datetime.now().strftime("%H:%M:%S")


class State:
    """Mutable behaviour settings plus counters, guarded by one lock."""

    def __init__(self):
        self.lock = threading.RLock()
        self.values = dict(DEFAULT_STATE)
        self.mode_since = time.time()
        self.started_at = time.time()
        self.requests_in_mode = 0
        self.total_requests = 0
        self.outcomes = Counter()
        self.destinations = Counter()
        self.last_request_at = None
        self.last_delivered_at = None
        self.latencies: deque[float] = deque(maxlen=200)
        self.log: deque[dict] = deque(maxlen=300)
        self.in_flight = 0
        self.held: list[socket.socket] = []
        self.rate_window: deque[float] = deque()
        self.scenario = None
        self.notes: deque[str] = deque(maxlen=40)

    # -- settings ----------------------------------------------------

    def get(self, key):
        with self.lock:
            return self.values[key]

    def snapshot(self) -> dict:
        with self.lock:
            return dict(self.values)

    def patch(self, changes: dict, scenario: str | None = None) -> dict:
        """Apply setting changes, resetting mode counters if the mode moved."""
        applied = {}
        with self.lock:
            old_mode = self.values["mode"]
            for key, value in changes.items():
                if key.startswith("_") or key not in DEFAULT_STATE:
                    continue
                expected = type(DEFAULT_STATE[key])
                try:
                    if expected is float:
                        value = float(value)
                    elif expected is int:
                        value = int(value)
                    elif expected is str:
                        value = str(value)
                except (TypeError, ValueError):
                    continue
                self.values[key] = value
                applied[key] = value
            if scenario is not None:
                self.scenario = scenario
            if self.values["mode"] != old_mode:
                self.mode_since = time.time()
                self.requests_in_mode = 0
                self.note("mode %s -> %s" % (old_mode, self.values["mode"]))
        return applied

    def note(self, text: str) -> None:
        stamp = "%s  %s" % (now_iso(), text)
        self.notes.append(stamp)
        print("[sim] " + stamp, flush=True)

    # -- counters ----------------------------------------------------

    def record(self, entry: dict) -> None:
        with self.lock:
            self.total_requests += 1
            self.requests_in_mode += 1
            self.last_request_at = time.time()
            self.outcomes[entry["outcome"]] += 1
            if entry.get("destination"):
                self.destinations[entry["destination"]] += 1
            if entry["outcome"] == "delivered":
                self.last_delivered_at = time.time()
                self.latencies.append(entry.get("latency_ms", 0))
            self.log.appendleft(entry)

    def reset(self) -> None:
        with self.lock:
            self.values = dict(DEFAULT_STATE)
            self.total_requests = 0
            self.requests_in_mode = 0
            self.outcomes.clear()
            self.destinations.clear()
            self.last_request_at = None
            self.last_delivered_at = None
            self.latencies.clear()
            self.log.clear()
            self.rate_window.clear()
            self.mode_since = time.time()
            self.scenario = None
            self.note("state reset")

    def rate_limited(self) -> bool:
        limit = self.get("rate_limit_per_min")
        cutoff = time.time() - 60
        with self.lock:
            while self.rate_window and self.rate_window[0] < cutoff:
                self.rate_window.popleft()
            if len(self.rate_window) >= limit:
                return True
            self.rate_window.append(time.time())
            return False

    def hold(self, conn: socket.socket) -> None:
        with self.lock:
            self.held.append(conn)

    def release_held(self) -> int:
        with self.lock:
            held, self.held = self.held, []
        for conn in held:
            try:
                conn.close()
            except OSError:
                pass
        if held:
            self.note("released %d held connection(s)" % len(held))
        return len(held)

    def report(self) -> dict:
        with self.lock:
            latencies = sorted(self.latencies)
            idle = (time.time() - self.last_request_at) if self.last_request_at else None
            return {
                "mode": self.values["mode"],
                "scenario": self.scenario,
                "settings": dict(self.values),
                "uptime_seconds": round(time.time() - self.started_at, 1),
                "seconds_in_mode": round(time.time() - self.mode_since, 1),
                "total_requests": self.total_requests,
                "requests_in_mode": self.requests_in_mode,
                "outcomes": dict(self.outcomes),
                "in_flight": self.in_flight,
                "held_connections": len(self.held),
                "seconds_since_last_request": round(idle, 1) if idle is not None else None,
                "seconds_since_last_delivery":
                    round(time.time() - self.last_delivered_at, 1)
                    if self.last_delivered_at else None,
                "latency_ms": {
                    "count": len(latencies),
                    "p50": latencies[len(latencies) // 2] if latencies else None,
                    "max": latencies[-1] if latencies else None,
                },
                "top_destinations": self.destinations.most_common(5),
                "sender_appears_wedged": self.wedged(),
                "events": list(self.notes)[-10:],
            }

    def wedged(self) -> bool:
        """True when we are holding a connection open and nothing new arrives.

        That combination means the caller is blocked inside its own send call
        rather than backing off and retrying, which is exactly the state a
        client with no socket timeout ends up in.
        """
        with self.lock:
            if not self.held or self.last_request_at is None:
                return False
            return (time.time() - self.last_request_at) > 20


# --------------------------------------------------------------------------
# The gateway
# --------------------------------------------------------------------------

class Gateway:
    def __init__(self, host: str, port: int, state: State):
        self.host, self.port, self.state = host, port, state
        self.listener: socket.socket | None = None
        self.listening = False
        self._stop = threading.Event()

    # -- listener management ----------------------------------------

    def open_listener(self) -> None:
        if self.listening:
            return
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind((self.host, self.port))
        sock.listen(64)
        sock.settimeout(0.5)
        self.listener = sock
        self.listening = True

    def close_listener(self) -> None:
        if not self.listening:
            return
        self.listening = False
        try:
            self.listener.close()
        except OSError:
            pass
        self.listener = None

    def sync_listener(self) -> None:
        """Bind or unbind so that 'refuse' really does refuse connections."""
        want = self.state.get("mode") != "refuse"
        if want and not self.listening:
            self.open_listener()
            self.state.note("listening on %s:%d" % (self.host, self.port))
        elif not want and self.listening:
            self.close_listener()
            self.state.note("stopped listening (mode=refuse)")

    # -- accept loop -------------------------------------------------

    def serve_forever(self) -> None:
        self.sync_listener()
        while not self._stop.is_set():
            if not self.listening:
                time.sleep(0.2)
                continue
            try:
                conn, addr = self.listener.accept()
            except socket.timeout:
                continue
            except OSError:
                time.sleep(0.1)
                continue

            cap = self.state.get("max_connections")
            with self.state.lock:
                if cap and self.state.in_flight >= cap:
                    # Leave the connection unserviced; once the backlog fills,
                    # the client's connect() itself starts to block.
                    self.state.hold(conn)
                    continue
                self.state.in_flight += 1
            threading.Thread(target=self._handle, args=(conn, addr), daemon=True).start()

    def stop(self) -> None:
        self._stop.set()
        self.close_listener()

    # -- per connection ----------------------------------------------

    def _handle(self, conn: socket.socket, addr) -> None:
        keep_open = False
        seen_on_conn = 0
        try:
            conn.settimeout(30)
            while True:
                request = self._read_request(conn)
                if request is None:
                    return
                seen_on_conn += 1
                keep_open = self._respond(conn, addr, request, seen_on_conn)
                if not keep_open:
                    return
        except (socket.timeout, OSError):
            return
        finally:
            with self.state.lock:
                self.state.in_flight -= 1
            if not keep_open:
                try:
                    conn.close()
                except OSError:
                    pass

    @staticmethod
    def _read_request(conn: socket.socket) -> dict | None:
        buffer = b""
        while b"\r\n\r\n" not in buffer:
            chunk = conn.recv(4096)
            if not chunk:
                return None
            buffer += chunk
            if len(buffer) > 65536:
                return None
        head = buffer.split(b"\r\n\r\n", 1)[0].decode("latin-1")
        lines = head.split("\r\n")
        parts = lines[0].split(" ")
        if len(parts) < 2:
            return None
        target = urlparse(parts[1])
        params = parse_qs(target.query)
        return {
            "method": parts[0],
            "path": target.path,
            "query": {k: v[0] for k, v in params.items()},
        }

    def _respond(self, conn: socket.socket, addr, request: dict, seen_on_conn: int) -> bool:
        """Act out the current mode. Returns True to keep the socket open."""
        state = self.state
        settings = state.snapshot()
        mode = settings["mode"]
        query = request["query"]
        destination = query.get("destination", "")
        text = query.get("message", "")

        if mode == "flaky":
            mode = (settings["failure_mode"]
                    if random.random() < settings["failure_rate"] else "healthy")

        if mode == "ratelimit" and not state.rate_limited():
            mode = "healthy"

        if mode == "staleconn":
            # First use of a socket works; anything reusing it is ignored.
            mode = "healthy_keepalive" if seen_on_conn == 1 else "hang"

        entry = {
            "at": now_iso(),
            "from": "%s:%d" % addr,
            "path": request["path"],
            "destination": destination,
            "message": text[:80],
            "mode": mode,
            "outcome": mode,
        }

        # Latency, including the ramp used by slow-degrade.
        delay_ms = 0
        if mode in ("healthy", "healthy_keepalive", "slow", "partial", "leak"):
            delay_ms = settings["latency_ms"]
            if settings["latency_ramp_ms"]:
                delay_ms += settings["latency_ramp_ms"] * state.requests_in_mode
            if settings["jitter_ms"]:
                delay_ms += random.randint(0, settings["jitter_ms"])
            delay_ms = min(delay_ms, settings["max_latency_ms"])
        if delay_ms:
            time.sleep(delay_ms / 1000.0)

        try:
            if mode in ("healthy", "slow", "healthy_keepalive", "leak"):
                alive = mode in ("healthy_keepalive", "leak")
                conn.sendall(self._http(200, settings["body"], keep_alive=alive))
                entry["outcome"] = "delivered"
                entry["latency_ms"] = delay_ms
                state.record(entry)
                if mode == "leak":
                    state.hold(conn)
                    return True
                return alive

            if mode == "error":
                conn.sendall(self._http(500, "Internal Server Error"))
                entry["outcome"] = "error_500"

            elif mode == "ratelimit":
                conn.sendall(self._http(429, "Too Many Requests"))
                entry["outcome"] = "rate_limited"

            elif mode == "hang":
                # Read the request, answer nothing, keep the socket open. A
                # caller without a read timeout waits here indefinitely.
                state.hold(conn)
                entry["outcome"] = "hung"
                state.record(entry)
                return True

            elif mode == "drop":
                entry["outcome"] = "dropped"

            elif mode == "reset":
                conn.setsockopt(socket.SOL_SOCKET, socket.SO_LINGER,
                                struct.pack("ii", 1, 0))
                entry["outcome"] = "reset"

            elif mode == "partial":
                body = settings["body"]
                header = ("HTTP/1.1 200 OK\r\n"
                          "Content-Type: text/plain\r\n"
                          "Content-Length: %d\r\n\r\n" % (len(body) + 64))
                conn.sendall(header.encode() + body[: max(1, len(body) // 2)].encode())
                state.hold(conn)
                entry["outcome"] = "truncated"
                state.record(entry)
                return True

            else:
                conn.sendall(self._http(200, settings["body"]))
                entry["outcome"] = "delivered"

            state.record(entry)
            return False
        except OSError:
            entry["outcome"] = "send_failed"
            state.record(entry)
            return False

    @staticmethod
    def _http(status: int, body: str, keep_alive: bool = False) -> bytes:
        reason = {200: "OK", 429: "Too Many Requests",
                  500: "Internal Server Error"}.get(status, "OK")
        return (
            "HTTP/1.1 %d %s\r\n"
            "Content-Type: text/plain\r\n"
            "Content-Length: %d\r\n"
            "Connection: %s\r\n\r\n%s"
            % (status, reason, len(body),
               "keep-alive" if keep_alive else "close", body)
        ).encode()


# --------------------------------------------------------------------------
# Scheduled transitions
# --------------------------------------------------------------------------

class Ticker(threading.Thread):
    """Applies the time and volume based transitions, and keeps the listener
    in step with the current mode."""

    def __init__(self, state: State, gateway: Gateway):
        super().__init__(daemon=True)
        self.state, self.gateway = state, gateway
        self._stop = threading.Event()

    def run(self) -> None:
        while not self._stop.wait(0.25):
            settings = self.state.snapshot()
            elapsed = time.time() - self.state.mode_since

            after_n = settings["degrade_after_requests"]
            after_s = settings["degrade_after_seconds"]
            target = settings["degrade_to"]
            if target and target != settings["mode"]:
                if after_n and self.state.requests_in_mode >= after_n:
                    self.state.note("degrading to %s after %d requests"
                                    % (target, after_n))
                    self.state.patch({"mode": target, "degrade_after_requests": 0,
                                      "degrade_after_seconds": 0})
                elif after_s and elapsed >= after_s:
                    self.state.note("degrading to %s after %ds" % (target, after_s))
                    self.state.patch({"mode": target, "degrade_after_requests": 0,
                                      "degrade_after_seconds": 0})

            recover_s = settings["recover_after_seconds"]
            recover_to = settings["recover_to"]
            if recover_s and elapsed >= recover_s and settings["mode"] != recover_to:
                self.state.note("recovering to %s after %ds" % (recover_to, recover_s))
                self.state.patch({"mode": recover_to, "recover_after_seconds": 0})

            self.gateway.sync_listener()

    def stop(self) -> None:
        self._stop.set()


# --------------------------------------------------------------------------
# MO injector: drive inbound traffic into the application
# --------------------------------------------------------------------------

def send_mo(target: str, phone: str, message: str, shortcode: str,
            timeout: float) -> dict:
    payload = json.dumps({"phone_number": phone, "shortcode": shortcode,
                          "message": message}).encode()
    request = urllib.request.Request(
        target, data=payload, method="POST",
        headers={"Content-Type": "application/json"})
    started = time.time()
    try:
        with urllib.request.urlopen(request, timeout=timeout) as response:
            return {"phone": phone, "message": message, "status": response.status,
                    "body": response.read().decode("utf-8", "replace")[:80],
                    "ms": round((time.time() - started) * 1000)}
    except urllib.error.HTTPError as exc:
        return {"phone": phone, "message": message, "status": exc.code,
                "body": str(exc), "ms": round((time.time() - started) * 1000)}
    except Exception as exc:  # timeouts, refusals, resets
        return {"phone": phone, "message": message, "status": None,
                "body": "%s: %s" % (type(exc).__name__, exc),
                "ms": round((time.time() - started) * 1000)}


# --------------------------------------------------------------------------
# Control API
# --------------------------------------------------------------------------

class ControlHandler(BaseHTTPRequestHandler):
    state: State = None
    gateway: Gateway = None
    mo_target: str = ""

    protocol_version = "HTTP/1.1"

    def log_message(self, *args):  # keep the console for gateway traffic only
        pass

    # -- helpers -----------------------------------------------------

    def _send(self, status: int, body: bytes, content_type: str) -> None:
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Connection", "close")
        self.end_headers()
        self.wfile.write(body)

    def _json(self, payload, status: int = 200) -> None:
        self._send(status, json.dumps(payload, indent=2).encode(), "application/json")

    def _body(self) -> dict:
        length = int(self.headers.get("Content-Length") or 0)
        if not length:
            return {}
        raw = self.rfile.read(length).decode("utf-8", "replace")
        try:
            return json.loads(raw)
        except json.JSONDecodeError:
            # Also accept form-encoded, so plain curl -d a=b works.
            return {k: v[0] for k, v in parse_qs(raw).items()}

    # -- routes ------------------------------------------------------

    def do_GET(self):
        route = urlparse(self.path)
        query = {k: v[0] for k, v in parse_qs(route.query).items()}
        if route.path == "/":
            self._send(200, dashboard(self.state).encode(), "text/html; charset=utf-8")
        elif route.path == "/state":
            self._json(self.state.report())
        elif route.path == "/log":
            limit = int(query.get("limit", 50))
            self._json(list(self.state.log)[:limit])
        elif route.path in ("/modes", "/scenarios"):
            self._json({"modes": MODES,
                        "scenarios": {k: v["_doc"] for k, v in SCENARIOS.items()}})
        else:
            self._json({"error": "unknown path", "try": ["/", "/state", "/log",
                                                         "/scenarios"]}, 404)

    def do_POST(self):
        route = urlparse(self.path)
        body = self._body()

        if route.path == "/state":
            mode = body.get("mode")
            if mode and mode not in MODES:
                self._json({"error": "unknown mode '%s'" % mode,
                            "known": sorted(MODES)}, 400)
                return
            applied = self.state.patch(body)
            self.gateway.sync_listener()
            self._json({"applied": applied, "state": self.state.report()})

        elif route.path == "/scenario":
            name = body.get("name")
            if name not in SCENARIOS:
                self._json({"error": "unknown scenario '%s'" % name,
                            "known": sorted(SCENARIOS)}, 400)
                return
            self.state.patch(dict(DEFAULT_STATE))
            applied = self.state.patch(SCENARIOS[name], scenario=name)
            self.state.note("scenario '%s' started" % name)
            self.gateway.sync_listener()
            self._json({"scenario": name, "doc": SCENARIOS[name]["_doc"],
                        "applied": applied})

        elif route.path == "/release":
            freed = self.state.release_held()
            self._json({"released": freed})

        elif route.path == "/reset":
            self.state.release_held()
            self.state.reset()
            self.gateway.sync_listener()
            self._json({"state": self.state.report()})

        elif route.path == "/mo":
            self._handle_mo(body)

        else:
            self._json({"error": "unknown path",
                        "try": ["/state", "/scenario", "/release", "/reset", "/mo"]},
                       404)

    def _handle_mo(self, body: dict) -> None:
        """Post inbound SMS callbacks at the application to create traffic."""
        target = body.get("target") or self.mo_target
        count = int(body.get("count", 1))
        timeout = float(body.get("timeout", 10))
        shortcode = str(body.get("shortcode", "77000"))
        phones = body.get("phones") or ([body["phone"]] if body.get("phone") else [])
        codes = body.get("codes") or ([body["code"]] if body.get("code") else [])
        if not phones:
            self._json({"error": "give phone or phones"}, 400)
            return

        results = []
        for i in range(count):
            phone = str(phones[i % len(phones)])
            text = str(codes[i % len(codes)]) if codes else "".join(
                random.choice("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789") for _ in range(10))
            results.append(send_mo(target, phone, text, shortcode, timeout))
        self._json({"target": target, "sent": len(results), "results": results})


def dashboard(state: State) -> str:
    report = state.report()
    recent = list(state.log)[:25]

    def cell(value):
        return "-" if value is None else value

    rows = "".join(
        "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td class='o %s'>%s</td></tr>"
        % (e["at"], e["destination"] or "-",
           (e["message"] or "")[:44], e["mode"], e["outcome"], e["outcome"])
        for e in recent) or "<tr><td colspan='5'>No traffic yet.</td></tr>"

    outcomes = "".join("<span class='pill'>%s <b>%d</b></span>" % (k, v)
                       for k, v in sorted(report["outcomes"].items())) or "<i>none</i>"
    events = "".join("<div>%s</div>" % e for e in report["events"]) or "<i>none</i>"

    warning = ""
    if report["sender_appears_wedged"]:
        warning = (
            "<div class='warn'><b>The sender looks stuck.</b> This simulator is "
            "holding %d connection(s) open without answering, and no new request "
            "has arrived for %ss. A caller that was timing out and retrying would "
            "still be knocking. POST /release to close them and watch whether "
            "delivery resumes.</div>"
            % (report["held_connections"], report["seconds_since_last_request"]))

    return """<!doctype html><html><head><meta charset="utf-8">
<title>SMS gateway simulator</title><meta http-equiv="refresh" content="3">
<style>
 body{font:14px/1.5 ui-monospace,SFMono-Regular,Menlo,monospace;margin:0;padding:24px;
      background:#11141a;color:#dde3ec}
 h1{font-size:17px;margin:0 0 4px} h2{font-size:13px;margin:22px 0 8px;color:#8b97ab;
      text-transform:uppercase;letter-spacing:.08em}
 .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(150px,1fr));gap:10px}
 .card{background:#1a1f28;border:1px solid #29303d;border-radius:8px;padding:12px}
 .card .k{color:#8b97ab;font-size:11px;text-transform:uppercase;letter-spacing:.06em}
 .card .v{font-size:20px;margin-top:4px}
 .mode{color:#7dd3a0} .mode.bad{color:#ff8b6b}
 table{width:100%%;border-collapse:collapse;margin-top:6px}
 td,th{text-align:left;padding:5px 8px;border-bottom:1px solid #232a35;font-size:12.5px}
 th{color:#8b97ab;font-weight:500}
 .o{font-weight:600} .delivered{color:#7dd3a0} .hung,.dropped,.reset,.truncated{color:#ff8b6b}
 .error_500,.rate_limited,.send_failed{color:#e8c46a}
 .pill{display:inline-block;background:#232a35;border-radius:20px;padding:3px 10px;
       margin:2px 4px 2px 0;font-size:12px}
 .warn{background:#40210f;border:1px solid #8a4b1d;color:#ffc9a8;padding:12px;
       border-radius:8px;margin:16px 0}
 .ev{background:#161a21;border:1px solid #232a35;border-radius:8px;padding:10px;
     font-size:12px;color:#9fb0c6;white-space:pre-wrap}
</style></head><body>
<h1>SMS gateway simulator</h1>
<div style="color:#8b97ab">mode <span class="mode %s">%s</span>%s &middot; up %ss</div>
%s
<h2>Counters</h2>
<div class="grid">
  <div class="card"><div class="k">Requests</div><div class="v">%d</div></div>
  <div class="card"><div class="k">In flight</div><div class="v">%d</div></div>
  <div class="card"><div class="k">Held open</div><div class="v">%d</div></div>
  <div class="card"><div class="k">Idle (s)</div><div class="v">%s</div></div>
  <div class="card"><div class="k">Since delivery (s)</div><div class="v">%s</div></div>
  <div class="card"><div class="k">p50 latency (ms)</div><div class="v">%s</div></div>
</div>
<h2>Outcomes</h2><div>%s</div>
<h2>Events</h2><div class="ev">%s</div>
<h2>Recent traffic</h2>
<table><tr><th>Time</th><th>Destination</th><th>Message</th><th>Mode</th><th>Outcome</th></tr>
%s</table>
</body></html>""" % (
        "bad" if report["mode"] not in ("healthy", "slow") else "",
        report["mode"],
        (" (scenario: %s)" % report["scenario"]) if report["scenario"] else "",
        report["uptime_seconds"], warning,
        report["total_requests"], report["in_flight"], report["held_connections"],
        cell(report["seconds_since_last_request"]),
        cell(report["seconds_since_last_delivery"]),
        cell(report["latency_ms"]["p50"]), outcomes, events, rows)


# --------------------------------------------------------------------------

def main() -> None:
    parser = argparse.ArgumentParser(
        description="A fake SMS gateway that can misbehave on demand.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="modes:\n" + "".join("  %-11s %s\n" % (k, v) for k, v in MODES.items())
               + "\nscenarios:\n"
               + "".join("  %-17s %s\n" % (k, v["_doc"]) for k, v in SCENARIOS.items()))
    parser.add_argument("--host", default="0.0.0.0")
    parser.add_argument("--port", type=int, default=8081, help="gateway port")
    parser.add_argument("--control-port", type=int, default=8090)
    parser.add_argument("--scenario", choices=sorted(SCENARIOS), default="healthy",
                        help="scenario to start in")
    parser.add_argument("--mo-target",
                        default="http://localhost:8080/api/sms-mo-callback",
                        help="application endpoint used by POST /mo")
    parser.add_argument("--quiet", action="store_true",
                        help="do not print a line per message")
    args = parser.parse_args()

    state = State()
    gateway = Gateway(args.host, args.port, state)
    state.patch(SCENARIOS[args.scenario], scenario=args.scenario)

    if not args.quiet:
        original_record = state.record

        def record_and_print(entry):
            original_record(entry)
            print("[sms] %s  %-13s %-16s %s"
                  % (entry["at"], entry["outcome"], entry["destination"] or "-",
                     (entry["message"] or "")[:60]), flush=True)

        state.record = record_and_print

    try:
        gateway.sync_listener()
    except OSError as exc:
        sys.exit("Cannot bind gateway to %s:%d (%s)" % (args.host, args.port, exc))

    ticker = Ticker(state, gateway)
    ticker.start()
    threading.Thread(target=gateway.serve_forever, daemon=True).start()

    ControlHandler.state = state
    ControlHandler.gateway = gateway
    ControlHandler.mo_target = args.mo_target
    control = ThreadingHTTPServer((args.host, args.control_port), ControlHandler)
    control.daemon_threads = True

    print("SMS gateway    http://localhost:%d/sms   <- set sms.api.url to this"
          % args.port)
    print("Dashboard      http://localhost:%d/" % args.control_port)
    print("Scenario       %s -- %s"
          % (args.scenario, SCENARIOS[args.scenario]["_doc"]))
    print("Ctrl-C to stop.\n")

    try:
        control.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping.")
    finally:
        ticker.stop()
        gateway.stop()
        state.release_held()


if __name__ == "__main__":
    main()
