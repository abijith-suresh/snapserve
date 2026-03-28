# API Design Standards

REST API design conventions for all SnapServe microservices.

## URL Structure

### Base Path

All APIs use `/api/v1` prefix:
```
/api/v1/auth/login
/api/v1/users
/api/v1/bookings
```

### Resource Naming

- **Plural nouns** for collections: `/users`, `/bookings`, `/specialists`
- **Kebab-case** for multi-word resources
- **Avoid verbs** in URLs — use HTTP methods instead

### URL Patterns

| Operation | Pattern | Example |
|-----------|---------|---------|
| List | GET /resources | `GET /api/v1/users` |
| Get one | GET /resources/{id} | `GET /api/v1/users/123` |
| Create | POST /resources | `POST /api/v1/users` |
| Update | PUT /resources/{id} | `PUT /api/v1/users/123` |
| Partial update | PATCH /resources/{id} | `PATCH /api/v1/users/123` |
| Delete | DELETE /resources/{id} | `DELETE /api/v1/users/123` |

### Nested Resources

```
GET /api/v1/users/{userId}/bookings
GET /api/v1/specialists/{id}/reviews
POST /api/v1/bookings/{id}/reviews
```

## HTTP Methods

- **GET**: Read operations, never modify data
- **POST**: Create new resources (returns 201)
- **PUT**: Full resource updates
- **PATCH**: Partial updates
- **DELETE**: Remove resources

## Response Format

### Success Response

All successful responses use `ApiResponse<T>` wrapper:

Structure:
- `success`: true
- `data`: Response payload (object, array, or null)
- `message`: Error message (null for success)
- `timestamp`: ISO 8601 timestamp
- `errors`: Array of field errors (if validation fails)

### Error Response

Same structure with `success: false` and appropriate message.

## HTTP Status Codes

| Code | Usage |
|------|-------|
| 200 OK | Successful GET, PUT, PATCH, DELETE |
| 201 Created | Successful POST |
| 204 No Content | Successful DELETE with no body |
| 400 Bad Request | Validation errors, malformed request |
| 401 Unauthorized | Missing or invalid JWT token |
| 403 Forbidden | Valid token but insufficient permissions |
| 404 Not Found | Resource doesn't exist |
| 409 Conflict | Resource conflict (e.g., duplicate email) |
| 422 Unprocessable | Business logic violation |
| 500 Internal Error | Server error |

## Request Patterns

### Pagination

Use query parameters:
```
GET /api/v1/users?page=0&size=20&sort=name,asc
```

### Filtering

```
GET /api/v1/specialists?category=plumbing&available=true
GET /api/v1/bookings?status=PENDING&from=2024-01-01
```

### Search

```
GET /api/v1/specialists?q=john&category=plumbing
```

### Sorting

```
GET /api/v1/users?sort=name,asc
GET /api/v1/bookings?sort=createdAt,desc
```

## Authentication

### JWT Tokens

**Access Token**:
- Short-lived (15 minutes)
- Sent in header: `Authorization: Bearer <token>`
- Contains: user ID, email, role

**Refresh Token**:
- Long-lived (7 days)
- Single-use (rotated on each use)
- Sent in request body for refresh endpoint

### Public Endpoints

These don't require authentication:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/refresh`

All other endpoints require valid JWT token.

## API Documentation

API documentation is auto-generated via **OpenAPI/SpringDoc**.

**Access**: `http://localhost:9090/swagger-ui.html`

Annotations to use:
- `@Operation` — Endpoint description
- `@ApiResponse` — Response codes and descriptions
- `@Parameter` — Parameter descriptions

## Best Practices

1. **Consistent naming** — Use same terms across all endpoints
2. **Use DTOs** — All request/response bodies use DTOs
3. **Validate input** — Bean Validation on all DTOs
4. **Handle errors** — Graceful error handling with proper status codes
5. **Return ApiResponse** — Wrapper for all responses
6. **Version APIs** — `/api/v1/` from day one
7. **Use proper HTTP methods** — Don't use POST for everything
8. **Return 201 for POST** — That creates resources
9. **Include Location header** — On 201 responses when applicable
10. **Idempotency** — POST should support idempotency keys for retries

## Rate Limiting

API Gateway implements rate limiting:

Headers returned:
- `X-RateLimit-Limit`: Maximum requests allowed
- `X-RateLimit-Remaining`: Requests remaining in window
- `X-RateLimit-Reset`: Timestamp when limit resets

Status code 429 returned when limit exceeded.

## CORS

CORS is handled **only** at the gateway. Never use `@CrossOrigin` on controllers.

Allowed origins configured via `ALLOWED_ORIGINS` environment variable.

## Links

- OpenAPI annotations: See `backend/user-service` controllers for examples
- JWT validation: See `backend/api-gateway` security configuration
- DTO examples: See `backend/*/src/main/java/.../dto/`
