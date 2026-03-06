# Shared Libraries

Common modules shared across all backend services.

## Overview

Two shared modules provide common functionality:
- **common**: Core utilities, exceptions, JWT handling
- **user-service-client**: Feign client for user-service integration

## Common Module

Located at: `backend/common/`

### Purpose

Provides shared code that all services depend on:
- Base entity classes (Auditable)
- Common enums (Role)
- JWT utilities
- Exception hierarchy
- Standardized API responses
- Auto-configuration

### Key Components

#### Auditable Base Class

```java
@Data
public abstract class Auditable {
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;
}
```

Extend this for automatic timestamp tracking.

#### Role Enum

```java
public enum Role {
    CUSTOMER,
    SPECIALIST
}
```

#### JWT Utilities

`JwtUtils` provides token validation and parsing:

```java
@Component
public class JwtUtils {
    public Claims extractClaims(String token);
    public boolean isValid(String token);
    public SecretKey getSigningKey();
}
```

#### Exception Hierarchy

| Exception | HTTP Status | Use Case |
|-----------|-------------|----------|
| `ApiException` | - | Base class |
| `BadRequestException` | 400 | Invalid input |
| `ConflictException` | 409 | Resource conflict |
| `ResourceNotFoundException` | 404 | Not found |
| `AccountLockedException` | 403 | Locked account |
| `InvalidRefreshTokenException` | 401 | Invalid refresh token |

#### API Response Wrapper

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<FieldValidationError> errors;
}
```

Usage:
```java
return ResponseEntity.ok(ApiResponse.ok(data));
return ResponseEntity.ok(ApiResponse.ok("Message", data));
```

#### Global Exception Handler

`GlobalExceptionHandler` catches all exceptions and returns standardized error responses with proper HTTP status codes.

### Dependency

```kotlin
// In service build.gradle.kts
dependencies {
    implementation(project(":backend:common"))
}
```

## User Service Client Module

Located at: `backend/user-service-client/`

### Purpose

Feign client for inter-service communication with user-service. Used by booking-service to validate users.

### Components

#### Feign Client

```java
@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {
    @GetMapping("/api/v1/customers/{id}")
    CustomerResponse getCustomerById(@PathVariable("id") String id);

    @GetMapping("/api/v1/specialists/{id}")
    SpecialistResponse getSpecialistById(@PathVariable("id") String id);
}
```

#### DTOs

**CustomerRequest / CustomerResponse:**
- `email`: String
- `name`: String
- `phone`: String
- `address`: String
- `preferredPaymentMethod`: String

**SpecialistRequest / SpecialistResponse:**
- `email`: String
- `name`: String
- `phone`: String
- `address`: String
- `title`: String
- `services`: List<String>
- `hourlyRate`: BigDecimal
- `verified`: Boolean

### Auto-Configuration

Enabled via `spring.factories`:
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.snapserve.userclient.config.UserServiceClientAutoConfiguration
```

Services using this client must add the dependency and configure `user.service.url`.

### Dependency

```kotlin
// In service build.gradle.kts
dependencies {
    implementation(project(":backend:user-service-client"))
}
```

### Usage

```java
@Service
@RequiredArgsConstructor
public class BookingService {
    private final UserServiceClient userServiceClient;
    
    public void createBooking(BookingDto dto) {
        // Validate customer exists
        CustomerResponse customer = userServiceClient.getCustomerById(dto.getCustomerId());
        // ...
    }
}
```

## Building

```bash
# Build common module
./gradlew :backend:common:build

# Build user-service-client
./gradlew :backend:user-service-client:build

# Publish to local Maven (if needed by other projects)
./gradlew :backend:common:publishToMavenLocal
./gradlew :backend:user-service-client:publishToMavenLocal
```

## Best Practices

1. **Keep common lean**: Only truly shared code belongs here
2. **Version carefully**: Changes affect all services
3. **No service-specific code**: This is a library, not a service
4. **Document breaking changes**: Use semantic versioning
5. **Test thoroughly**: Bugs here affect everything
