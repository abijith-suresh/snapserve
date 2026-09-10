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

## Run locally

```bash
cp .env.example .env
# Set POSTGRES_PASSWORD and JWT_SECRET in .env.
docker compose up --build
```

The API is available at `http://localhost:8080`. Health is exposed at
`http://localhost:8080/actuator/health`, and the OpenAPI UI will be available at
`http://localhost:8080/swagger-ui.html` once API modules are added.

Run the frontend separately during development:

```bash
cd frontend
bun install --frozen-lockfile
bun run dev
```

The first backend migration is in
`backend/snapserve-api/src/main/resources/db/migration`. It establishes accounts, customer and
professional profiles, service offerings, availability, bookings, and reviews. Admins,
complaints, payments, and email notifications are intentionally outside the MVP foundation.

## Build and test

```bash
./gradlew build
cd frontend && bun run type-check && bun run build
```

The revival work is organized as stacked local branches:

- `revival/01-dependency-upgrade`
- `revival/02-modular-monolith-foundation`
