# snapserve backend — agent brief
The Spring Boot side of snapserve; only rules and traps that the build files do not reveal.

## How I Work Here
- Backend gate: `./gradlew build` before handoff; CI owns the full suite.
- Blocking Spring MVC only: never WebFlux, `Mono`, or `Flux`.
- Stay a modular monolith. A new domain module is a package under `com.snapserve`, not a Gradle subproject.
- Cross-module calls go through the other module's application services, never its repositories or entities.
- Schema changes are Flyway migrations only; Hibernate never owns the schema.
- CORS lives in the security configuration; never add `@CrossOrigin` to controllers.
- Trap: Gradle needs a Java 25 toolchain — when the default JDK differs, point `JAVA_HOME` at a JDK 25 install or the build fails resolving `compileJava`.

## Sources of Truth
| Fact | Where |
|---|---|
| Build & deps | `backend/snapserve-api/build.gradle.kts`, `gradle/libs.versions.toml` |
| Config & secrets | `src/main/resources/application.yml`, `.env.example` |
| Schema | `src/main/resources/db/migration/` |
Code and tests are the authority; this file never overrides executable truth.
