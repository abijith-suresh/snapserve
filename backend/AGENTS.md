# SnapServe backend — agent brief
The Spring Boot side of SnapServe; this brief covers backend specifics only.

## The Project
Status: foundation only — `shared` (JWT security, CORS, API errors) is implemented; `auth`,
`customer`, `provider`, `booking`, and `review` are planned packages with no code yet.

## How I Work Here
- Quality gate: `./gradlew build` (Spotless google-java-format check plus tests); fix formatting with `./gradlew spotlessApply`, never by hand.
- Gradle resolves a Java 25 toolchain: when the default JDK differs, set `JAVA_HOME` to a JDK 25 install or the build fails while resolving `compileJava`.
- Blocking Spring MVC only — never add WebFlux, `Mono`, or `Flux`.
- One deployable modular monolith. Domain boundaries are Java packages under `com.snapserve`: reach another module only through its application services, never its repositories or entities.
- A new domain module is a new package, not a new Gradle subproject: `settings.gradle.kts` includes only `:backend:snapserve-api`.
- PostgreSQL schema changes are Flyway migrations in `src/main/resources/db/migration/`, named `V<n>__<description>.sql`. Hibernate never creates or alters schema (`ddl-auto=validate`).
- CORS belongs in `shared/security/SecurityConfig` via `app.cors.allowed-origins`; never add `@CrossOrigin` to controllers.
- YAML configuration only (`application.yml` plus profile files); secrets come from environment variables.
- Tests run on the H2 profile in `src/test/resources/application-test.yml`, where Flyway is disabled — tests do not exercise the migrations.

## Sources of Truth
| Fact | Where |
|---|---|
| Build & dependencies | `backend/snapserve-api/build.gradle.kts`, `gradle/libs.versions.toml` |
| Runtime config | `src/main/resources/application.yml`, `.env.example` |
| Schema | `src/main/resources/db/migration/` |
| Behavior | code + `src/test/` |
Code and tests are the authority; this file never overrides executable truth.

## Getting Around
- `src/main/java/com/snapserve/SnapServeApplication.java` — Spring Boot entry point.
- `src/main/java/com/snapserve/shared/security/` — JWT resource-server setup, role mapping, CORS.
- `src/main/java/com/snapserve/shared/exception/` — `ApiException` and `GlobalExceptionHandler` (`problem+json`).
- `src/main/java/com/snapserve/{auth,customer,provider,booking,review}/` — planned module packages, not created yet.
- `src/test/java/com/snapserve/SnapServeApplicationTests.java` — context-load test.
- `Dockerfile` — multi-stage Temurin 25 build used by Docker Compose.
