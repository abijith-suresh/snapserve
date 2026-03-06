# Auth Service Agent Context

JWT-based authentication service. Handles registration, login, token refresh, and logout. Port: 9000.

## Quick Commands

```bash
# Build
./gradlew :backend:auth-service:build

# Test
./gradlew :backend:auth-service:test

# Docker
docker compose up -d auth-service

# Test login
curl -X POST http://localhost:9000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret"}'
```

## Key Files

| File | Purpose |
|------|---------|
| `AuthServiceApplication.java` | Entry point |
| `controller/AccountController.java` | Auth REST endpoints |
| `service/AccountService.java` | Login/register logic |
| `service/RefreshTokenService.java` | Token refresh handling |
| `config/JwtTokenProvider.java` | JWT generation/validation |
| `config/SecurityConfig.java` | Spring Security setup |
| `model/Account.java` | User credentials |
| `model/RefreshToken.java` | Refresh token storage |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Create account |
| POST | `/api/v1/auth/login` | Authenticate, get tokens |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| POST | `/api/v1/auth/logout` | Invalidate refresh token |

## Auth Response

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbG...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4=",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  }
}
```

## Service-Specific Rules

1. **Password hashing**: Use BCrypt (strength 10+).
2. **JWT secret**: Must be ≥64 chars from `JWT_SECRET` env var.
3. **Refresh tokens**: Store hashed in MongoDB with device ID.
4. **Rate limiting**: To be added (use bucket4j or similar).
5. **Device tracking**: Track X-Device-Id and X-Real-IP headers.
6. **No CORS**: CORS handled at gateway only.

## Environment Variables

```yaml
MONGODB_URI: MongoDB connection string
JWT_SECRET: Signing key (≥64 characters)
```

## Dependencies

```kotlin
dependencies {
    implementation(project(":backend:common"))
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.jsonwebtoken:jjwt")
}
```

## Full Documentation

- [Auth Service Docs](../../docs/backend/auth-service.md) (create if needed)
- [Architecture Overview](../../AGENTS.md)

## Port

9000
