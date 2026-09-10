# SnapServe architecture

## Runtime shape

SnapServe uses one deployable Spring Boot application backed by PostgreSQL. The React frontend
calls that application directly. Docker Compose supplies PostgreSQL and the API for local runs.

This keeps the monorepo simple while retaining explicit module boundaries. The intended backend
packages are:

| Module | Responsibility |
| --- | --- |
| `auth` | Registration, login, token lifecycle, account roles |
| `customer` | Customer profile and booking history |
| `provider` | Professional profile, offerings, and availability |
| `booking` | Request, accept, decline, cancel, and complete flows |
| `review` | Reviews attached to completed bookings |
| `notification` | Deferred until the booking flow is stable |
| `shared` | Security, API errors, persistence and cross-cutting concerns |

The modules are package boundaries, not separate deployables. A module should expose use cases
to other modules through application services rather than reaching into another module's
repositories or persistence models.

## MVP decisions

- The product is booking-first; there are no payments, complaints, or admin workflows.
- A user has one primary role in the MVP: `CUSTOMER` or `PROVIDER`. The database role table is
  intentionally extensible for dual-role accounts later.
- Professionals self-publish their profile and offerings; no approval workflow is required.
- An offering is either `FIXED` or `HOURLY`.
- A customer creates a booking request. The professional accepts or declines it.
- Booking addresses are stored as a snapshot so later profile edits do not change historical
  appointments.
- Email notifications are a later capability. The booking domain should emit application events
  before an email provider is introduced.

## Data ownership

PostgreSQL is the source of truth. Flyway owns schema changes and JPA owns object mapping; runtime
schema generation is disabled with `ddl-auto=validate`.

The first migration creates the core marketplace tables. It deliberately does not install
PostgreSQL extensions, so it can run on managed PostgreSQL services with restricted permissions.
