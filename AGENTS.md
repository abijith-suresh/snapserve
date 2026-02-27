# SnapServe — Agent Context

SnapServe is a service-booking platform where customers book appointments with specialists
(plumbers, electricians, etc). Admins manage specialist approvals and complaints.

## Architecture

5 Spring Boot microservices + 1 React frontend:

| Service | Port | Responsibility |
|---------|------|----------------|
| api-gateway | 9090 | Routing, JWT validation, CORS, rate limiting |
| auth-service | 9000 | Register, login, token management |
| user-service | 9001 | Customer, Specialist, Admin profiles |
| booking-service | 9002 | Bookings, reviews, complaints |
| notification-service | 9003 | Email notifications |

Frontend runs on port 3000. All frontend API calls go through api-gateway at port 9090.

## Tech Stack

- **Backend**: Java 21, Spring Boot 4.0.3, Spring Cloud 2025.1.1 (Oakwood), MongoDB Atlas, Gradle 8 (Kotlin DSL)
- **Frontend**: React 18 + TypeScript, Bun, Vite, shadcn/ui, Tailwind CSS, Axios, React Query, Zustand
- **Infra**: Docker Compose, GitHub Actions CI/CD

## Key Rules for Agents

1. **Read the issue description carefully** — each issue is atomic and self-contained.
2. **No WebFlux**: All services use standard `spring-boot-starter-web` (blocking). Never use WebFlux or reactive types (`Mono`, `Flux`).
3. **No hardcoded secrets**: All secrets come from environment variables. See `.env.example` for the full list.
4. **YAML config only**: Services use `application.yml` and `application-prod.yml`. No `.properties` files.
5. **Service URLs**: Services reach each other via Docker Compose DNS (e.g. `http://auth-service:9000`). URLs injected via `@Value` from env vars.
6. **CORS lives at the gateway only**: Never add `@CrossOrigin` to any controller.
7. **TypeScript strict**: Frontend is TypeScript strict mode. `bun run tsc --noEmit` must pass with zero errors.
8. **Conventional commits**: All commits must follow Conventional Commits format (`feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`, `ci:`). commitlint enforces this.
9. **Monorepo**: Backend is a Gradle multi-project build. Run `./gradlew :backend:<service-name>:build` for a specific service, or `./gradlew build` for all.
10. **No Eureka, no Config Server**: Removed. Docker Compose DNS handles service discovery. Per-service YAML handles config.

## Directory Structure

```
snapserve/
├── backend/
│   ├── api-gateway/           Spring Cloud Gateway
│   ├── auth-service/          JWT auth
│   ├── user-service/          Customer, Specialist, Admin profiles
│   ├── booking-service/       Bookings, reviews, complaints
│   └── notification-service/  Email only
├── frontend/                  React + TypeScript + Bun
├── docker-compose.yml
├── docker-compose.prod.yml
├── .env.example               All required env variable names (values blank)
├── .github/workflows/         CI pipelines
└── .husky/                    Pre-commit hooks
```

## Running Locally

```bash
cp .env.example .env   # fill in your values
docker compose up --build
# Frontend: http://localhost:3000
# API:      http://localhost:9090
```
