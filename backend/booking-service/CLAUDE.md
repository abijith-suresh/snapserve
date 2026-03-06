# Booking Service Agent Context

Manages appointments and reviews between customers and specialists. Port: 9002.

## Quick Commands

```bash
# Build
./gradlew :backend:booking-service:build

# Test
./gradlew :backend:booking-service:test

# Docker
docker compose up -d booking-service

# Test endpoint
curl http://localhost:9002/api/v1/bookings
```

## Key Files

| File | Purpose |
|------|---------|
| `BookingServiceApplication.java` | Entry point |
| `controller/BookingController.java` | Booking CRUD |
| `controller/ReviewController.java` | Review CRUD |
| `service/BookingService.java` | Booking logic |
| `service/ReviewService.java` | Review logic |
| `model/Booking.java` | Booking document |
| `model/Review.java` | Review document |
| `dto/*` | Request/response DTOs |

## API Endpoints

**Bookings:**
- `GET /api/v1/bookings` - List all
- `GET /api/v1/bookings/{id}` - Get by ID
- `POST /api/v1/bookings` - Create
- `PUT /api/v1/bookings/{id}` - Update
- `DELETE /api/v1/bookings/{id}` - Delete

**Reviews:**
- `GET /api/v1/reviews` - List all
- `GET /api/v1/reviews/{id}` - Get by ID
- `POST /api/v1/reviews` - Create
- `PUT /api/v1/reviews/{id}` - Update
- `DELETE /api/v1/reviews/{id}` - Delete

## Models

**Booking:**
```java
@Document(collection = "booking")
public class Booking {
    private ObjectId customerId;
    private ObjectId specialistId;
    private LocalDateTime appointmentTime;
    private String service;
    private String status;  // PENDING, CONFIRMED, COMPLETED, CANCELLED
}
```

**Review:**
```java
@Document(collection = "reviews")
public class Review {
    private ObjectId customerId;
    private ObjectId specialistId;
    private Integer rating;  // 1-5
    private String comment;
    private LocalDate createdAt;
}
```

## Service-Specific Rules

1. **Validate users**: Use `UserServiceClient` to verify customer/specialist exist.
2. **Status flow**: PENDING → CONFIRMED → COMPLETED (or CANCELLED).
3. **Feign client**: Add `user-service-client` dependency for inter-service calls.
4. **One review per booking**: Enforce at service layer.
5. **Notification integration**: Call notification-service on booking create/update.

## Environment Variables

```yaml
MONGODB_URI: MongoDB connection string
USER_SERVICE_URL: http://user-service:9001
NOTIFICATION_SERVICE_URL: http://notification-service:9003
```

## Dependencies

```kotlin
dependencies {
    implementation(project(":backend:common"))
    implementation(project(":backend:user-service-client"))
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
}
```

## Full Documentation

- [Booking Service Docs](../../docs/backend/booking-service.md)
- [Architecture Overview](../../AGENTS.md)

## Port

9002
