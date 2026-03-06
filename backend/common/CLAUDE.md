# Common Module Agent Context

Shared library for all backend services. Contains utilities, exceptions, JWT handling, and base classes.

## Quick Commands

```bash
# Build
./gradlew :backend:common:build

# Publish locally (for external use)
./gradlew :backend:common:publishToMavenLocal

# Test
./gradlew :backend:common:test
```

## Key Files

| File | Purpose |
|------|---------|
| `jwt/JwtUtils.java` | JWT validation/claims extraction |
| `model/Auditable.java` | Base class with createdAt/updatedAt |
| `model/Role.java` | CUSTOMER, SPECIALIST enum |
| `exception/*.java` | Exception hierarchy |
| `response/ApiResponse.java` | Standardized response wrapper |
| `response/ErrorResponse.java` | Error response structure |
| `handler/GlobalExceptionHandler.java` | Global exception handling |
| `config/CommonAutoConfiguration.java` | Auto-config |

## Using This Module

Add to a service's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":backend:common"))
}
```

## Key Components

### JwtUtils

```java
@Component
public class JwtUtils {
    public Claims extractClaims(String token);
    public boolean isValid(String token);
}
```

### Auditable Base Class

```java
@Data
public abstract class Auditable {
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;
}
```

Extend for automatic timestamp tracking.

### Exception Hierarchy

- `ApiException` - Base class
- `BadRequestException` - 400
- `ConflictException` - 409
- `ResourceNotFoundException` - 404
- `AccountLockedException` - 403
- `InvalidRefreshTokenException` - 401

### ApiResponse Wrapper

```java
// Success
return ResponseEntity.ok(ApiResponse.ok(data));
return ResponseEntity.ok(ApiResponse.ok("Message", data));

// Created
return ResponseEntity.status(HttpStatus.CREATED)
    .body(ApiResponse.ok("Created", data));
```

## Service-Specific Rules

1. **No service code**: Only truly shared utilities.
2. **No Spring Boot**: Just Spring, so it can be used in libraries.
3. **Document changes**: Breaking changes affect all services.
4. **Test thoroughly**: Bugs here break everything.
5. **Keep it lean**: Don't bloat with rarely-used code.

## Auto-Configuration

Enabled via `resources/META-INF/spring.factories`:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.snapserve.common.config.CommonAutoConfiguration
```

## Full Documentation

- [Shared Libraries Docs](../../docs/backend/shared-libraries.md)
- [Architecture Overview](../../AGENTS.md)

## Note

This is a library module, not a service. No main class, no port.
