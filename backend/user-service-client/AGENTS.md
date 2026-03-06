# User Service Client Agent Context

Feign client library for inter-service communication with user-service.

## Quick Commands

```bash
# Build
./gradlew :backend:user-service-client:build

# Publish locally
./gradlew :backend:user-service-client:publishToMavenLocal
```

## Key Files

| File | Purpose |
|------|---------|
| `client/UserServiceClient.java` | Feign client interface |
| `dto/customer/CustomerRequest.java` | Customer request DTO |
| `dto/customer/CustomerResponse.java` | Customer response DTO |
| `dto/specialist/SpecialistRequest.java` | Specialist request DTO |
| `dto/specialist/SpecialistResponse.java` | Specialist response DTO |
| `config/UserServiceClientAutoConfiguration.java` | Auto-config |

## Using This Module

Add to a service's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":backend:user-service-client"))
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
}
```

Enable Feign:

```java
@SpringBootApplication
@EnableFeignClients
public class BookingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
```

Configure URL:

```yaml
user:
  service:
    url: ${USER_SERVICE_URL:http://user-service:9001}
```

## API

```java
@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {
    
    @GetMapping("/api/v1/customers/{id}")
    CustomerResponse getCustomerById(@PathVariable("id") String id);
    
    @GetMapping("/api/v1/specialists/{id}")
    SpecialistResponse getSpecialistById(@PathVariable("id") String id);
}
```

## DTOs

**Customer:**
- email, name, phone, address, preferredPaymentMethod

**Specialist:**
- email, name, phone, address, title, services, hourlyRate, verified

## Service-Specific Rules

1. **Mirror user-service API**: Keep DTOs in sync.
2. **Use records**: For immutable DTOs (Java 21).
3. **Validation annotations**: Match user-service validation.
4. **Error handling**: Feign errors propagate to caller.
5. **Circuit breaker**: Consider Resilience4j for production.

## Example Usage

```java
@Service
@RequiredArgsConstructor
public class BookingService {
    private final UserServiceClient userServiceClient;
    
    public Booking createBooking(BookingDto dto) {
        // Validate customer exists
        CustomerResponse customer = userServiceClient.getCustomerById(dto.getCustomerId());
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found");
        }
        // ... create booking
    }
}
```

## Full Documentation

- [Shared Libraries Docs](../../docs/backend/shared-libraries.md)
- [Architecture Overview](../../AGENTS.md)

## Note

This is a library module, not a service. No main class, no port.
