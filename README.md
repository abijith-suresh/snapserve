# SnapServe

SnapServe is a service-booking monorepo for customers booking appointments with specialists.

Current backend scope is intentionally limited to authentication, customer/specialist
profiles, bookings, reviews, and notifications. Admin workflows, complaint handling,
and specialist approval flows are not required unless explicitly reintroduced.

## Services

| Service | Port | Responsibility |
|---------|------|----------------|
| api-gateway | 9090 | Routing, JWT validation, CORS, rate limiting |
| auth-service | 9000 | Register, login, token management |
| user-service | 9001 | Customer and specialist profiles |
| booking-service | 9002 | Bookings and reviews |
| notification-service | 9003 | Email notifications |

## Backend Scope Guardrails

- In scope: authentication, customer profiles, specialist profiles, bookings, reviews, and notifications.
- Out of scope unless an issue explicitly asks for them: admin workflows, complaint handling, and specialist approval flows.
- For the `feat/backend-hardening` branch, prefer defensive validation, configuration safety, and test coverage around existing contracts instead of adding new backend workflows.

## Backend Verification Strategy

- Verify only the backend service you changed first with `./gradlew :backend:<service-name>:test`.
- Use existing `*ApplicationTests` classes as wiring smoke tests and focused service tests for branch-specific business rules.
- Run `./gradlew test` before cross-service review when a change touches shared backend code such as `:backend:common` or client modules.
- Treat out-of-scope flows as non-goals during verification so hardening work stays aligned with the reduced backend scope.

## TDD Approach For Hardening

- Start with a failing unit test that describes the hardening rule or invariant being added.
- Keep most hardening tests at the service layer with mocked repositories or downstream clients so business logic stays isolated and fast.
- Add or update happy-path and rejection-path assertions together for changes such as state transitions, verification flags, retry behavior, or token handling.
- Use full integration coverage only when a hardening change modifies Spring wiring, serialization, or inter-service contracts.

## Local Development

```bash
cp .env.example .env
docker compose up --build
```

- Frontend app via Docker Compose: `http://localhost:3000`
- API gateway: `http://localhost:9090`
- Frontend Vite dev server in `frontend/`: `http://localhost:5173`

## Repo Hygiene

- Backend generated outputs such as per-service `bin/` directories are local artifacts and should not be committed.
- Keep secrets in `.env` only; commit `.env.example` updates when required variable names change.
