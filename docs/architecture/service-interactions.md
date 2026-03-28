# Service Interactions

How microservices communicate with each other.

## Communication Overview

### External Communication (Frontend ↔ API)

All frontend requests flow through the API Gateway:

1. Frontend sends request to `http://localhost:9090`
2. Gateway validates CORS headers
3. Gateway validates JWT token (if protected endpoint)
4. Gateway routes to appropriate service
5. Service processes request and returns response
6. Gateway returns response to frontend

Authentication: JWT Bearer token in Authorization header.

### Internal Communication (Service-to-Service)

Services communicate via HTTP REST APIs:

- Booking Service → User Service (get customer/specialist data)
- All services → Notification Service (send emails)
- URLs via Docker Compose DNS: `http://<service>:<port>`
- Feign Client abstracts HTTP calls
- No direct database access between services

## Request Flows

### 1. Customer Books an Appointment

1. Frontend POST `/api/v1/bookings` to Gateway
2. Gateway validates JWT token
3. Gateway routes to booking-service
4. Booking Service:
   - Validates DTO (Bean Validation)
   - Calls User Service via Feign to get customer/specialist details
   - Creates Booking entity
   - Saves to MongoDB
   - Calls Notification Service to send confirmation email
5. Response returns through Gateway to Frontend

### 2. User Login

1. Frontend POST `/api/v1/auth/login` to Gateway
2. Gateway routes to auth-service (no auth required for this endpoint)
3. Auth Service:
   - Validates credentials against database
   - Checks account lock status
   - Generates JWT tokens (access + refresh)
   - Returns tokens
4. Frontend stores tokens and uses access token for subsequent requests

### 3. Customer Views Profile

1. Frontend GET `/api/v1/customers/{id}` to Gateway
2. Gateway validates JWT token
3. Gateway checks role (must be CUSTOMER)
4. Gateway routes to user-service
5. User Service:
   - Validates JWT
   - Queries MongoDB for user
   - Returns CustomerDto
6. Response returns through Gateway

## Service Dependencies

### Startup Order

Docker Compose handles startup order:

1. MongoDB (first)
2. Notification Service (no DB dependencies)
3. Auth Service (depends on MongoDB)
4. User Service (depends on MongoDB, Notification)
5. Booking Service (depends on MongoDB, User, Notification)
6. API Gateway (depends on all services)

### Runtime Dependencies

| Service | Depends On |
|---------|-----------|
| API Gateway | All other services (for routing) |
| Auth Service | MongoDB only |
| User Service | MongoDB, Notification Service |
| Booking Service | MongoDB, User Service (Feign), Notification Service |
| Notification Service | None (outbound only) |

## Data Flows

### User Registration

1. Frontend → Gateway → Auth Service
2. Auth Service creates Account in MongoDB
3. Auth Service calls Notification Service
4. Notification Service sends welcome email

### Booking Lifecycle

**Create:**
1. Frontend → Gateway → Booking Service
2. Booking Service creates record in MongoDB
3. Booking Service calls Notification Service (confirmation email)

**Update Status:**
1. Frontend → Gateway → Booking Service
2. Booking Service updates MongoDB
3. Booking Service calls Notification Service (status update email)

**Add Review:**
1. Frontend → Gateway → Booking Service
2. Booking Service creates Review in MongoDB (linked to booking)

### Profile Updates

**Customer:**
- Frontend → Gateway → User Service → MongoDB

**Specialist:**
- Frontend → Gateway → User Service → MongoDB
- May trigger admin approval workflow

## Error Handling

### Gateway Level

- **401 Unauthorized** — Invalid or missing JWT token
- **403 Forbidden** — Valid token but insufficient role
- **503 Service Unavailable** — Upstream service down

### Service Level

All services use standardized error handling via `common` module:

- **ResourceNotFoundException** — 404 Not Found
- **BadRequestException** — 400 Bad Request
- **ConflictException** — 409 Conflict
- **AccessDeniedException** — 403 Forbidden

Standard error response format with timestamp, message, and optional field errors.

### Feign Client Errors

When calling other services via Feign:
- Convert Feign exceptions to domain exceptions
- Never return null on failure (always throw)
- Propagate meaningful error messages

## Authentication Flow

### Token Lifecycle

1. **Login**: POST `/api/v1/auth/login`
   - Returns: accessToken, refreshToken, expiresIn

2. **Authenticated Requests**: Include header
   - `Authorization: Bearer <accessToken>`

3. **Token Expiry**: When access token expires (15 minutes)
   - POST `/api/v1/auth/refresh` with refreshToken
   - Returns: new accessToken + new refreshToken (rotation)

4. **Logout**: POST `/api/v1/auth/logout`
   - Invalidates refresh token
   - Client discards tokens

### Gateway Role Validation

Gateway extracts role from JWT and validates against endpoint requirements:

- `/api/v1/customers/**` — Requires CUSTOMER role
- `/api/v1/specialists/**` — Requires SPECIALIST role
- `/api/v1/admins/**` — Requires ADMIN role
- Public endpoints — No role required

## CORS Handling

CORS is handled **only** at the gateway:

- Configured via `ALLOWED_ORIGINS` environment variable
- Applied to all routes
- Credentials (cookies) allowed
- Standard HTTP methods enabled

**Important**: Never use `@CrossOrigin` on controller classes or methods.

## Communication Security

- All service-to-service communication happens inside Docker network
- No external exposure of individual services
- JWT tokens validated at gateway level
- Role-based access control enforced
- Rate limiting applied at gateway

## Links

- Gateway implementation: `backend/api-gateway/`
- Feign client: `backend/user-service-client/`
- Authentication: `backend/auth-service/`
- Security guidelines: [../standards/security.md](../standards/security.md)
- API design: [../standards/api-design.md](../standards/api-design.md)
