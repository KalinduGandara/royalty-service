# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./mvnw spring-boot:run                      # run locally (port 8080, needs MySQL)
./mvnw clean package                        # builds target/loyalty-application.war
./mvnw clean package -DskipTests            # what CI does
./mvnw test                                 # runs RoyaltyApplicationTests
./mvnw test -Dtest=RoyaltyApplicationTests#contextLoads
```

The only test is `contextLoads`, and it starts the full context — so it needs a reachable MySQL at
`jdbc:mysql://localhost:3306/royalty` (user/password `royalty`/`royalty`). There is no test profile or
embedded DB, which is why `.github/workflows/maven.yml` builds with `-DskipTests`.

Spring Boot is pinned to `3.2.0-SNAPSHOT`, resolved from the `spring-snapshots` / `spring-milestones`
repositories declared in `pom.xml`; builds need network access to `repo.spring.io`.

## Packaging

WAR, not an executable JAR: `spring-boot-starter-tomcat` is `provided` and `RoyaltyApplication` extends
`SpringBootServletInitializer`, so the app deploys to an external Tomcat as well as running standalone.
This is required because the views are JSPs under `src/main/webapp/WEB-INF/view/` (resolved via
`spring.mvc.view.prefix/suffix`), and JSPs do not work from an executable jar. `finalName` is
`loyalty-application`, so the artifact is `target/loyalty-application.war` regardless of the Maven version.

CI (`.github/workflows/maven.yml`) triggers on `v*` tags or manual dispatch, and attaches `target/*.war`
to a GitHub release.

## Development tools

`tools/` holds two Python 3 helpers (stdlib only), documented in
[tools/README.md](tools/README.md):

- `tools/seed/seed_db.py --apply` loads a consistent sample data set (12
  customers, 12 products, 48 codes, 22 received messages, 25 messages, 10 staff
  accounts, password `test1234`). It writes rows only — start the app once first
  so Hibernate creates the schema. It deliberately leaves 2 received messages at
  status `valid` and 5 messages unsent, so the scheduler has work to pick up.
- `tools/sms-simulator/sms_sim.py` is a fake SMS gateway that can hang, reset,
  refuse, throttle or slowly degrade on command, with a dashboard on port 8090.
  Point `sms.api.url` at `http://localhost:8081/sms`.

The README also documents the "SMS delivery stops until we restart" failure and
its fix. Two things caused it and both are now guarded, so keep them in mind
when touching this path: `SMSService` must keep its connect and read timeouts
(the JDK defaults to none, and a gateway that accepts a connection then goes
silent throws nothing), and `SchedulingConfig` must keep `sms.scheduler.poolSize`
above 1, because `SMSScheduler`'s two jobs would otherwise share one thread and a
stalled send would also stop point crediting.

## Architecture

A loyalty-points system: customers text product codes to an SMS shortcode and earn points, which staff
redeem through a JSP admin UI.

Package layout (note the naming):
- `modal/` — JPA entities (`Customer`, `Product`, `Code`, `Message`, `ReceivedMessage`, `User`). Misspelling of "model"; keep it consistent when adding entities.
- `dao/` — **not** data access; these are Lombok form-binding/request DTOs (`RedeemPointsDAO`, `BulkUploadDAO`, `SmsRequest`, …). Actual persistence lives in `repository/` (Spring Data JPA interfaces).
- `controller/` — `@Controller` returning JSP view names, except `SmsController` which is the `@RestController` under `/api`.
- `service/` — business logic; `util/` — `ReadFile` (CSV/Excel parsing), `SMSScheduler`, `AuthenticationSuccessListener`.

### SMS point-earning flow

This is the core pipeline and it spans four classes:

1. The gateway POSTs to `/api/sms-mo-callback` (`SmsController`). Every branch persists a `ReceivedMessage`
   with a status string — `unregistered`, `invalid`, `used`, or `valid` — and **always returns HTTP 200
   "Success"**, because the gateway retries on anything else. Rejections also queue an outbound `Message`
   explaining the failure to the customer.
2. `SMSScheduler` (`@EnableScheduling`, `fixedDelayString = ${sms.scheduler.fixedDelay}`) polls two jobs.
3. `ReceivedMessageService.handleReceivedMessages()` picks up `valid` rows, flips them to `processed`,
   credits `product.points` to the customer, and queues a confirmation `Message`.
4. `SMSService.sendUnsentMessages()` picks up `Message` rows with `send = false` and calls `sms.api.url`
   over plain `HttpURLConnection` GET with `?q=<sms.api.key>&destination=&message=`, then marks them sent.

Status strings are untyped literals shared across these classes; `ReportService.generateMaster` reads only
`processed` rows, so changing a status value breaks reporting silently.

`SmsController` also exposes a mock `GET /api/sms` that just logs and returns "Success" — the default
`sms.api.url` points at it, so out of the box the app talks to itself instead of a real gateway.

### Security

`SecurityConfig` permits all of `/api/**` (and exempts it from CSRF) and requires authentication for
everything else, via form login or HTTP basic. `UserDetailsServiceImpl` loads users from the `user` table
and gives everyone `ROLE_USER` — the `roll` column on `User` is stored but not used for authorization.
`DataInitializer` seeds an `admin` / `1234` user on every context refresh if one does not exist.

### Bulk upload

`CustomerController.uploadFile` and `ProductController.uploadFile` accept CSV or XLSX. The expected header
row is hardcoded as a `String[]` in each controller and validated by `ReadFile.checkHeaders` — exact count
and order, compared case- and whitespace-insensitively. The corresponding `createBulk` in the service then
reads **positional** column indexes. Adding or reordering a column means editing both the header array in
the controller and the indexes in the service.

Customer rows upsert on `tpNumber` (points are *added* to existing customers); product rows upsert on code.

### Code generation

`POST /product/generate` (`ProductController.downloadCsv`) generates N unique alphanumeric codes, persists
them, and streams the CSV straight to the response — there is no view for it. `ProductService.generateCode`
uses `org.apache.commons.lang3.RandomStringUtils`, which is a transitive dependency (via POI) and is not
declared in `pom.xml`; a dependency change that drops it will break compilation.

Reports (`ReportController.reportMaster`) are written with Apache POI `XSSFWorkbook` directly to the
response stream, same pattern.

## Conventions

Constructor injection throughout (no `@Autowired` on constructors). Services return `boolean` for
success/failure rather than throwing, and controllers translate that into `BindingResult.rejectValue`
or a `RedirectAttributes` flash message. Entities use Lombok `@Getter/@Setter/@NoArgsConstructor` and
carry the Jakarta validation annotations directly — they are bound straight to forms with
`@Valid @ModelAttribute`, so there are no separate view models for CRUD.

Date-range filters repeat a private `parseDate(String, LocalDateTime)` helper (pattern `yyyy-MM-dd HH:mm:ss`,
falling back to a default) in `MessageController`, `ProductController`, and `ReportController`.
