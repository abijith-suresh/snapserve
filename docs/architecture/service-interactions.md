# Service Interactions

This document describes how the microservices communicate with each other.

## Communication Patterns

### External Communication (Frontend ↔ API)

```
Frontend (Port 3000)
    │
    │ HTTP/REST + JSON
    │ Authorization: Bearer <JWT>
    ▼
API Gateway (Port 9090)
    │
    │ 1. CORS handling
    │ 2. JWT validation
    │ 3. Route to service
    ▼
Target Service
```

All frontend requests go through the API Gateway:
- Base URL: `http://localhost:9090`
- Authentication: JWT Bearer token required for protected routes
- Content-Type: `application/json`
- Response Format: `ApiResponse<T>` wrapper

### Internal Communication (Service-to-Service)

Services communicate via REST APIs using OpenFeign:

```
Booking Service needs customer data:

Booking Service ──Feign──► User Service Client ──HTTP──► User Service
                                    │
                                    ▼
                            Returns CustomerDto
```

**Key Points:**
- Direct HTTP calls between services (not through gateway)
- URLs via Docker Compose DNS: `http://user-service:9001`
- Feign client defined in `user-service-client` module
- Services don't share databases

## Request Flow Examples

### 1. Customer Books an Appointment

```
1. Frontend POST /api/v1/bookings
   │
   ▼
2. Gateway validates JWT, routes to booking-service
   │
   ▼
3. Booking Service:
   ├── Validates DTO (Bean Validation)
   ├── Calls User Service via Feign to get customer/specialist
   ├── Creates Booking entity
   └── Saves to MongoDB
   │
   ▼
4. Booking Service calls Notification Service
   │
   ▼
5. Notification Service sends email
   │
   ▼
6. Response returns through Gateway to Frontend
```

### 2. User Login

```
1. Frontend POST /api/v1/auth/login
   │
   ▼
2. Gateway routes to auth-service (no auth required)
   │
   ▼
3. Auth Service:
   ├── Validates credentials
   ├── Checks account lock status
   ├── Generates JWT tokens (access + refresh)
   └── Returns tokens
   │
   ▼
4. Frontend stores tokens (Zustand persist)
```

### 3. Customer Views Profile

```
1. Frontend GET /api/v1/customers/{id}
   │
   ▼
2. Gateway validates JWT, checks role=customer
   │
   ▼
3. Gateway routes to user-service
   │
   ▼
4. User Service:
   ├── Validates JWT (via common module)
   ├── Queries MongoDB
   └── Returns CustomerDto
   │
   ▼
5. Response returns through Gateway
```

## Service Dependencies

### Startup Order (Docker Compose)

```
MongoDB (first)
    │
    ├──► Notification Service (no DB deps)
    │
    ├──► Auth Service (depends on MongoDB)
    │
    ├──► User Service (depends on MongoDB, Notification)
    │
    └──► Booking Service (depends on MongoDB, User, Notification)
         │
         └──► API Gateway (depends on all services)
```

### Runtime Dependencies

| Service | Depends On |
|---------|-----------|
| API Gateway | All other services for routing |
| Auth Service | MongoDB only |
| User Service | MongoDB, Notification Service |
| Booking Service | MongoDB, User Service (Feign), Notification Service |
| Notification Service | None (outbound only) |

## Data Flow

### User Registration

```
Frontend ──► Gateway ──► Auth Service
                              │
                              ├──► Creates Account in MongoDB
                              │
                              └──► Calls Notification Service
                                        │
                                        └──► Sends welcome email
```

### Booking Lifecycle

```
1. CREATE
   Frontend ──► Booking Service ──► MongoDB
                    │
                    └──► Notification Service ──► Email

2. UPDATE STATUS
   Frontend ──► Booking Service ──► MongoDB
                    │
                    └──► Notification Service ──► Email

3. REVIEW
   Frontend ──► Booking Service ──► MongoDB (Review collection)
```

### Profile Updates

```
Customer:
  Frontend ──► Gateway ──► User Service ──► MongoDB

Specialist:
  Frontend ──► Gateway ──► User Service ──► MongoDB
                              │
                              └──► Validation & approval workflow
```

## Error Handling

### Gateway Level

```
Invalid JWT ──► 401 Unauthorized
Missing role ──► 403 Forbidden
Service down ──► 503 Service Unavailable
```

### Service Level

All services use the `GlobalExceptionHandler` from `common`:

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(...) {
    return ResponseEntity.status(404).body(errorResponse);
}
```

**Standard Error Response:**
```json
{
  "status": 404,
  "message": "Customer not found",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/v1/customers/123"
}
```

### Feign Client Errors

When User Service Client fails:

```java
// Booking Service
try {
    CustomerDto customer = userServiceClient.getCustomerById(id);
} catch (FeignException.NotFound e) {
    throw new ResourceNotFoundException("Customer not found");
}
```

## Authentication Flow

```
1. Login:
   POST /api/v1/auth/login
   Response: { accessToken, refreshToken }

2. Protected Request:
   GET /api/v1/customers/123
   Header: Authorization: Bearer <accessToken>
   
   Gateway validates JWT signature and expiry
   
3. Token Refresh:
   POST /api/v1/auth/refresh
   Body: { refreshToken }
   Response: { accessToken, refreshToken }

4. Logout:
   POST /api/v1/auth/logout
   Header: Authorization: Bearer <accessToken>
   Invalidates refresh token
```

## CORS Configuration

CORS is handled **only** at the gateway:

```java
// Gateway configuration
registry.addMapping("/api/**")
    .allowedOrigins("http://localhost:3000")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
    .allowedHeaders("*")
    .allowCredentials(true);
```

**Important:** No @CrossOrigin annotations on any controller.
