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
- **Kebab-case** for multi-word resources: `/user-profiles`
- **Avoid verbs** in URLs: Use HTTP methods instead

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
GET /api/v1/users/{userId}/bookings          # User's bookings
GET /api/v1/specialists/{id}/reviews         # Specialist's reviews
POST /api/v1/bookings/{id}/reviews           # Add review to booking
```

## HTTP Methods

### GET

Read operations, never modify data:

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable String id) {
    UserDto user = userService.getUser(id);
    return ResponseEntity.ok(ApiResponse.success(user));
}
```

### POST

Create new resources:

```java
@PostMapping
public ResponseEntity<ApiResponse<UserDto>> createUser(
        @Valid @RequestBody CreateUserRequest request) {
    UserDto user = userService.createUser(request);
    return ResponseEntity.status(201).body(ApiResponse.success(user));
}
```

### PUT

Full resource updates (all fields):

```java
@PutMapping("/{id}")
public ResponseEntity<ApiResponse<UserDto>> updateUser(
        @PathVariable String id,
        @Valid @RequestBody UpdateUserRequest request) {
    UserDto user = userService.updateUser(id, request);
    return ResponseEntity.ok(ApiResponse.success(user));
}
```

### PATCH

Partial updates:

```java
@PatchMapping("/{id}")
public ResponseEntity<ApiResponse<UserDto>> patchUser(
        @PathVariable String id,
        @Valid @RequestBody PatchUserRequest request) {
    UserDto user = userService.patchUser(id, request);
    return ResponseEntity.ok(ApiResponse.success(user));
}
```

### DELETE

Remove resources:

```java
@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(ApiResponse.success(null));
}
```

## Response Format

### Success Response

All successful responses use `ApiResponse<T>` wrapper:

```json
{
  "success": true,
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "email": "user@example.com",
    "name": "John Doe",
    "role": "CUSTOMER"
  },
  "message": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### List Response

```json
{
  "success": true,
  "data": [
    { "id": "1", "name": "User 1" },
    { "id": "2", "name": "User 2" }
  ],
  "message": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response

```json
{
  "success": false,
  "data": null,
  "message": "User not found",
  "timestamp": "2024-01-15T10:30:00Z",
  "errors": [
    {
      "field": "email",
      "message": "Email already exists"
    }
  ]
}
```

## HTTP Status Codes

| Code | Usage |
|------|-------|
| 200 OK | Successful GET, PUT, PATCH, DELETE |
| 201 Created | Successful POST (resource created) |
| 204 No Content | Successful DELETE (no body) |
| 400 Bad Request | Validation errors, malformed request |
| 401 Unauthorized | Missing or invalid JWT token |
| 403 Forbidden | Valid token but insufficient permissions |
| 404 Not Found | Resource doesn't exist |
| 409 Conflict | Resource conflict (e.g., duplicate email) |
| 422 Unprocessable | Business logic violation |
| 500 Internal Error | Server error |

## Request/Response Examples

### Authentication

**Login:**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!"
}

Response 200:
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g...",
    "expiresIn": 900
  },
  "message": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Refresh Token:**
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g..."
}

Response 200:
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "newRefreshToken...",
    "expiresIn": 900
  },
  ...
}
```

### User Management

**Create User:**
```http
POST /api/v1/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "newuser@example.com",
  "name": "Jane Doe",
  "role": "CUSTOMER"
}

Response 201:
{
  "success": true,
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "email": "newuser@example.com",
    "name": "Jane Doe",
    "role": "CUSTOMER",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  ...
}
```

**Validation Error:**
```http
POST /api/v1/users
Content-Type: application/json

{
  "email": "invalid-email",
  "name": "",
  "role": null
}

Response 400:
{
  "success": false,
  "data": null,
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00Z",
  "errors": [
    { "field": "email", "message": "Invalid email format" },
    { "field": "name", "message": "Name is required" },
    { "field": "role", "message": "Role is required" }
  ]
}
```

## Pagination

Use cursor-based pagination for large datasets:

```http
GET /api/v1/users?page=0&size=20&sort=name,asc
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 150,
    "totalPages": 8,
    "last": false
  }
}
```

## Filtering

Use query parameters for filtering:

```http
GET /api/v1/specialists?category=plumbing&available=true
GET /api/v1/bookings?status=PENDING&from=2024-01-01&to=2024-01-31
```

## Search

Use `q` parameter for search:

```http
GET /api/v1/specialists?q=john&category=plumbing
```

## Sorting

Use `sort` parameter:

```http
GET /api/v1/users?sort=name,asc
GET /api/v1/bookings?sort=createdAt,desc
```

## Idempotency

POST requests should support idempotency for retries:

```http
POST /api/v1/bookings
Idempotency-Key: unique-request-id
```

## Rate Limiting

API Gateway implements rate limiting:

```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 99
X-RateLimit-Reset: 1640995200
```

## Versioning

- Current version: **v1**
- Version in URL: `/api/v1/...`
- Breaking changes require new version (v2)
- Non-breaking changes stay in current version

## API Documentation

Use SpringDoc OpenAPI for automatic documentation:

```java
@Operation(summary = "Create a new user")
@ApiResponse(responseCode = "201", description = "User created")
@ApiResponse(responseCode = "400", description = "Validation error")
@PostMapping
public ResponseEntity<ApiResponse<UserDto>> createUser(...) { ... }
```

Access at: `http://localhost:9090/swagger-ui.html`

## Best Practices

1. **Consistent naming** across all endpoints
2. **Use DTOs** for all request/response bodies
3. **Validate input** with Bean Validation
4. **Handle errors** gracefully with proper status codes
5. **Return ApiResponse** wrapper for all responses
6. **Document with OpenAPI** annotations
7. **Version your APIs** from day one
8. **Use proper HTTP methods** (don't use POST for everything)
9. **Return 201 for POST** that creates resources
10. **Include Location header** on 201 responses
