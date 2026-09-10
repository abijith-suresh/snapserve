# SnapServe — Agent Context

SnapServe is a booking platform for local services. Customers discover professionals such as
plumbers and electricians, review their offerings, and request appointments. Professionals
publish fixed-price or hourly offerings and manage booking requests.

## Architecture

One Spring Boot modular monolith and one React frontend:

| Application | Port | Responsibility |
| --- | --- | --- |
| `backend/snapserve-api` | 8080 | Auth, profiles, offerings, availability, bookings, and reviews |
| `frontend` | 3000 | Customer and professional web application |
| PostgreSQL | 5432 | Persistent application data |

The backend is one deployable application. Domain boundaries are represented by Java packages,
not by separately deployed services. There is no API gateway, service discovery, Feign, admin
workflow, complaints module, payments module, or notification service in the MVP.

## Tech stack

- **Backend**: Java 25, Spring Boot 4.1.1, Spring MVC, Spring Security JWT, Spring Data JPA,
  PostgreSQL, Flyway, Gradle Kotlin DSL
- **Frontend**: React, TypeScript strict mode, Bun, Vite, Tailwind CSS, Axios, React Query,
  Zustand
- **Infra**: Docker Compose, GitHub Actions

## Key rules for agents

1. Keep the MVP booking-first: customers request appointments; professionals accept or decline.
2. No WebFlux: use standard blocking Spring MVC. Never add `Mono` or `Flux`.
3. No hardcoded secrets. Use environment variables documented in `.env.example`.
4. YAML configuration only. Use `application.yml` and profile-specific YAML files.
5. PostgreSQL schema changes must be Flyway migrations. Keep `ddl-auto=validate`.
6. CORS belongs in the backend security configuration. Do not add `@CrossOrigin` to controllers.
7. Keep module boundaries explicit: use application services, and do not reach across modules into
   another module's repositories or persistence models.
8. TypeScript is strict. `bun run type-check` must pass with zero errors.
9. Use Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`, `ci:`).
10. Run `./gradlew build` for the backend and `cd frontend && bun run build` for the frontend.

## Directory structure

```text
snapserve/
├── backend/snapserve-api/
│   ├── src/main/java/com/snapserve/
│   │   ├── shared/
│   │   ├── auth/
│   │   ├── customer/
│   │   ├── provider/
│   │   ├── booking/
│   │   └── review/
│   └── src/main/resources/db/migration/
├── frontend/
├── docs/
├── docker-compose.yml
├── .env.example
└── .github/workflows/
```

## Running locally

```bash
cp .env.example .env   # set POSTGRES_PASSWORD and JWT_SECRET
docker compose up --build
# API: http://localhost:8080
# Frontend: run `cd frontend && bun run dev`
```
