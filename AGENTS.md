# SnapServe — Agent Context

Service booking platform where customers book appointments with specialists (plumbers, electricians, etc). Admins manage specialist approvals and complaints.

## Architecture Overview

**5 Spring Boot microservices + React frontend:**

| Service | Port | Responsibility |
|---------|------|----------------|
| api-gateway | 9090 | Routing, JWT validation, CORS, rate limiting |
| auth-service | 9000 | JWT authentication, token refresh, account lockout |
| user-service | 9001 | Customer, Specialist, Admin profile management |
| booking-service | 9002 | Bookings, reviews, complaints |
| notification-service | 9003 | Email notifications (rebuilding) |

Frontend runs on port 3000. All API calls go through api-gateway at port 9090.

## Tech Stack

- **Backend**: Java 21, Spring Boot 4.0.3, Spring Cloud 2025.1.1 (Oakwood), MongoDB, Gradle 8 (Kotlin DSL)
- **Frontend**: React 19 + TypeScript (strict), Bun, Vite, shadcn/ui, Tailwind CSS v4, React Query v5, Zustand v5
- **Infra**: Docker Compose, GitHub Actions CI/CD

## Quick Commands

```bash
# Start everything
docker compose up --build

# Build specific service
./gradlew :backend:<service-name>:build

# Build all services
./gradlew build

# Frontend dev
bun run dev

# Frontend checks
bun run lint
bun run typecheck
```

## Critical Rules

1. **Read issue description carefully** — each issue is atomic
2. **Backend patterns**:
   - Java Records for DTOs (not Lombok @Data)
   - MapStruct for DTO mapping
   - Constructor injection with @RequiredArgsConstructor (never @Autowired field injection)
   - ApiResponse<T> wrapper for all responses
   - @Transactional on service methods
3. **No WebFlux**: Blocking Spring MVC only (no Mono/Flux)
4. **No hardcoded secrets**: All from environment variables (.env)
5. **YAML config only**: application.yml and application-prod.yml (no .properties)
6. **CORS lives at gateway only**: Never @CrossOrigin on controllers
7. **TypeScript strict**: `bun run tsc --noEmit` must pass with zero errors
8. **Conventional commits**: `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`, `ci:`
9. **Service URLs via Docker Compose DNS**: `http://<service-name>:<port>`

## Frontend Patterns

- **Server state**: React Query (TanStack Query) — configured but not yet implemented
- **Client state**: Zustand with persistence
- **Forms**: React Hook Form + Zod validation
- **Routing**: React Router 7 (data API pattern)
- **API calls**: Axios through gateway (port 9090)

## Service Architecture

```
Controller (@RestController, @RequestMapping)
  ↓
Service (@Service, @RequiredArgsConstructor, @Transactional)
  ↓
Repository (Spring Data MongoDB)
  ↓
MongoDB
```

All services use:
- `common` module: Auditable base class, exceptions, ApiResponse wrapper, JWT utilities
- MapStruct mappers for DTO conversion
- Java Records for DTOs with Bean Validation
- Constructor injection (never field injection)

## Directory Structure

```
backend/
├── api-gateway/           Spring Cloud Gateway (routing, auth)
├── auth-service/          JWT token management
├── user-service/          Profile management (reference implementation)
├── booking-service/       Bookings/reviews (refactoring in progress)
├── notification-service/  Email service (rebuilding)
├── common/                Shared code (auto-configured)
└── user-service-client/   Feign client for user-service

frontend/
├── components/ui/         shadcn/ui components
├── features/              Feature modules (auth, customer, specialist)
├── pages/                 Public pages
├── routes/                Route layouts
└── shared/                API client, types

docs/                      Comprehensive documentation
```

## Documentation

- **Full docs**: [docs/](./docs/index.md)
- **Architecture**: [docs/architecture/](./docs/architecture/)
- **Standards**: [docs/standards/](./docs/standards/)
- **Backend**: [docs/backend/](./docs/backend/)
- **Frontend**: [docs/frontend/](./docs/frontend/)
- **Deployment**: [docs/deployment/](./docs/deployment/)

## Environment

Copy `.env.example` to `.env` and fill in values.

## Links

- [Architecture Overview](./docs/architecture/overview.md)
- [Backend Standards](./docs/standards/code-style.md)
- [API Design Guidelines](./docs/standards/api-design.md)
- [Security Guidelines](./docs/standards/security.md)
- [Frontend Architecture](./docs/frontend/architecture.md)
