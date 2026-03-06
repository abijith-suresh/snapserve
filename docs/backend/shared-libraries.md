# Shared Libraries

Common code shared across microservices.

## Overview

The `common` module and `user-service-client` module provide shared functionality to avoid code duplication across services.

## Common Module

**Location**: `backend/common/`

Auto-configured Spring Boot library used by all services.

### Provided Components

**Base Classes**
- `Auditable` — Base entity with createdAt/updatedAt timestamps
- Extend this class for all entities that need audit fields

**Response Wrappers**
- `ApiResponse<T>` — Standard success response wrapper
- `ErrorResponse` — Standard error response
- `FieldValidationError` — Validation error details

All controllers return `ApiResponse<T>` for consistency.

**Exceptions**
- `ApiException` — Base exception class
- `ResourceNotFoundException` — 404 errors
- `BadRequestException` — 400 errors
- `ConflictException` — 409 errors
- `UnauthorizedException` — 401 errors
- `AccessDeniedException` — 403 errors

Use these exceptions instead of returning null or error codes.

**Global Exception Handler**
- `GlobalExceptionHandler` — Catches exceptions and converts to ErrorResponse
- Auto-registered via Spring Boot auto-configuration
- No manual configuration needed

**JWT Utilities**
- `JwtUtils` — Token parsing and validation
- Used by API Gateway for token validation
- Used by services to extract user info from tokens

**Auto-Configuration**
- `CommonAutoConfiguration` — Automatically configures all components
- Enabled via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Services just add the dependency, no manual config needed

### Usage

Add to service dependencies in `build.gradle.kts`.

All components are automatically available.

## User Service Client

**Location**: `backend/user-service-client/`

Feign Client for calling User Service from other services.

### Purpose

Provides type-safe HTTP client for User Service APIs:
- Get customer by ID
- Get specialist by ID
- Check if user exists
- Validate user data

### Usage

**1. Add dependency** to `build.gradle.kts`

**2. Enable Feign Clients** — Add `@EnableFeignClients` to main application class

**3. Inject and use** — Inject `UserServiceClient` into your service

See `backend/booking-service` for example usage in `BookingService`.

### Configuration

Environment variable:
- `USER_SERVICE_URL` — User service URL (e.g., `http://user-service:9001`)

### Error Handling

The client throws exceptions on failures:
- `ResourceNotFoundException` — User not found
- `FeignException` — Communication errors

Always wrap calls in try-catch and handle appropriately.

## Benefits

**Common Module:**
- Consistent API responses across all services
- Standardized error handling
- No code duplication for base classes
- Centralized JWT utilities

**User Service Client:**
- Type-safe API calls
- No manual HTTP client setup
- Consistent error handling
- Easy to mock for testing

## Best Practices

**When to add to Common:**
- Base classes used by multiple services
- Shared exception types
- Utility classes (JWT, validation)
- Response wrappers

**When NOT to add to Common:**
- Service-specific logic
- Business rules
- Service-specific DTOs

**When to create a new client:**
- Multiple services call the same service
- API surface is stable
- Type safety is important

## Links

- Common module: `backend/common/`
- User service client: `backend/user-service-client/`
- Feign integration: `backend/booking-service/` (example usage)
