# Booking Service

Manages bookings, reviews, and complaints.

## Purpose

Core business logic for the platform:
- Create and manage service bookings
- Handle booking status transitions
- Customer reviews for completed bookings
- Complaint handling

## API Endpoints

### Booking Endpoints

**POST /api/v1/bookings**
- Create new booking
- Access: Customer
- Validates: Customer and specialist exist, time slot available

**GET /api/v1/bookings/{id}**
- Get booking details
- Access: Booking participants (customer or specialist)

**GET /api/v1/bookings**
- List bookings
- Query params: status, from, to, customerId, specialistId
- Access: Customer (own bookings) or Specialist (own bookings)

**PUT /api/v1/bookings/{id}**
- Update booking details
- Access: Customer (if status allows)

**PATCH /api/v1/bookings/{id}/status**
- Update booking status
- Access: Specialist or Admin
- Valid transitions: PENDING → CONFIRMED → COMPLETED

**DELETE /api/v1/bookings/{id}**
- Cancel booking
- Access: Customer or Admin
- Only allowed before completion

### Review Endpoints

**POST /api/v1/bookings/{id}/reviews**
- Add review to completed booking
- Access: Customer (booking owner)
- Only allowed for COMPLETED bookings

**GET /api/v1/specialists/{id}/reviews**
- Get all reviews for a specialist
- Access: Public

**PUT /api/v1/reviews/{id}**
- Update review (within 24 hours)
- Access: Review author

## Booking Lifecycle

### Status Flow

```
PENDING → CONFIRMED → COMPLETED
   ↓          ↓
CANCELLED  CANCELLED
```

**PENDING**: Booking created, awaiting specialist confirmation
**CONFIRMED**: Specialist accepted, scheduled
**COMPLETED**: Service delivered, payment processed
**CANCELLED**: Cancelled by customer or specialist

### Status Transitions

- **PENDING → CONFIRMED**: Specialist accepts booking
- **CONFIRMED → COMPLETED**: Service completed
- **Any → CANCELLED**: Cancellation (rules apply)
- **No other transitions allowed**

## Data Models

### Booking

Core booking information:
- `customerId` — Reference to customer
- `specialistId` — Reference to specialist
- `serviceType` — Type of service requested
- `scheduledDate` — Date of service
- `scheduledTime` — Time slot
- `status` — PENDING, CONFIRMED, COMPLETED, CANCELLED
- `address` — Service location
- `notes` — Special instructions
- `price` — Agreed price
- `createdAt`, `updatedAt` — Timestamps

### Review

Customer feedback after service:
- `bookingId` — Reference to booking
- `customerId` — Who wrote the review
- `specialistId` — Who received the review
- `rating` — 1-5 stars
- `comment` — Text feedback
- `createdAt` — When review was written

Reviews are immutable after 24 hours (no edits allowed).

### Complaint

Customer complaints about specialists:
- `bookingId` — Related booking
- `customerId` — Complainant
- `specialistId` — Accused specialist
- `type` — Category of complaint
- `description` — Details
- `status` — OPEN, UNDER_REVIEW, RESOLVED, DISMISSED
- `resolution` — Admin notes

## Service Integration

### User Service (Feign Client)

Fetches user details:
- Customer name/contact for booking
- Specialist details for display
- Validates user existence

### Notification Service

Sends emails for:
- Booking created (to specialist)
- Booking confirmed (to customer)
- Booking completed (both parties)
- Booking cancelled (both parties)
- New review posted (to specialist)

## Architecture

### Components

**BookingController / ReviewController**
- REST endpoints
- Request validation
- Access control checks

**BookingService / ReviewService**
- Business logic
- Status transition validation
- Transaction management

**BookingRepository / ReviewRepository**
- Data access
- Custom queries for filtering

**DTOs** (Java Records)
- Request/response objects
- Validation annotations

**Mapper** (MapStruct)
- Entity ↔ DTO conversion

## Database

**Collections**:
- `bookings` — All booking records
- `reviews` — Customer reviews
- `complaints` — Customer complaints

Indexes:
- `customerId` — For customer booking queries
- `specialistId` — For specialist booking queries
- `status` — For status-based filtering

## Dependencies

- Spring Boot Web
- Spring Data MongoDB
- Spring Validation
- MapStruct
- OpenFeign (for user-service calls)
- Common module
- User Service Client module

See `build.gradle.kts` for versions.

## Configuration

Environment variables:
- `MONGODB_URI` — Database connection
- `USER_SERVICE_URL` — For Feign client
- `NOTIFICATION_SERVICE_URL` — For email notifications

## Status Update Rules

When updating booking status:

1. **PENDING → CONFIRMED**: Specialist only, must be available
2. **CONFIRMED → COMPLETED**: Specialist only, after service date
3. **Any → CANCELLED**:
   - Customer can cancel own bookings
   - Specialist can cancel own bookings
   - Admin can cancel any booking
   - Cannot cancel already COMPLETED bookings

## Review Rules

- Only customers can write reviews
- Only for COMPLETED bookings
- One review per booking
- Editable within 24 hours
- Specialist can respond (not edit)

## Links

- Service source: `backend/booking-service/`
- Feign client usage: See `UserServiceClient` calls
- DTOs: `backend/booking-service/src/main/java/.../dto/`
- API design: [../standards/api-design.md](../standards/api-design.md)
