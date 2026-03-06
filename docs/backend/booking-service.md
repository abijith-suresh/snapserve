# Booking Service

Manages appointments and reviews between customers and specialists. Port: 9002.

## Overview

The Booking Service coordinates service appointments and customer feedback:
- Create and manage bookings
- Track booking status (pending, confirmed, completed, cancelled)
- Customer reviews and ratings for specialists
- Specialist review aggregation

## Architecture

Spring Boot application with MongoDB. Uses Feign client (`user-service-client`) to validate users exist before creating bookings.

## Key Entities

### Booking

| Field | Type | Description |
|-------|------|-------------|
| `id` | ObjectId | Primary key |
| `customerId` | ObjectId | Customer reference |
| `specialistId` | ObjectId | Specialist reference |
| `appointmentTime` | LocalDateTime | Scheduled time |
| `service` | String | Service type |
| `status` | String | Booking status |

### Review

| Field | Type | Description |
|-------|------|-------------|
| `id` | ObjectId | Primary key |
| `customerId` | ObjectId | Reviewer reference |
| `specialistId` | ObjectId | Reviewee reference |
| `rating` | Integer | 1-5 star rating |
| `comment` | String | Review text |
| `createdAt` | LocalDate | Review date |

## API Endpoints

### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/bookings` | List all bookings |
| GET | `/api/v1/bookings/{id}` | Get booking by ID |
| POST | `/api/v1/bookings` | Create booking |
| PUT | `/api/v1/bookings/{id}` | Update booking |
| DELETE | `/api/v1/bookings/{id}` | Delete booking |

### Reviews

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/reviews` | List all reviews |
| GET | `/api/v1/reviews/{id}` | Get review by ID |
| POST | `/api/v1/reviews` | Create review |
| PUT | `/api/v1/reviews/{id}` | Update review |
| DELETE | `/api/v1/reviews/{id}` | Delete review |

## DTOs

### AddBookingDto
- `customerId`: String
- `specialistId`: String  
- `appointmentTime`: LocalDateTime
- `service`: String
- `status`: String

### BookingResponseDto
Full booking details with user info resolved via UserServiceClient.

### ReviewDto
- `customerId`: String
- `specialistId`: String
- `rating`: Integer
- `comment`: String

### SpecialistReviewResponseDto
Aggregated review data for a specialist including average rating.

## Configuration

```yaml
# application.yml
server:
  port: 9002

spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}

user:
  service:
    url: ${USER_SERVICE_URL:http://user-service:9001}

notification:
  service:
    url: ${NOTIFICATION_SERVICE_URL:http://notification-service:9003}
```

## Key Classes

| Class | Purpose |
|-------|---------|
| `Booking` | Booking document model |
| `Review` | Review document model |
| `BookingRepository` | Booking data access |
| `ReviewRepository` | Review data access |
| `BookingService` | Booking business logic |
| `ReviewService` | Review business logic |
| `BookingController` | Booking REST endpoints |
| `ReviewController` | Review REST endpoints |

## Integration

- **User Service**: Validates customer/specialist exist via Feign client
- **Notification Service**: Sends confirmation emails for new bookings

## Booking Status Flow

```
PENDING → CONFIRMED → COMPLETED
   ↓
CANCELLED
```

Statuses are stored as strings for flexibility.

## Building

```bash
# Build this service only
./gradlew :backend:booking-service:build

# Run tests
./gradlew :backend:booking-service:test
```

## Error Handling

Uses common exception classes:
- `ResourceNotFoundException`: Booking/review not found
- `BadRequestException`: Invalid booking data

Returns standardized `ApiResponse` wrapper for all responses.
