# API Gateway Agent Context

Entry point for all client requests. Handles routing, JWT validation, and CORS. Port: 9090.

## Quick Commands

```bash
# Build
./gradlew :backend:api-gateway:build

# Test
./gradlew :backend:api-gateway:test

# Docker
docker compose up -d api-gateway

# Health check
curl http://localhost:9090/actuator/health
```

## Key Files

| File | Purpose |
|------|---------|
| `GatewayServiceApplication.java` | Spring Boot entry point |
| `filter/AuthenticationInterceptor.java` | JWT validation |
| `filter/RouteValidator.java` | Public route whitelist |
| `config/WebMvcConfig.java` | Interceptor registration |
| `config/GlobalCorsConfig.java` | CORS configuration |
| `application.yml` | Service routing config |

## Service Routing

Routes configured via `@Value` from env vars:

```yaml
auth:
  service:
    url: ${AUTH_SERVICE_URL}
user:
  service:
    url: ${USER_SERVICE_URL}
booking:
  service:
    url: ${BOOKING_SERVICE_URL}
notification:
  service:
    url: ${NOTIFICATION_SERVICE_URL}
```

## Public Endpoints

No JWT required:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/validate/token`

All other routes require valid JWT in `Authorization: Bearer <token>` header.

## Service-Specific Rules

1. **Single CORS config**: All CORS lives here. Never add `@CrossOrigin` to controllers.
2. **JWT at gateway only**: Downstream services trust requests (internal network).
3. **Route validation**: Use `RouteValidator.isSecured(path)` to check if JWT required.
4. **No business logic**: Pure routing and auth only.
5. **Docker DNS**: Use service names (`http://auth-service:9000`) in Docker, `localhost` for local dev.

## Dependencies

```kotlin
dependencies {
    implementation(project(":backend:common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // For forwarding
}
```

## Full Documentation

- [API Gateway Docs](../../docs/backend/api-gateway.md)
- [Architecture Overview](../../AGENTS.md)

## Port

9090 (external), connects to internal services on 9000-9003
