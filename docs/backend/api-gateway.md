# API Gateway

Entry point for all client requests. Handles routing, authentication, CORS, and rate limiting.

## Purpose

The API Gateway sits between the frontend and microservices:
- Routes requests to appropriate services
- Validates JWT tokens on protected routes
- Handles cross-origin requests (CORS)
- Applies rate limiting
- Provides single entry point for all APIs

## Responsibilities

### Routing

Routes incoming requests to the correct microservice based on URL path:

- `/api/v1/auth/**` → Auth Service (port 9000)
- `/api/v1/customers/**` → User Service (port 9001)
- `/api/v1/specialists/**` → User Service (port 9001)
- `/api/v1/bookings/**` → Booking Service (port 9002)
- `/api/v1/reviews/**` → Booking Service (port 9002)
- `/api/v1/notifications/**` → Notification Service (port 9003)

Configuration in: `backend/api-gateway/src/main/resources/application.yml`

### Authentication

Validates JWT tokens on all protected routes:
- Extracts token from Authorization header
- Validates signature and expiration
- Extracts user ID and role from token
- Adds user info to request headers for downstream services

Public endpoints (no auth required):
- POST `/api/v1/auth/login`
- POST `/api/v1/auth/register`
- POST `/api/v1/auth/refresh`

### Authorization

Checks user roles for endpoint access:
- Customer endpoints require CUSTOMER role
- Specialist endpoints require SPECIALIST role
- Admin endpoints require ADMIN role

Role validation happens after token validation.

### CORS

Handles cross-origin requests from frontend:
- Configured via `ALLOWED_ORIGINS` environment variable
- Applied to all routes uniformly
- Credentials (cookies) allowed

**Important**: Never add `@CrossOrigin` annotations to controllers in other services.

### Rate Limiting

Prevents API abuse:
- Default: 100 requests per minute per client
- Returns 429 status when limit exceeded
- Includes rate limit headers in responses

## Architecture

Request flow through gateway:

1. **CORS Filter** — Handle preflight and origin checks
2. **JWT Authentication Filter** — Validate token (if required)
3. **Role Authorization Filter** — Check role permissions
4. **Routing** — Forward to appropriate service
5. **Response** — Return service response to client

## Configuration

Key configuration files:
- `application.yml` — Routes, JWT settings, CORS origins
- `RouteValidator.java` — Public endpoint definitions
- Security filter configuration

Environment variables:
- `JWT_SECRET` — Secret for token validation
- `ALLOWED_ORIGINS` — CORS allowed origins

## Dependencies

- Spring Cloud Gateway — Routing and filtering
- JJWT — Token parsing and validation
- Spring Boot Actuator — Health checks

See `backend/api-gateway/build.gradle.kts` for complete list.

## Error Handling

### Invalid Token

Returns 401 Unauthorized when:
- Token missing on protected endpoint
- Token expired
- Token signature invalid

### Insufficient Permissions

Returns 403 Forbidden when:
- Valid token but wrong role for endpoint
- Attempting to access another user's data

### Service Unavailable

Returns 503 when upstream service is down or not responding.

## Testing

### Local Testing

Start services:
```bash
docker compose up
```

Test public endpoint:
```bash
curl http://localhost:9090/api/v1/auth/login \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'
```

Test protected endpoint (should return 401 without token):
```bash
curl http://localhost:9090/api/v1/customers/123
```

Test with valid token:
```bash
curl http://localhost:9090/api/v1/customers/123 \
  -H "Authorization: Bearer <valid-token>"
```

## Best Practices

1. **Keep gateway lean** — No business logic, only routing and auth
2. **Stateless** — No session storage in gateway
3. **Validate early** — Reject bad requests before routing
4. **Log minimally** — Don't log sensitive headers or tokens
5. **Health checks** — Implement `/actuator/health` endpoint
6. **Timeouts** — Set reasonable timeouts for upstream services

## Links

- Gateway source: `backend/api-gateway/`
- Route configuration: `backend/api-gateway/src/main/resources/application.yml`
- Security filters: `backend/api-gateway/src/main/java/.../config/`
- Architecture overview: [../architecture/overview.md](../architecture/overview.md)
- Security guidelines: [../standards/security.md](../standards/security.md)
