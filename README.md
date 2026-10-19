# SnapServe

SnapServe is a local-service booking platform. Customers discover professionals such as
electricians and plumbers, review their service offerings, and request appointments.
Professionals publish fixed-price or hourly offerings and accept or decline booking requests.

The project is intentionally a monorepo with one Spring Boot backend and one React frontend.
The backend is a modular monolith: domain boundaries are kept inside one deployable application,
so the code can be split later only if there is a real need.

## Stack

- Java 25, Spring Boot 4.1.1, Spring MVC, Spring Security JWT
- PostgreSQL with Flyway migrations and Spring Data JPA
- React, TypeScript, Vite, Bun, Tailwind CSS
- Docker Compose for local PostgreSQL and the API

## Architecture

One deployable Spring Boot application backed by PostgreSQL; the React app calls it directly.
Docker Compose supplies PostgreSQL and the API for local runs. Domain boundaries are Java package
boundaries inside that application:

| Module | Responsibility |
| --- | --- |
| `shared` | Security, CORS, API errors, persistence and cross-cutting concerns |
| `auth` | Registration, login, token lifecycle, account roles |
| `customer` | Customer profile and booking history |
| `provider` | Professional profile, offerings, availability |
| `booking` | Request, accept, decline, cancel, and complete flows |
| `review` | Reviews attached to completed bookings |

Only `shared` exists so far; the other modules are planned packages under
`backend/snapserve-api/src/main/java/com/snapserve`. Modules expose use cases to each other through
application services rather than reaching into another module's repositories or persistence models.

PostgreSQL is the source of truth. Flyway owns schema changes and JPA owns object mapping; runtime
schema generation is disabled with `ddl-auto=validate`. The first migration creates the core
marketplace tables and deliberately installs no PostgreSQL extensions, so it can run on managed
PostgreSQL services with restricted permissions.

MVP decisions: the product is booking-first; a user has one primary role (`CUSTOMER` or
`PROVIDER`, extensible later); professionals self-publish without an approval workflow; an offering
is `FIXED` or `HOURLY`; booking addresses are snapshotted so later profile edits do not change
historical appointments; email notifications come later, after the booking domain emits events.

### Planned API surface

Base path `/api/v1`; identifiers are UUIDs; timestamps are ISO-8601 UTC; errors use Spring's
`application/problem+json` shape including the request path. These endpoints are the contract
target for the next slices — only the foundation exists today.

| Area | Endpoints | Access |
| --- | --- | --- |
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh` | Public |
| Professionals | `GET /professionals`, `GET /professionals/{id}` | Public |
| Offerings | `POST /professionals/me/offerings`, `PATCH /professionals/me/offerings/{id}` | Provider |
| Availability | `GET/PUT /professionals/me/availability` | Provider |
| Bookings | `POST /bookings`, `GET /bookings`, `GET /bookings/{id}` | Authenticated |
| Booking actions | `POST /bookings/{id}/accept`, `/decline`, `/cancel`, `/complete` | Role-dependent |
| Reviews | `POST /bookings/{id}/review`, `GET /professionals/{id}/reviews` | Role-dependent/public |

Bookings move through:

```text
REQUESTED -> CONFIRMED -> COMPLETED
     |            |
     v            v
  DECLINED     CANCELLED
```

Only the customer can create a request. Only the selected professional can confirm or decline one.
Cancellation rules and who may cancel at each state are encoded in the booking application service,
not inferred by controllers.

## Run locally

```bash
cp .env.example .env
# Set POSTGRES_PASSWORD and JWT_SECRET in .env.
docker compose up --build
```

The API is available at `http://localhost:8080`. Health is exposed at
`http://localhost:8080/actuator/health`, and the OpenAPI UI is at
`http://localhost:8080/swagger-ui.html`.

Run the frontend separately during development:

```bash
cd frontend
bun install --frozen-lockfile
bun run dev
```

Vite serves the frontend on `http://localhost:5173`. The API's default allowed CORS origin is
`http://localhost:3000`, so set `CORS_ALLOWED_ORIGINS` to match, or run Vite on port 3000.

## Build and test

```bash
bun run verify   # backend: ./gradlew build; frontend: lint, type-check, build
```

The modular-monolith revival is in flight but stalled: the foundation (schema, security, and
application shell) is merged, while the domain modules and API endpoints above are still planned.
