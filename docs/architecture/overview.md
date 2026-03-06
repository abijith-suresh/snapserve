# Architecture Overview

## System Design

SnapServe follows a microservices architecture with clear separation of concerns.

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Frontend (React)                     │
│                      Port: 3000                         │
└───────────────────────┬─────────────────────────────────┘
                        │
                        │ HTTP/REST
                        │
┌───────────────────────▼─────────────────────────────────┐
│                 API Gateway (Spring)                     │
│              Port: 9090 | Routes + Auth                  │
└───────┬───────┬───────┬───────┬──────────────────────────┘
        │       │       │       │
        ▼       ▼       ▼       ▼
   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
   │ Auth   │ │ User   │ │Booking │ │Notify  │
   │Service │ │Service │ │Service │ │Service │
   │:9000   │ │:9001   │ │:9002   │ │:9003   │
   └────┬───┘ └────┬───┘ └────┬───┘ └────┬───┘
        │          │          │          │
        └──────────┴────┬─────┴──────────┘
                        │
               ┌────────▼─────────┐
               │   MongoDB        │
               │   Port: 27017    │
               └──────────────────┘
```

## Service Responsibilities

### API Gateway (Port 9090)
- **Routing**: Routes requests to appropriate microservices
- **Authentication**: Validates JWT tokens on all protected routes
- **CORS**: Handles cross-origin requests from frontend
- **Rate Limiting**: Prevents abuse

### Auth Service (Port 9000)
- **Registration**: Customer and specialist sign-up
- **Authentication**: Login with JWT tokens
- **Token Management**: Access token refresh, logout
- **Account Security**: Failed login lockout after 5 attempts
- **Role Management**: CUSTOMER, SPECIALIST roles

### User Service (Port 9001)
- **Profiles**: Customer and specialist profile CRUD
- **Specialist Management**: Skills, availability, ratings
- **Admin Functions**: Specialist approval, user management
- **Reference Implementation**: Shows target patterns for all services

### Booking Service (Port 9002)
- **Bookings**: Create, update, cancel appointments
- **Reviews**: Customer reviews for completed bookings
- **Complaints**: Customer complaints about specialists
- **Status Management**: PENDING, CONFIRMED, COMPLETED, CANCELLED

### Notification Service (Port 9003)
- **Email Notifications**: Booking confirmations, status updates
- **Templates**: Thymeleaf-based email templates
- **Queue Processing**: Async email sending
- **Status**: Currently being rebuilt

## Communication Patterns

### Frontend → API
- All requests go through API Gateway at `http://localhost:9090`
- JWT Bearer token in Authorization header
- Standard REST with JSON payloads
- ApiResponse<T> wrapper for all responses

### Service-to-Service
- Internal calls use Feign Client through user-service-client
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

1. **Single Responsibility**: Each service does one thing well
2. **API-First**: Frontend only talks to gateway, never directly to services
3. **DTO Pattern**: All data transfer uses DTOs (Java Records), never entities
4. **Validation**: Bean Validation on all DTOs
5. **Standardized Responses**: ApiResponse<T> wrapper for consistency
6. **Constructor Injection**: Never field injection (@Autowired)
7. **Transactional Boundaries**: Service methods annotated with @Transactional
8. **No WebFlux**: Blocking MVC only (no reactive streams)

## Shared Code

The `common` module provides:
- **Auditable**: Base entity with createdAt/updatedAt
- **Exceptions**: Standard exceptions (ResourceNotFound, BadRequest, etc.)
- **Responses**: ApiResponse<T>, ErrorResponse wrappers
- **JWT Utils**: Token parsing and validation
- **Auto-Configuration**: Automatic registration via Spring Boot

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

See [Environment Variables](./deployment/environment-variables.md) for complete list.
