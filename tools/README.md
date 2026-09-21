# Development tools

Two things live here:

* `seed/` — fills the database with a consistent set of sample data
* `sms-simulator/` — a fake SMS gateway that can misbehave on demand

Both are plain Python 3 with no third-party packages. The seeder shells out to
the `mysql` client.

---

## 1. Sample data

```bash
tools/seed/seed_db.py --apply
```

Start the application once first, so Hibernate (`ddl-auto=update`) creates the
tables. The seeder only writes rows; it never creates schema.

It truncates `product`, `customer`, `code`, `received_message` and `message`,
removes every `user` except `admin`, and reloads them. Defaults give:

| table              | rows | notes                                              |
| ------------------ | ---- | -------------------------------------------------- |
| `product`          | 12   | real-looking waterproofing SKUs, 5–120 points each  |
| `customer`         | 12   | unique `tp_number` and `phone`, all districts filled |
| `code`             | 48   | 4 per product, 14 marked used                       |
| `user`             | 10   | plus the existing `admin`                           |
| `received_message` | 22   | 12 `processed`, 2 `valid`, plus rejects             |
| `message`          | 25   | 20 delivered, 5 still queued                        |

The rows are generated together rather than independently, so the history hangs
together:

* every `processed` row points at a real customer and product, its text is a
  real code string, and that code row is marked used
* every `processed` row has the matching confirmation SMS that
  `ReceivedMessageService` would have queued, worded the same way
* `customer.points` equals what those rows earned, less any redemption noted in
  `customer.notes`
* rejected traffic (`invalid`, `used`, `unregistered`) carries the reply text
  from `SmsController`

Two deliberate loose ends give the running system something to do:

* **2 rows left at status `valid`** — `SMSScheduler.handleReceivedMessages`
  picks these up on the next tick, credits the points and queues two more
  messages
* **5 messages left at `send = 0`** — `SMSService.sendUnsentMessages` drains
  these to whatever `sms.api.url` points at

So after seeding and starting the app, traffic should appear at the gateway
within one scheduler tick. If it does not, the sender is already stuck.

Seeded staff accounts all use the password **`test1234`** (`admin` keeps
whatever `DataInitializer` set, `1234` by default). Output is deterministic for
a given `--seed`.

```bash
tools/seed/seed_db.py --out tools/seed/seed-data.sql   # write SQL, load it yourself
tools/seed/seed_db.py --customers 200 --codes-per-product 50 --apply
tools/seed/seed_db.py --help
```

`seed-data.sql` in this directory is the default output, committed so it can be
piped straight in:

```bash
mysql -u royalty -proyalty royalty < tools/seed/seed-data.sql
```

---

## 2. SMS gateway simulator

```bash
tools/sms-simulator/sms_sim.py
```

Opens two ports:

| port   | what                                                        |
| ------ | ----------------------------------------------------------- |
| `8081` | the gateway — point `sms.api.url` here                       |
| `8090` | dashboard at `/` and a JSON control API                      |

Point the application at it:

```properties
sms.api.url=http://localhost:8081/sms
```

or without touching the file:

```bash
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--sms.api.url=http://localhost:8081/sms"
```

It answers `200 Success` like the real gateway until told otherwise. The
control port stays responsive even when the gateway is deliberately wedged, so
you can always see what is happening.

### Modes

Set with `curl -X POST localhost:8090/state -d '{"mode":"..."}'`.

| mode        | behaviour                                                           |
| ----------- | ------------------------------------------------------------------- |
| `healthy`   | 200 Success straight away                                           |
| `slow`      | answers after `latency_ms` (+ `jitter_ms`)                          |
| `hang`      | accepts, reads the request, **never answers**, holds the socket open |
| `drop`      | accepts, then closes without answering                              |
| `reset`     | accepts, then sends a TCP RST                                       |
| `refuse`    | unbinds the port, so connects fail with ECONNREFUSED                |
| `error`     | 500                                                                 |
| `ratelimit` | 429 past `rate_limit_per_min`                                       |
| `partial`   | promises a body, sends half, holds the socket open                  |
| `staleconn` | first request on a socket succeeds with keep-alive, reuse hangs     |
| `leak`      | answers but never closes, so descriptors pile up at both ends       |
| `flaky`     | `failure_rate` of requests get `failure_mode`, the rest succeed     |

`hang`, `partial`, `staleconn` and `leak` are the ones that matter for a client
with no socket timeout — they all leave the caller waiting on a read rather
than handing it an error it could recover from.

### Scenarios

Presets that combine a mode with a trigger:

```bash
curl -X POST localhost:8090/scenario -d '{"name":"stuck-thread"}'
curl localhost:8090/scenarios          # list them with descriptions
```

| scenario           | what it does                                              |
| ------------------ | --------------------------------------------------------- |
| `healthy`          | baseline                                                  |
| `stuck-thread`     | fine for 10 messages, then hangs forever                  |
| `stuck-after-time` | fine for 60 seconds, then hangs forever                   |
| `slow-degrade`     | latency climbs 250ms per message up to 60s                |
| `gateway-down`     | refuses connections                                       |
| `gateway-restart`  | refuses for 45s, then recovers                            |
| `flaky-network`    | 30% resets, the rest slow                                 |
| `stale-pool`       | keep-alive sockets that die after first use               |
| `throttled`        | 429 past 10 a minute                                      |
| `truncated`        | half a response body                                      |
| `fd-leak`          | never closes a socket                                     |
| `errors`           | every message gets a 500                                  |

### Control API

| call                                | does                                             |
| ----------------------------------- | ------------------------------------------------ |
| `GET /`                             | dashboard, refreshes every 3s                    |
| `GET /state`                        | mode, settings, counters, latency, warnings      |
| `GET /log?limit=50`                 | recent gateway traffic                           |
| `GET /scenarios`                    | modes and scenarios with descriptions            |
| `POST /state`                       | change mode or any setting                       |
| `POST /scenario`                    | apply a preset                                   |
| `POST /release`                     | close held sockets — unblocks a hung caller      |
| `POST /reset`                       | back to healthy, counters cleared                |
| `POST /mo`                          | push inbound SMS at the application (see below)  |

Settings accepted by `POST /state`: `mode`, `latency_ms`, `jitter_ms`,
`latency_ramp_ms`, `max_latency_ms`, `failure_rate`, `failure_mode`,
`rate_limit_per_min`, `degrade_after_requests`, `degrade_after_seconds`,
`degrade_to`, `recover_after_seconds`, `recover_to`, `max_connections`, `body`.

### Generating inbound traffic

`POST /mo` posts to the application's `/api/sms-mo-callback` so you can create
load without a phone:

```bash
# one real code from a seeded customer -> points credited
curl -X POST localhost:8090/mo -d '{
  "phone":"+94786137916", "code":"AB12CD34EF"
}'

# 50 messages with random (invalid) codes
curl -X POST localhost:8090/mo -d '{
  "phones":["+94786137916","+94736121553"], "count":50
}'
```

Pick real codes with:

```sql
SELECT c.code, cu.phone FROM code c
  JOIN product p ON p.id = c.product_id
  CROSS JOIN customer cu
 WHERE c.used = 0 LIMIT 5;
```

Target something other than the default with `"target": "http://host:8080/api/sms-mo-callback"`.

---

## 3. The "delivery stops until we restart" failure

**Fixed.** What follows is what caused it, what changed, and how to re-run the
test as a regression check.

### The cause

`scheduling-1` — Spring's **single** scheduler thread — parked here and never
came back:

```
"scheduling-1" ... java.lang.Thread.State: RUNNABLE
  at sun.nio.ch.SocketDispatcher.read0(Native Method)
  ...
  at java.net.HttpURLConnection.getResponseCode(HttpURLConnection.java:531)
  at com.example.royalty.service.SMSService.sendSMS(SMSService.java:69)
  at com.example.royalty.util.SMSScheduler.sendUnsentMessages(SMSScheduler.java:23)
```

Three things combined:

1. `SMSService.sendSMS` opened an `HttpURLConnection` and called
   `getResponseCode()` without setting `setConnectTimeout` or `setReadTimeout`.
   The JDK defaults both to *infinite*. A gateway that accepted the TCP
   connection and then stopped answering left that call blocked with nothing to
   time it out, and since no exception was raised the `catch` never ran.
2. `SMSScheduler` declares both jobs with `@Scheduled` and nothing configured a
   `TaskScheduler`, so Spring's default single-threaded pool ran them. The
   blocked send therefore also stopped `handleReceivedMessages` — point
   crediting died at the same moment as delivery, and inbound codes silently
   piled up at status `valid`.
3. Nothing could free the thread, so only a restart cleared it.

The restart was never fixing an unrecoverable state. Closing the socket from
the gateway side (`POST /release`) made the blocked read return and the backlog
drained on its own — a restart was simply the only way anyone had to close that
socket.

### What changed

* [`SMSService`](../src/main/java/com/example/royalty/service/SMSService.java)
  sets a connect and a read timeout, so a silent gateway now costs one timeout
  per message instead of the process. It also disconnects in a `finally`
  (previously skipped whenever the send threw), drains the error stream, treats
  a non-2xx status as a failure rather than a success, and abandons a run after
  `sms.api.max-consecutive-failures` consecutive failures instead of spending a
  timeout on every message in the backlog.
* [`SchedulingConfig`](../src/main/java/com/example/royalty/config/SchedulingConfig.java)
  is new: it supplies a `ThreadPoolTaskScheduler` so the two scheduled jobs no
  longer share one thread. A stalled send can no longer stop point crediting.

New settings, all in `application.properties`:

| property                          | default | purpose                              |
| --------------------------------- | ------- | ------------------------------------ |
| `sms.api.connect-timeout-ms`      | 5000    | TCP connect timeout                  |
| `sms.api.read-timeout-ms`         | 10000   | response read timeout                |
| `sms.api.max-consecutive-failures`| 5       | abandon a run once the gateway is clearly down |
| `sms.scheduler.poolSize`          | 2       | scheduler threads; keep above 1      |

### Measured, before and after

Same test both times: gateway holding connections open and answering nothing,
with three messages queued and one inbound code awaiting credit.

|                                   | before        | after                  |
| --------------------------------- | ------------- | ---------------------- |
| send attempts during the outage   | 1, then never again | 24 and counting  |
| inbound code credited             | no, stuck at `valid` | yes             |
| recovered when the gateway healed | no, needed a restart | yes, drained in seconds |

### Re-running it as a regression test

```bash
# 1. gateway that works for 10 messages and then stops answering
tools/sms-simulator/sms_sim.py --scenario stuck-thread

# 2. application pointed at it
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--sms.api.url=http://localhost:8081/sms"

# 3. queue more than 10 messages
tools/seed/seed_db.py --apply --unsent 40
```

Watch `http://localhost:8090/`. After the tenth message the gateway stops
answering. What you should see is `held connections` climbing steadily and
`idle (s)` staying low — the sender timing out and coming back. A single held
connection with `idle (s)` climbing forever is the old broken behaviour.

Meanwhile, insert a row to check the other job is still alive:

```sql
INSERT INTO received_message (cid, message, phone, product_id, received_time, status)
  SELECT 1, c.code, (SELECT phone FROM customer WHERE id=1), c.product_id, NOW(6), 'valid'
    FROM code c WHERE c.used = 0 LIMIT 1;
```

It should reach status `processed` within a tick or two even while sending is
failing. Then flip the gateway back:

```bash
curl -X POST localhost:8090/state -d '{"mode":"healthy"}'
```

The backlog should drain with no restart.

### Still worth exercising

| scenario         | what it tells you                                                  |
| ---------------- | ------------------------------------------------------------------- |
| `stale-pool`     | whether a pooled keep-alive socket dropped by a firewall or LB is handled — it should now fail on the read timeout rather than wedge |
| `slow-degrade`   | whether the gateway is merely slow; at 500ms ticks, delivery falls behind without anything failing |
| `fd-leak`        | descriptor growth; pair with `lsof -p <pid> | wc -l`                 |
| `gateway-restart`| that the sender resumes by itself once the gateway comes back        |
| `flaky-network`  | that resets are retried sensibly rather than hammered at 500ms       |

One thing the fix does not change: a failed send leaves `send = 0`, so the row
is retried on the next tick — every 500ms in production
(`sms.scheduler.fixedDelay`). The consecutive-failure limit bounds the work per
run, but if you want real backoff, that needs an attempt counter on `message`.
