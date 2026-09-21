#!/usr/bin/env python3
"""Generate and apply sample data for the loyalty database.

The generated rows respect the JPA mappings in com.example.royalty.modal and the
relationships between them:

  product 1---* code                     (code.product_id -> product.id)
  customer *---* product via received_message
                                         (received_message.cid, .product_id)
  customer 1---* message                 (message.cid)

Consistency rules enforced by the generator:

  * every received_message with status 'processed' points at a real customer and
    product, its text is a real code string, and that code row has used = 1
  * every 'processed' row has a matching confirmation message for the same
    customer, worded the way ReceivedMessageService does it
  * customer.points equals the points earned from their 'processed' rows, minus
    any redemption recorded in customer.notes
  * codes that were never texted in are left used = 0

Output is deterministic: the same --seed always produces the same SQL.

Usage:
    ./seed_db.py --apply                 # generate and load into MySQL
    ./seed_db.py --out seed-data.sql     # just write the SQL
    ./seed_db.py --customers 50 --apply  # bigger data set
"""

from __future__ import annotations

import argparse
import random
import string
import subprocess
import sys
from datetime import datetime, timedelta

# BCrypt hash of "test1234" ($2y$ is accepted by Spring's BCryptPasswordEncoder).
SEED_PASSWORD = "test1234"
SEED_PASSWORD_HASH = "$2y$10$HjUVVsIs.dsvIUaA6aC5pO59Nmin3c0TV6gJTzbTTh.9CxcLfNwzS"

# (city, district, province) -- kept internally consistent so reports group sanely.
LOCATIONS = [
    ("Colombo", "Colombo", "Western"),
    ("Dehiwala", "Colombo", "Western"),
    ("Negombo", "Gampaha", "Western"),
    ("Kandy", "Kandy", "Central"),
    ("Matale", "Matale", "Central"),
    ("Galle", "Galle", "Southern"),
    ("Matara", "Matara", "Southern"),
    ("Jaffna", "Jaffna", "Northern"),
    ("Kurunegala", "Kurunegala", "North Western"),
    ("Anuradhapura", "Anuradhapura", "North Central"),
    ("Batticaloa", "Batticaloa", "Eastern"),
    ("Ratnapura", "Ratnapura", "Sabaragamuwa"),
]

REGIONS = ["Region 1 - West", "Region 2 - Central", "Region 3 - South", "Region 4 - North"]
TERRITORIES = ["T-101", "T-102", "T-205", "T-206", "T-310", "T-411", "T-512"]
CMDES = ["A. Perera", "N. Fernando", "S. Bandara", "M. Jayasuriya", "R. Wickrama"]
LOYALTY_TIERS = ["Gold", "Silver", "Bronze", "Platinum"]
DEALERS = ["Lanka Hardware", "City Builders", "Ceylon Traders", "Nimal Stores",
           "Unity Hardware", "Ranjan Enterprises", "Southern Supplies"]

FIRST_NAMES = ["Nimal", "Kamal", "Sunil", "Ruwan", "Chamara", "Dilhani", "Sanduni",
               "Pradeep", "Mahesh", "Thilak", "Anura", "Ishara", "Kasun", "Nadeeka",
               "Roshan", "Gayan", "Upul", "Shanika", "Dinesh", "Malini"]
LAST_NAMES = ["Perera", "Silva", "Fernando", "Bandara", "Jayawardena", "Gunasekara",
              "Rajapaksa", "Wijesinghe", "Dissanayake", "Ranasinghe"]

STREETS = ["Main Street", "Galle Road", "Temple Road", "Station Road", "Lake Drive",
           "Hospital Road", "Market Lane", "Kandy Road"]

# (material code, description, sku, points)
PRODUCTS = [
    ("PIDI-LW-1L", "Dr. Fixit Pidiproof LW+ 1L", "SKU-100011", 5),
    ("PIDI-LW-5L", "Dr. Fixit Pidiproof LW+ 5L", "SKU-100012", 20),
    ("PIDI-LW-20L", "Dr. Fixit Pidiproof LW+ 20L", "SKU-100013", 70),
    ("RAIN-CT-4L", "Dr. Fixit Raincoat Exterior 4L", "SKU-100021", 25),
    ("RAIN-CT-20L", "Dr. Fixit Raincoat Exterior 20L", "SKU-100022", 100),
    ("NEWC-BOND-1L", "Dr. Fixit Newcoat Bond 1L", "SKU-100031", 8),
    ("NEWC-BOND-5L", "Dr. Fixit Newcoat Bond 5L", "SKU-100032", 30),
    ("ROOF-SEAL-5L", "Dr. Fixit Roofseal Classic 5L", "SKU-100041", 35),
    ("ROOF-SEAL-20L", "Dr. Fixit Roofseal Classic 20L", "SKU-100042", 120),
    ("CRACK-FIL-1K", "Dr. Fixit Crack-X Paste 1kg", "SKU-100051", 6),
    ("CRACK-FIL-5K", "Dr. Fixit Crack-X Paste 5kg", "SKU-100052", 22),
    ("BATH-SEAL-2L", "Dr. Fixit Bathseal Flex 2L", "SKU-100061", 15),
]

USERS = [
    ("supervisor", "EMP-1001", "199012345678", "Loyalty programme supervisor", 0),
    ("dataentry1", "EMP-1002", "199223456789", "Data entry - Colombo", 1),
    ("dataentry2", "EMP-1003", "199334567890", "Data entry - Kandy", 1),
    ("cmde.perera", "EMP-1004", "198845678901", "CMDE - Western region", 1),
    ("cmde.fernando", "EMP-1005", "199156789012", "CMDE - Central region", 1),
    ("cmde.bandara", "EMP-1006", "199467890123", "CMDE - Southern region", 1),
    ("reports", "EMP-1007", "199078901234", "Read-only reporting account", 2),
    ("callcentre1", "EMP-1008", "199589012345", "Call centre agent", 2),
    ("callcentre2", "EMP-1009", "199690123456", "Call centre agent", 2),
    ("auditor", "EMP-1010", "198701234567", "Monthly audit account", 2),
]

# Rejection texts copied from SmsController so the seeded history matches what
# the running application would have produced.
MSG_UNREGISTERED = "Sorry, you are not registered with the Dr.Fixit - Loyalty program."
MSG_INVALID = "This code is not valid. Please check and enter the correct code."
MSG_USED = "This code is already used. Please check and enter the correct code."

BULK_CAMPAIGNS = [
    "Dr.Fixit Loyalty: double points on Raincoat packs until the end of this month.",
    "Dr.Fixit Loyalty: your points can now be redeemed at any authorised dealer.",
    "Dr.Fixit Loyalty: thank you for being with us. Keep texting your pack codes.",
]


def sql_str(value) -> str:
    """Quote a value for MySQL, mapping None to NULL."""
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "b'1'" if value else "b'0'"
    if isinstance(value, int):
        return str(value)
    if isinstance(value, datetime):
        return "'" + value.strftime("%Y-%m-%d %H:%M:%S.%f") + "'"
    return "'" + str(value).replace("\\", "\\\\").replace("'", "''") + "'"


def insert(table: str, columns: list[str], rows: list[tuple]) -> str:
    """Build one multi-row INSERT statement."""
    if not rows:
        return ""
    cols = ", ".join("`%s`" % c for c in columns)
    values = ",\n  ".join("(" + ", ".join(sql_str(v) for v in row) + ")" for row in rows)
    return "INSERT INTO `%s` (%s) VALUES\n  %s;\n" % (table, cols, values)


class Generator:
    def __init__(self, args):
        self.rng = random.Random(args.seed)
        self.args = args
        self.now = datetime.now().replace(microsecond=0)
        self.used_codes: set[str] = set()

    def random_code(self) -> str:
        """Mirrors ProductService.generateCode: 10 uppercase alphanumerics."""
        alphabet = string.ascii_uppercase + string.digits
        while True:
            value = "".join(self.rng.choice(alphabet) for _ in range(10))
            if value not in self.used_codes:
                self.used_codes.add(value)
                return value

    def past(self, max_days: int, min_days: int = 0) -> datetime:
        delta = timedelta(
            days=self.rng.randint(min_days, max_days),
            hours=self.rng.randint(0, 23),
            minutes=self.rng.randint(0, 59),
            seconds=self.rng.randint(0, 59),
        )
        return self.now - delta

    # -- table builders -------------------------------------------------

    def products(self):
        rows = []
        for pid, (code, desc, sku, points) in enumerate(PRODUCTS[: self.args.products], start=1):
            rows.append((pid, code, desc, points, sku))
        return rows

    def customers(self):
        rows = []
        for cid in range(1, self.args.customers + 1):
            city, district, province = LOCATIONS[(cid - 1) % len(LOCATIONS)]
            name = "%s %s" % (
                FIRST_NAMES[(cid - 1) % len(FIRST_NAMES)],
                LAST_NAMES[(cid - 1) % len(LAST_NAMES)],
            )
            # Phone is stored without spaces: SmsController looks the customer up
            # by an exact match on the number the gateway posts.
            phone = "+947%d%07d" % (self.rng.randint(0, 8), self.rng.randint(0, 9999999))
            rows.append({
                "id": cid,
                "name": name,
                "phone": phone,
                "address": "No. %d, %s, %s" % (
                    self.rng.randint(1, 250), self.rng.choice(STREETS), city),
                "points": 0,          # filled in once earnings are known
                "notes": None,
                "tp_number": "TP%05d" % (1000 + cid),
                "city": city,
                "district": district,
                "province": province,
                "sales_person_territory": self.rng.choice(TERRITORIES),
                "region": self.rng.choice(REGIONS),
                "assigned_cmde": self.rng.choice(CMDES),
                "loyalty_status": self.rng.choice(LOYALTY_TIERS),
                "current_ave_consumption_pm": "%d L/month" % self.rng.choice([20, 50, 100, 200, 500]),
                "linked_dealer_1": self.rng.choice(DEALERS),
                "linked_dealer_2": self.rng.choice(DEALERS) if self.rng.random() < 0.6 else None,
            })
        return rows

    def codes(self, products):
        rows = []
        code_id = 1
        for product in products:
            pid = product[0]
            for _ in range(self.args.codes_per_product):
                rows.append({
                    "id": code_id,
                    "code": self.random_code(),
                    "created_at": self.past(90, 30),
                    "used": False,
                    "product_id": pid,
                })
                code_id += 1
        return rows

    def users(self):
        rows = []
        for name, eid, nic, description, roll in USERS[: self.args.users]:
            rows.append((description, eid, name, nic, SEED_PASSWORD_HASH, roll))
        return rows

    def traffic(self, customers, products, codes):
        """Build received_message + message history, and credit customer points.

        Walks the same path the application does: a valid code is consumed, the
        customer is credited, and a confirmation SMS is queued.
        """
        received: list[dict] = []
        messages: list[dict] = []
        earned = {c["id"]: 0 for c in customers}
        points_by_product = {p[0]: p[3] for p in products}
        free_codes = [c for c in codes]
        self.rng.shuffle(free_codes)

        def take_code():
            return free_codes.pop() if free_codes else None

        # 1. Redemptions already processed: code consumed, points credited,
        #    confirmation SMS sent.
        processed_target = max(self.args.customers, 12)
        for i in range(processed_target):
            customer = customers[i % len(customers)]
            code = take_code()
            if code is None:
                break
            code["used"] = True
            points = points_by_product[code["product_id"]]
            received_at = self.past(60, 1)
            code["created_at"] = min(code["created_at"], received_at - timedelta(days=1))
            earned[customer["id"]] += points
            received.append({
                "cid": customer["id"],
                "message": code["code"],
                "phone": customer["phone"],
                "product_id": code["product_id"],
                "received_time": received_at,
                "status": "processed",
            })
            messages.append({
                "cid": customer["id"],
                "message": "You have earned %d points for this transaction and total "
                           "points eared %d. Thank you" % (points, earned[customer["id"]]),
                "phone": customer["phone"],
                "create_time": received_at + timedelta(seconds=2),
                "send": True,
                "send_time": received_at + timedelta(seconds=3),
            })

        # 2. Codes texted in twice -> rejected as already used.
        for customer in customers[:2]:
            already_used = next(
                (r for r in received if r["status"] == "processed" and r["cid"] != customer["id"]),
                None,
            )
            if not already_used:
                break
            received_at = self.past(10, 1)
            received.append({
                "cid": customer["id"],
                "message": already_used["message"],
                "phone": customer["phone"],
                "product_id": None,
                "received_time": received_at,
                "status": "used",
            })
            messages.append({
                "cid": customer["id"], "message": MSG_USED, "phone": customer["phone"],
                "create_time": received_at + timedelta(seconds=1),
                "send": True, "send_time": received_at + timedelta(seconds=2),
            })

        # 3. Typos -> code not found.
        for customer in customers[2:5]:
            received_at = self.past(10, 1)
            received.append({
                "cid": customer["id"],
                "message": "".join(self.rng.choice(string.ascii_uppercase + string.digits)
                                   for _ in range(10)),
                "phone": customer["phone"],
                "product_id": None,
                "received_time": received_at,
                "status": "invalid",
            })
            messages.append({
                "cid": customer["id"], "message": MSG_INVALID, "phone": customer["phone"],
                "create_time": received_at + timedelta(seconds=1),
                "send": True, "send_time": received_at + timedelta(seconds=2),
            })

        # 4. Texts from numbers that are not registered customers (cid stays NULL).
        for _ in range(3):
            received_at = self.past(10, 1)
            stranger = "+947%d%07d" % (self.rng.randint(0, 8), self.rng.randint(0, 9999999))
            received.append({
                "cid": None, "message": self.random_code(), "phone": stranger,
                "product_id": None, "received_time": received_at, "status": "unregistered",
            })
            messages.append({
                "cid": None, "message": MSG_UNREGISTERED, "phone": stranger,
                "create_time": received_at + timedelta(seconds=1),
                "send": True, "send_time": received_at + timedelta(seconds=2),
            })

        # 5. Two valid rows left unprocessed on purpose. When the application is
        #    next started, SMSScheduler.handleReceivedMessages picks these up,
        #    credits the points and queues two more outbound messages -- a live
        #    end-to-end check that the pipeline is running.
        for customer in customers[5:7]:
            code = take_code()
            if code is None:
                break
            code["used"] = True
            received.append({
                "cid": customer["id"], "message": code["code"], "phone": customer["phone"],
                "product_id": code["product_id"],
                "received_time": self.now - timedelta(minutes=self.rng.randint(1, 30)),
                "status": "valid",
            })

        # 6. A bulk campaign that was never delivered. These are exactly what
        #    SMSService.sendUnsentMessages drains, so they give the SMS simulator
        #    something to receive the moment the application starts.
        for customer in customers[: self.args.unsent]:
            messages.append({
                "cid": customer["id"],
                "message": self.rng.choice(BULK_CAMPAIGNS),
                "phone": customer["phone"],
                "create_time": self.now - timedelta(minutes=self.rng.randint(1, 120)),
                "send": False,
                "send_time": None,
            })

        # Apply earnings, then record a redemption for a couple of customers so
        # the balances are not merely the sum of everything ever earned.
        for customer in customers:
            customer["points"] = earned[customer["id"]]
        for customer in customers[:2]:
            if customer["points"] >= 20:
                redeemed = 20
                customer["points"] -= redeemed
                customer["notes"] = "Redeemed %d points at dealer counter." % redeemed

        return received, messages

    # -- assembly -------------------------------------------------------

    def build(self) -> str:
        products = self.products()
        customers = self.customers()
        codes = self.codes(products)
        received, messages = self.traffic(customers, products, codes)

        out = [
            "-- Sample data for the loyalty database.",
            "-- Generated by tools/seed/seed_db.py (seed=%d) on %s."
            % (self.args.seed, self.now.strftime("%Y-%m-%d %H:%M:%S")),
            "-- Regenerate with: tools/seed/seed_db.py --out tools/seed/seed-data.sql",
            "--",
            "-- All seeded staff accounts use the password: %s" % SEED_PASSWORD,
            "",
            "SET FOREIGN_KEY_CHECKS = 0;",
            "TRUNCATE TABLE `message`;",
            "TRUNCATE TABLE `received_message`;",
            "TRUNCATE TABLE `code`;",
            "TRUNCATE TABLE `customer`;",
            "TRUNCATE TABLE `product`;",
            "SET FOREIGN_KEY_CHECKS = 1;",
            "",
            "-- The admin account created by DataInitializer is left alone.",
            "DELETE FROM `user` WHERE `name` <> 'admin';",
            "",
        ]

        out.append(insert("product", ["id", "code", "description", "points", "sku"], products))

        customer_cols = ["id", "name", "phone", "address", "points", "notes", "tp_number",
                         "city", "district", "province", "sales_person_territory", "region",
                         "assigned_cmde", "loyalty_status", "current_ave_consumption_pm",
                         "linked_dealer_1", "linked_dealer_2"]
        out.append(insert("customer", customer_cols,
                          [tuple(c[k] for k in customer_cols) for c in customers]))

        code_cols = ["id", "code", "created_at", "used", "product_id"]
        out.append(insert("code", code_cols,
                          [tuple(c[k] for k in code_cols) for c in codes]))

        out.append(insert("user", ["description", "eid", "name", "nic", "password", "roll"],
                          self.users()))

        received.sort(key=lambda r: r["received_time"])
        received_cols = ["cid", "message", "phone", "product_id", "received_time", "status"]
        out.append(insert("received_message", received_cols,
                          [tuple(r[k] for k in received_cols) for r in received]))

        messages.sort(key=lambda m: m["create_time"])
        message_cols = ["cid", "create_time", "message", "phone", "send", "send_time"]
        out.append(insert("message", message_cols,
                          [tuple(m[k] for k in message_cols) for m in messages]))

        summary = [
            "-- Summary",
            "--   product           %4d" % len(products),
            "--   customer          %4d" % len(customers),
            "--   code              %4d (%d used)"
            % (len(codes), sum(1 for c in codes if c["used"])),
            "--   user              %4d (plus the existing admin)" % self.args.users,
            "--   received_message  %4d (%d processed, %d awaiting the scheduler)"
            % (len(received),
               sum(1 for r in received if r["status"] == "processed"),
               sum(1 for r in received if r["status"] == "valid")),
            "--   message           %4d (%d queued for delivery)"
            % (len(messages), sum(1 for m in messages if not m["send"])),
        ]
        out.append("\n".join(summary) + "\n")
        return "\n".join(out)


def apply_sql(sql: str, args) -> None:
    cmd = ["mysql", "-h", args.host, "-P", str(args.port), "-u", args.user,
           "-p" + args.password, args.database]
    proc = subprocess.run(cmd, input=sql, text=True, capture_output=True)
    stderr = "\n".join(line for line in proc.stderr.splitlines()
                       if "Using a password" not in line)
    if proc.returncode != 0:
        sys.stderr.write(stderr + "\n")
        sys.exit("Failed to load sample data (mysql exited %d)." % proc.returncode)
    if stderr.strip():
        sys.stderr.write(stderr + "\n")


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Generate and load sample data for the loyalty database.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__.split("Usage:")[-1])
    parser.add_argument("--customers", type=int, default=12,
                        help="number of customer rows (default: 12)")
    parser.add_argument("--products", type=int, default=12,
                        help="number of product rows, max %d (default: 12)" % len(PRODUCTS))
    parser.add_argument("--codes-per-product", type=int, default=4,
                        help="codes generated per product (default: 4)")
    parser.add_argument("--users", type=int, default=10,
                        help="number of staff accounts, max %d (default: 10)" % len(USERS))
    parser.add_argument("--unsent", type=int, default=5,
                        help="queued-but-undelivered messages (default: 5)")
    parser.add_argument("--seed", type=int, default=1337,
                        help="random seed, for reproducible output (default: 1337)")
    parser.add_argument("--out", metavar="FILE",
                        help="write the SQL to FILE (default: stdout unless --apply)")
    parser.add_argument("--apply", action="store_true",
                        help="load the SQL straight into MySQL")
    parser.add_argument("--host", default="localhost")
    parser.add_argument("--port", type=int, default=3306)
    parser.add_argument("--user", default="royalty")
    parser.add_argument("--password", default="royalty")
    parser.add_argument("--database", default="royalty")
    args = parser.parse_args()

    args.products = min(args.products, len(PRODUCTS))
    args.users = min(args.users, len(USERS))

    sql = Generator(args).build()

    if args.out:
        with open(args.out, "w") as handle:
            handle.write(sql)
        print("Wrote %s" % args.out)
    if args.apply:
        apply_sql(sql, args)
        print("Loaded sample data into %s@%s:%d/%s"
              % (args.user, args.host, args.port, args.database))
        print("Staff accounts use the password: %s" % SEED_PASSWORD)
    if not args.out and not args.apply:
        sys.stdout.write(sql)


if __name__ == "__main__":
    main()
