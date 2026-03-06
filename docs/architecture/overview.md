# Architecture Overview

High-level system design and technology choices.

## System Design

SnapServe follows a microservices architecture with clear separation of concerns.

## Architecture Components

**5 Spring Boot microservices:**

| Service | Port | Responsibility |
|---------|------|----------------|
| api-gateway | 9090 | Routing, JWT validation, CORS, rate limiting |
| auth-service | 9000 | JWT authentication, token refresh, account lockout |
| user-service | 9001 | Customer, Specialist, Admin profile management |
| booking-service | 9002 | Bookings, reviews, complaints |
| notification-service | 9003 | Email notifications (rebuilding) |

**Frontend:**
- Single Page Application (React + TypeScript)
- Port: 3000
- All API calls go through API Gateway at port 9090

**Database:**
- MongoDB (port 27017)
- Each service owns its collections
- No direct database sharing between services

## Communication Patterns

### Frontend → API

- All requests go through API Gateway at `http://localhost:9090`
- JWT Bearer token in Authorization header
- Standard REST with JSON payloads
- ApiResponse wrapper for all responses

### Service-to-Service

- Internal calls use Feign Client
- URLs via Docker Compose DNS: `http://<service>:<port>`
- No direct database access between services
- Each service owns its data

### Service → Database

- Spring Data MongoDB repositories
- Each service has its own database/collection
- MongoDB running in Docker container

## Technology Stack

### Backend

- **Language**: Java 21 (Temurin)
- **Framework**: Spring Boot 4.0.3
- **Cloud**: Spring Cloud 2025.1.1 (Oakwood)
- **Data**: Spring Data MongoDB
- **Security**: Spring Security with JWT
- **Communication**: OpenFeign for service calls
- **Build**: Gradle 8 with Kotlin DSL
- **DTO Mapping**: MapStruct 1.5.5
- **Utilities**: Lombok, validation

### Frontend

- **Framework**: React 19
- **Language**: TypeScript 5.9 (strict mode)
- **Styling**: Tailwind CSS v4
- **Components**: shadcn/ui + Radix UI
- **State**: Zustand (client), React Query (server)
- **Forms**: React Hook Form + Zod
- **Routing**: React Router 7
- **HTTP**: Axios
- **Build**: Vite + Bun

### Infrastructure

- **Containers**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Code Quality**: Spotless (Java), Biome (TypeScript)
- **Git Hooks**: Husky + lint-staged + commitlint

## Design Principles

1. **Single Responsibility** — Each service does one thing well
2. **API-First** — Frontend only talks to gateway, never directly to services
3. **DTO Pattern** — All data transfer uses DTOs (Java Records), never entities
4. **Validation** — Bean Validation on all DTOs
5. **Standardized Responses** — ApiResponse wrapper for consistency
6. **Constructor Injection** — Never field injection
7. **Transactional Boundaries** — Service methods annotated with @Transactional
8. **No WebFlux** — Blocking MVC only (no reactive streams)

## Architecture Patterns

### Backend Layer Structure

All backend services follow the same pattern:

1. **Controller** — REST endpoints, request validation, response formatting
2. **Service** — Business logic, transaction management
3. **Repository** — Data access via Spring Data MongoDB
4. **DTOs** — Java Records for request/response objects
5. **Mappers** — MapStruct interfaces for DTO conversion

See `backend/user-service` for reference implementation.

### Frontend Structure

Feature-based organization:

- **components/ui/** — shadcn/ui components
- **features/** — Feature modules (auth, customer, specialist)
- **pages/** — Public pages
- **routes/** — Route layouts and guards
- **shared/** — API client, TypeScript types

See `frontend/src/` for structure.

## Shared Code

The `common` module provides shared functionality:

- **Auditable** — Base entity with createdAt/updatedAt
- **Exceptions** — Standard exceptions (ResourceNotFound, BadRequest, etc.)
- **Responses** — ApiResponse, ErrorResponse wrappers
- **JWT Utils** — Token parsing and validation
- **Auto-Configuration** — Automatic Spring Boot registration

Location: `backend/common/`

## Development Workflow

1. Start MongoDB and services: `docker compose up`
2. Frontend development: `bun run dev` (port 3000)
3. API testing: Gateway at `http://localhost:9090`
4. Database: MongoDB at `localhost:27017`

## Environment Configuration

All configuration is externalized via environment variables:

- Database connections
- JWT secrets
- Service URLs
- Email credentials
- CORS origins

See `.env.example` for complete list.

## Decision Rationale

### Why Microservices?

- Independent deployment of services
- Technology flexibility per service
- Clear service boundaries
- Scalability per component

### Why MongoDB?

- Flexible schema for evolving requirements
- Native JSON matches our API format
- Good fit for document-based data (users, bookings)
- Horizontal scaling capabilities

### Why Java Records for DTOs?

- Immutability by default
- Built-in equals/hashCode/toString
- Less boilerplate than Lombok
- Better type safety

### Why Constructor Injection?

- Required dependencies are explicit
- Easier to test (no reflection needed)
- Immutable dependencies
- Spring best practice

### Why React Query?

- Automatic caching
- Background refetching
- Optimistic updates
- Loading/error states handled
- Industry standard for server state

## Links

- Service interactions: [service-interactions.md](./service-interactions.md)
- Backend standards: [../standards/code-style.md](../standards/code-style.md)
- API design: [../standards/api-design.md](../standards/api-design.md)
- Security: [../standards/security.md](../standards/security.md)
