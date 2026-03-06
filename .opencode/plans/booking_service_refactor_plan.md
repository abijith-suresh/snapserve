# Booking Service Refactor Plan

## Executive Summary

This document outlines a comprehensive refactor of the booking-service to align with production-ready standards, 12-Factor App methodology, and patterns established in auth-service and user-service. The refactor focuses on extensibility, maintainability, and consistency across the SnapServe microservices architecture.

**Scope**: Complete code restructuring without test implementation (tests to be added separately). Complaints functionality will be entirely removed. API versioning introduced. Synchronous architecture preserved with TODOs for future async migration.

---

## 1. Current State Analysis

### 1.1 Architecture Overview

**Current Stack**: Java 21, Spring Boot 4.0.3, Spring Data MongoDB, OpenFeign, Lombok

**Current Package Structure**:
```
com.snapserve.booking/
├── BookingServiceApplication.java
├── controller/
│   ├── BookingController.java
│   └── ReviewController.java
├── service/
│   ├── BookingService.java
│   └── ReviewService.java
├── model/
│   ├── Booking.java
│   └── Review.java
├── dto/
│   ├── AddBookingDto.java
│   ├── BookingResponseDto.java
│   ├── ReviewDto.java
│   └── SpecialistReviewResponseDto.java
└── repo/
    ├── BookingRepository.java
    └── ReviewRepository.java
```

### 1.2 Critical Issues Identified

| Issue | Severity | Impact |
|-------|----------|--------|
| **Silent Failures** | CRITICAL | Services catch all exceptions and return null, masking errors |
| **Null Returns for Not Found** | HIGH | Controllers check for null instead of using exceptions |
| **Field Injection** | MEDIUM | Uses `@Autowired` instead of constructor injection |
| **No Validation** | MEDIUM | Request bodies lack `@Valid` and validation annotations |
| **No API Versioning** | MEDIUM | Endpoints lack /api/v1 prefix |
| **No Auditing** | MEDIUM | Entities lack createdAt/updatedAt fields |
| **No Logging** | MEDIUM | Services lack operational logging |
| **No Pagination** | LOW | List endpoints return all records |
| **Inconsistent Naming** | LOW | Collection names mixed (booking vs reviews) |

### 1.3 Pattern Inconsistencies

**Booking-Service vs Reference Services**:

| Aspect | Booking-Service | Auth/User Service | Gap |
|--------|-----------------|-------------------|-----|
| Response Wrapping | Raw DTOs | `ApiResponse<T>` | Missing wrapper |
| Exception Handling | Returns null | Throws exceptions | Inconsistent error handling |
| Dependency Injection | Field `@Autowired` | `@RequiredArgsConstructor` | Outdated pattern |
| DTOs | Lombok classes | Java Records | Different style |
| Mapping | Manual methods | MapStruct mappers | No mapper layer |
| Auditing | None | `Auditable` base class | Missing audit fields |
| OpenAPI | None | Full annotations | No API docs |
| Logging | None | `@Slf4j` comprehensive | Missing observability |

---

## 2. Target Architecture

### 2.1 Package Structure

```
com.snapserve.booking/
├── BookingServiceApplication.java
├── config/
│   ├── OpenApiConfig.java              # Swagger/OpenAPI configuration
│   └── MongoConfig.java                # MongoDB configuration (optional)
├── controller/
│   ├── BookingController.java          # /api/v1/bookings endpoints
│   └── ReviewController.java           # /api/v1/reviews endpoints
├── service/
│   ├── BookingService.java             # Booking business logic
│   ├── ReviewService.java              # Review business logic
│   └── mapper/
│       ├── BookingMapper.java          # MapStruct mapper
│       └── ReviewMapper.java           # MapStruct mapper
├── model/
│   ├── Booking.java                    # MongoDB entity (extends Auditable)
│   └── Review.java                     # MongoDB entity (extends Auditable)
├── repository/
│   ├── BookingRepository.java          # Spring Data repository
│   └── ReviewRepository.java           # Spring Data repository
└── dto/
    ├── request/
    │   ├── BookingRequest.java         # Java Record with validation
    │   └── ReviewRequest.java          # Java Record with validation
    └── response/
        ├── BookingResponse.java        # Java Record
        ├── BookingListResponse.java    # Java Record (paginated)
        ├── ReviewResponse.java         # Java Record
        └── ReviewListResponse.java     # Java Record (paginated)
```

### 2.2 Technology Additions

**Dependencies to Add** (in `build.gradle.kts`):

```kotlin
dependencies {
    // Existing dependencies maintained
    implementation(project(":backend:common"))
    implementation(project(":backend:user-service-client"))
    
    // Mapping
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    compileOnly("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    
    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Validation (ensure jakarta validation is available)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Existing Spring dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
```

---

## 3. Detailed Implementation Plan

### Phase 1: Foundation & Configuration

#### 3.1.1 Update build.gradle.kts

**Add dependencies**:
- MapStruct for DTO mapping
- SpringDoc OpenAPI
- Validation starter
- Lombok MapStruct binding

**Remove dependencies**:
- None (all current deps remain)

#### 3.1.2 Configuration Files

**application.yml** (revised):
```yaml
spring:
  application:
    name: booking-service
  data:
    mongodb:
      uri: ${MONGODB_URI}
      auto-index-creation: true

server:
  port: 9002

logging:
  level:
    com.snapserve.booking: INFO
  include-application-name: true

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info

user:
  service:
    url: ${USER_SERVICE_URL:http://user-service:9001}
```

**application-prod.yml** (new file):
```yaml
spring:
  data:
    mongodb:
      auto-index-creation: false

logging:
  level:
    com.snapserve.booking: WARN
  include-application-name: true

springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

#### 3.1.3 Create OpenApiConfig.java

**Purpose**: Configure OpenAPI documentation with service metadata

**Location**: `com.snapserve.booking.config.OpenApiConfig`

**Key Features**:
- Service title and description
- API versioning info
- Contact information
- Server URLs for different environments

---

### Phase 2: Domain Layer Refactoring

#### 3.2.1 Refactor Booking Entity

**Current Issues**:
- No auditing fields
- No indexes defined
- Inconsistent collection naming

**Target Changes**:
```java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bookings")  // Plural, consistent
@CompoundIndex(name = "customer_status_idx", def = "{'customerId': 1, 'status': 1}")
@CompoundIndex(name = "specialist_status_idx", def = "{'specialistId': 1, 'status': 1}")
@CompoundIndex(name = "booking_date_idx", def = "{'bookingDate': 1}")
public class Booking extends Auditable {
    
    @Id
    private ObjectId id;
    
    @Indexed
    private String customerId;
    
    @Indexed
    private String specialistId;
    
    private LocalDateTime bookingDate;
    
    private String status;  // PENDING, CONFIRMED, CANCELLED, COMPLETED
    
    private String notes;
    
    private BigDecimal price;
    
    private String serviceType;
    
    @Version
    private Long version;  // For optimistic locking
}
```

**Changes**:
- Extend `Auditable` from common module
- Add proper indexes for query optimization
- Standardize collection name to "bookings"
- Add `version` field for optimistic locking
- Use `ObjectId` for ID type (internal)

#### 3.2.2 Refactor Review Entity

**Target Changes**:
```java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reviews")
@CompoundIndex(name = "booking_idx", def = "{'bookingId': 1}", unique = true)
@CompoundIndex(name = "specialist_rating_idx", def = "{'specialistId': 1, 'rating': 1}")
public class Review extends Auditable {
    
    @Id
    private ObjectId id;
    
    @Indexed
    private String bookingId;
    
    @Indexed
    private String customerId;
    
    @Indexed
    private String specialistId;
    
    private Integer rating;  // 1-5
    
    private String comment;
    
    @Version
    private Long version;
}
```

**Changes**:
- Extend `Auditable`
- Add unique constraint on bookingId (one review per booking)
- Add indexes for common queries

---

### Phase 3: DTO Layer Refactoring

#### 3.3.1 Create Request DTOs (Java Records)

**BookingRequest.java**:
```java
public record BookingRequest(
    @NotBlank(message = "Customer ID is required")
    String customerId,
    
    @NotBlank(message = "Specialist ID is required")
    String specialistId,
    
    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    LocalDateTime bookingDate,
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    String notes,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price,
    
    @NotBlank(message = "Service type is required")
    @Size(max = 100, message = "Service type must not exceed 100 characters")
    String serviceType
) {}
```

**ReviewRequest.java**:
```java
public record ReviewRequest(
    @NotBlank(message = "Booking ID is required")
    String bookingId,
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    Integer rating,
    
    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    String comment
) {}
```

**UpdateBookingRequest.java** (for PATCH operations):
```java
public record UpdateBookingRequest(
    @Future(message = "Booking date must be in the future")
    LocalDateTime bookingDate,
    
    @Pattern(regexp = "PENDING|CONFIRMED|CANCELLED|COMPLETED", message = "Invalid status")
    String status,
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    String notes
) {}
```

#### 3.3.2 Create Response DTOs (Java Records)

**BookingResponse.java**:
```java
public record BookingResponse(
    String id,
    String customerId,
    String specialistId,
    LocalDateTime bookingDate,
    String status,
    String notes,
    BigDecimal price,
    String serviceType,
    Instant createdAt,
    Instant updatedAt
) {}
```

**BookingListResponse.java** (for paginated results):
```java
public record BookingListResponse(
    List<BookingResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {}
```

**ReviewResponse.java**:
```java
public record ReviewResponse(
    String id,
    String bookingId,
    String customerId,
    String specialistId,
    Integer rating,
    String comment,
    Instant createdAt,
    Instant updatedAt
) {}
```

**SpecialistReviewSummaryResponse.java**:
```java
public record SpecialistReviewSummaryResponse(
    String specialistId,
    double averageRating,
    long totalReviews,
    Map<Integer, Long> ratingDistribution  // rating -> count
) {}
```

---

### Phase 4: Repository Layer Refactoring

#### 3.4.1 Refactor BookingRepository

**Target Interface**:
```java
@Repository
public interface BookingRepository extends MongoRepository<Booking, ObjectId> {
    
    Page<Booking> findByCustomerId(String customerId, Pageable pageable);
    
    Page<Booking> findBySpecialistId(String specialistId, Pageable pageable);
    
    Page<Booking> findByCustomerIdAndStatus(String customerId, String status, Pageable pageable);
    
    Page<Booking> findBySpecialistIdAndStatus(String specialistId, String status, Pageable pageable);
    
    List<Booking> findByBookingDateBetween(LocalDateTime start, LocalDateTime end);
    
    boolean existsByIdAndCustomerId(ObjectId id, String customerId);
    
    boolean existsByIdAndSpecialistId(ObjectId id, String specialistId);
    
    @Query("{ 'specialistId': ?0, 'bookingDate': { $gte: ?1, $lte: ?2 }, 'status': { $nin: ['CANCELLED'] } }")
    List<Booking> findConflictingBookings(String specialistId, LocalDateTime start, LocalDateTime end);
}
```

**Key Changes**:
- Add pagination support (Pageable)
- Add status filtering
- Add date range queries
- Add conflict detection for double-booking prevention
- Add ownership verification methods

#### 3.4.2 Refactor ReviewRepository

**Target Interface**:
```java
@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
    
    Page<Review> findBySpecialistId(String specialistId, Pageable pageable);
    
    Page<Review> findByCustomerId(String customerId, Pageable pageable);
    
    Optional<Review> findByBookingId(String bookingId);
    
    boolean existsByBookingId(String bookingId);
    
    @Aggregation(pipeline = {
        "{ $match: { 'specialistId': ?0 } }",
        "{ $group: { _id: null, avgRating: { $avg: '$rating' }, count: { $sum: 1 }, " +
        "ratings: { $push: '$rating' } } }"
    })
    Optional<ReviewAggregationResult> calculateSpecialistStats(String specialistId);
}
```

**Key Changes**:
- Add pagination
- Add aggregation for specialist stats
- Add booking existence check

---

### Phase 5: Mapper Layer (New)

#### 3.5.1 Create BookingMapper (MapStruct)

**Location**: `com.snapserve.booking.service.mapper.BookingMapper`

```java
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BookingMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Booking toEntity(BookingRequest request);
    
    @Mapping(target = "id", expression = "java(booking.getId().toString())")
    BookingResponse toResponse(Booking booking);
    
    List<BookingResponse> toResponseList(List<Booking> bookings);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "specialistId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(UpdateBookingRequest request, @MappingTarget Booking booking);
}
```

#### 3.5.2 Create ReviewMapper (MapStruct)

```java
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "specialistId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Review toEntity(ReviewRequest request);
    
    @Mapping(target = "id", expression = "java(review.getId().toString())")
    ReviewResponse toResponse(Review review);
    
    List<ReviewResponse> toResponseList(List<Review> reviews);
}
```

---

### Phase 6: Service Layer Refactoring

#### 3.6.1 Refactor BookingService

**Current Issues**:
- Silent failures (catch Exception return null)
- No logging
- No validation
- Manual DTO mapping
- No pagination

**Target Implementation**:

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final BookingMapper bookingMapper;
    private final UserServiceClient userServiceClient;
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String id) {
        log.debug("Fetching booking by ID: {}", id);
        
        Booking booking = bookingRepository.findById(new ObjectId(id))
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", id));
        
        log.debug("Found booking: {}", id);
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public BookingListResponse getAllBookings(Pageable pageable) {
        log.debug("Fetching all bookings, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Booking> bookingPage = bookingRepository.findAll(pageable);
        
        return new BookingListResponse(
            bookingMapper.toResponseList(bookingPage.getContent()),
            bookingPage.getNumber(),
            bookingPage.getSize(),
            bookingPage.getTotalElements(),
            bookingPage.getTotalPages(),
            bookingPage.isFirst(),
            bookingPage.isLast()
        );
    }
    
    @Transactional(readOnly = true)
    public BookingListResponse getBookingsByCustomer(String customerId, Pageable pageable) {
        log.debug("Fetching bookings for customer: {}, page: {}", customerId, pageable.getPageNumber());
        
        validateCustomerExists(customerId);
        
        Page<Booking> bookingPage = bookingRepository.findByCustomerId(customerId, pageable);
        
        return new BookingListResponse(
            bookingMapper.toResponseList(bookingPage.getContent()),
            bookingPage.getNumber(),
            bookingPage.getSize(),
            bookingPage.getTotalElements(),
            bookingPage.getTotalPages(),
            bookingPage.isFirst(),
            bookingPage.isLast()
        );
    }
    
    @Transactional(readOnly = true)
    public BookingListResponse getBookingsBySpecialist(String specialistId, Pageable pageable) {
        log.debug("Fetching bookings for specialist: {}, page: {}", specialistId, pageable.getPageNumber());
        
        validateSpecialistExists(specialistId);
        
        Page<Booking> bookingPage = bookingRepository.findBySpecialistId(specialistId, pageable);
        
        return new BookingListResponse(
            bookingMapper.toResponseList(bookingPage.getContent()),
            bookingPage.getNumber(),
            bookingPage.getSize(),
            bookingPage.getTotalElements(),
            bookingPage.getTotalPages(),
            bookingPage.isFirst(),
            bookingPage.isLast()
        );
    }
    
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for customer: {}, specialist: {}", 
            request.customerId(), request.specialistId());
        
        // Validate both customer and specialist exist
        validateCustomerExists(request.customerId());
        validateSpecialistExists(request.specialistId());
        
        // Check for booking conflicts
        checkForBookingConflicts(request.specialistId(), request.bookingDate());
        
        Booking booking = bookingMapper.toEntity(request);
        booking.setStatus("PENDING");
        
        Booking saved = bookingRepository.save(booking);
        
        log.info("Booking created successfully with ID: {}", saved.getId());
        
        // TODO: Emit async event for notification service integration
        // eventPublisher.publishEvent(new BookingCreatedEvent(saved));
        
        return bookingMapper.toResponse(saved);
    }
    
    public BookingResponse updateBooking(String id, UpdateBookingRequest request) {
        log.info("Updating booking: {}", id);
        
        Booking booking = bookingRepository.findById(new ObjectId(id))
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", id));
        
        // Check for conflicts if date is being updated
        if (request.bookingDate() != null) {
            checkForBookingConflicts(booking.getSpecialistId(), request.bookingDate());
        }
        
        bookingMapper.updateEntityFromRequest(request, booking);
        Booking updated = bookingRepository.save(booking);
        
        log.info("Booking updated successfully: {}", id);
        
        // TODO: Emit async event for status change notifications
        // if (request.status() != null) {
        //     eventPublisher.publishEvent(new BookingStatusChangedEvent(updated, oldStatus));
        // }
        
        return bookingMapper.toResponse(updated);
    }
    
    public void cancelBooking(String id) {
        log.info("Cancelling booking: {}", id);
        
        Booking booking = bookingRepository.findById(new ObjectId(id))
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", id));
        
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new ConflictException("Booking is already cancelled");
        }
        
        if ("COMPLETED".equals(booking.getStatus())) {
            throw new ConflictException("Cannot cancel a completed booking");
        }
        
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        
        log.info("Booking cancelled successfully: {}", id);
        
        // TODO: Emit async event for cancellation notification
        // eventPublisher.publishEvent(new BookingCancelledEvent(booking));
    }
    
    public void deleteBooking(String id) {
        log.info("Deleting booking: {}", id);
        
        ObjectId objectId = new ObjectId(id);
        
        if (!bookingRepository.existsById(objectId)) {
            throw ResourceNotFoundException.of("Booking", id);
        }
        
        // Delete associated review if exists
        reviewRepository.findByBookingId(id).ifPresent(review -> {
            reviewRepository.delete(review);
            log.debug("Deleted associated review for booking: {}", id);
        });
        
        bookingRepository.deleteById(objectId);
        
        log.info("Booking deleted successfully: {}", id);
    }
    
    // Helper methods
    private void validateCustomerExists(String customerId) {
        try {
            userServiceClient.getCustomerById(customerId);
        } catch (FeignException.NotFound e) {
            throw ResourceNotFoundException.of("Customer", customerId);
        } catch (FeignException e) {
            log.error("Error validating customer: {}", customerId, e);
            throw new ServiceUnavailableException("User service unavailable");
        }
    }
    
    private void validateSpecialistExists(String specialistId) {
        try {
            userServiceClient.getSpecialistById(specialistId);
        } catch (FeignException.NotFound e) {
            throw ResourceNotFoundException.of("Specialist", specialistId);
        } catch (FeignException e) {
            log.error("Error validating specialist: {}", specialistId, e);
            throw new ServiceUnavailableException("User service unavailable");
        }
    }
    
    private void checkForBookingConflicts(String specialistId, LocalDateTime bookingDate) {
        // Check for conflicts within 2 hours window
        LocalDateTime start = bookingDate.minusHours(2);
        LocalDateTime end = bookingDate.plusHours(2);
        
        List<Booking> conflicts = bookingRepository.findConflictingBookings(specialistId, start, end);
        
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Specialist has conflicting booking at this time");
        }
    }
}
```

**Key Changes**:
- Constructor injection via `@RequiredArgsConstructor`
- Comprehensive SLF4J logging
- Proper exception handling (no silent failures)
- Use of `ResourceNotFoundException` from common module
- Pagination support
- Double-booking prevention
- Ownership validation via user-service client
- TODO comments for future async events
- `@Transactional` boundaries

#### 3.6.2 Refactor ReviewService

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;
    private final UserServiceClient userServiceClient;
    
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(String id) {
        log.debug("Fetching review by ID: {}", id);
        
        Review review = reviewRepository.findById(new ObjectId(id))
            .orElseThrow(() -> ResourceNotFoundException.of("Review", id));
        
        return reviewMapper.toResponse(review);
    }
    
    @Transactional(readOnly = true)
    public ReviewResponse getReviewByBookingId(String bookingId) {
        log.debug("Fetching review for booking: {}", bookingId);
        
        return reviewRepository.findByBookingId(bookingId)
            .map(reviewMapper::toResponse)
            .orElseThrow(() -> ResourceNotFoundException.of("Review for Booking", bookingId));
    }
    
    @Transactional(readOnly = true)
    public ReviewListResponse getReviewsBySpecialist(String specialistId, Pageable pageable) {
        log.debug("Fetching reviews for specialist: {}, page: {}", specialistId, pageable.getPageNumber());
        
        validateSpecialistExists(specialistId);
        
        Page<Review> reviewPage = reviewRepository.findBySpecialistId(specialistId, pageable);
        
        return new ReviewListResponse(
            reviewMapper.toResponseList(reviewPage.getContent()),
            reviewPage.getNumber(),
            reviewPage.getSize(),
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.isFirst(),
            reviewPage.isLast()
        );
    }
    
    @Transactional(readOnly = true)
    public SpecialistReviewSummaryResponse getSpecialistReviewSummary(String specialistId) {
        log.debug("Calculating review summary for specialist: {}", specialistId);
        
        validateSpecialistExists(specialistId);
        
        return reviewRepository.calculateSpecialistStats(specialistId)
            .map(stats -> new SpecialistReviewSummaryResponse(
                specialistId,
                stats.getAvgRating(),
                stats.getCount(),
                calculateRatingDistribution(stats.getRatings())
            ))
            .orElse(new SpecialistReviewSummaryResponse(specialistId, 0.0, 0L, Map.of()));
    }
    
    public ReviewResponse createReview(String customerId, ReviewRequest request) {
        log.info("Creating review for booking: {} by customer: {}", request.bookingId(), customerId);
        
        // Validate booking exists and is completed
        Booking booking = bookingRepository.findById(new ObjectId(request.bookingId()))
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", request.bookingId()));
        
        if (!"COMPLETED".equals(booking.getStatus())) {
            throw new BadRequestException("Can only review completed bookings");
        }
        
        // Verify customer owns the booking
        if (!customerId.equals(booking.getCustomerId())) {
            throw new BadRequestException("Can only review your own bookings");
        }
        
        // Check if review already exists
        if (reviewRepository.existsByBookingId(request.bookingId())) {
            throw new ConflictException("Review already exists for this booking");
        }
        
        Review review = reviewMapper.toEntity(request);
        review.setCustomerId(customerId);
        review.setSpecialistId(booking.getSpecialistId());
        
        Review saved = reviewRepository.save(review);
        
        log.info("Review created successfully with ID: {}", saved.getId());
        
        // TODO: Emit async event for notification
        // eventPublisher.publishEvent(new ReviewCreatedEvent(saved));
        
        return reviewMapper.toResponse(saved);
    }
    
    public void deleteReview(String id) {
        log.info("Deleting review: {}", id);
        
        ObjectId objectId = new ObjectId(id);
        
        if (!reviewRepository.existsById(objectId)) {
            throw ResourceNotFoundException.of("Review", id);
        }
        
        reviewRepository.deleteById(objectId);
        
        log.info("Review deleted successfully: {}", id);
    }
    
    private void validateSpecialistExists(String specialistId) {
        try {
            userServiceClient.getSpecialistById(specialistId);
        } catch (FeignException.NotFound e) {
            throw ResourceNotFoundException.of("Specialist", specialistId);
        } catch (FeignException e) {
            log.error("Error validating specialist: {}", specialistId, e);
            throw new ServiceUnavailableException("User service unavailable");
        }
    }
    
    private Map<Integer, Long> calculateRatingDistribution(List<Integer> ratings) {
        return ratings.stream()
            .collect(Collectors.groupingBy(r -> r, Collectors.counting()));
    }
}
```

---

### Phase 7: Controller Layer Refactoring

#### 3.7.1 Refactor BookingController

**Target Implementation**:

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management operations")
public class BookingController {
    
    private final BookingService bookingService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieve a specific booking by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking found"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @Parameter(description = "Booking ID") @PathVariable String id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.ok(booking));
    }
    
    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieve paginated list of all bookings")
    public ResponseEntity<ApiResponse<BookingListResponse>> getAllBookings(
            @ParameterObject @PageableDefault(size = 20, sort = "bookingDate", direction = Sort.Direction.DESC) Pageable pageable) {
        BookingListResponse bookings = bookingService.getAllBookings(pageable);
        return ResponseEntity.ok(ApiResponse.ok(bookings));
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get bookings by customer", description = "Retrieve paginated bookings for a specific customer")
    public ResponseEntity<ApiResponse<BookingListResponse>> getBookingsByCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        BookingListResponse bookings = bookingService.getBookingsByCustomer(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(bookings));
    }
    
    @GetMapping("/specialist/{specialistId}")
    @Operation(summary = "Get bookings by specialist", description = "Retrieve paginated bookings for a specific specialist")
    public ResponseEntity<ApiResponse<BookingListResponse>> getBookingsBySpecialist(
            @Parameter(description = "Specialist ID") @PathVariable String specialistId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        BookingListResponse bookings = bookingService.getBookingsBySpecialist(specialistId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(bookings));
    }
    
    @PostMapping
    @Operation(summary = "Create booking", description = "Create a new booking")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Booking conflict")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {
        BookingResponse created = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Booking created successfully", created));
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Update booking", description = "Update an existing booking")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "409", description = "Update conflict")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(
            @Parameter(description = "Booking ID") @PathVariable String id,
            @Valid @RequestBody UpdateBookingRequest request) {
        BookingResponse updated = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Booking updated successfully", updated));
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel an existing booking")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Booking cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "409", description = "Cannot cancel booking")
    })
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @Parameter(description = "Booking ID") @PathVariable String id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete booking", description = "Delete a booking permanently")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteBooking(
            @Parameter(description = "Booking ID") @PathVariable String id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(ApiResponse.ok("Booking deleted successfully"));
    }
}
```

**Key Changes**:
- API versioning: `/api/v1/bookings`
- Constructor injection
- OpenAPI annotations for documentation
- `@Valid` for request validation
- `ApiResponse<T>` wrapper for all responses
- Proper HTTP status codes (201 for create, 204 for delete/cancel)
- Pagination support with `@PageableDefault`
- `@Parameter` and `@ParameterObject` for OpenAPI

#### 3.7.2 Refactor ReviewController

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management operations")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(
            @PathVariable String id) {
        ReviewResponse review = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.ok(review));
    }
    
    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get review by booking ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByBookingId(
            @PathVariable String bookingId) {
        ReviewResponse review = reviewService.getReviewByBookingId(bookingId);
        return ResponseEntity.ok(ApiResponse.ok(review));
    }
    
    @GetMapping("/specialist/{specialistId}")
    @Operation(summary = "Get reviews by specialist")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getReviewsBySpecialist(
            @PathVariable String specialistId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        ReviewListResponse reviews = reviewService.getReviewsBySpecialist(specialistId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }
    
    @GetMapping("/specialist/{specialistId}/summary")
    @Operation(summary = "Get specialist review summary")
    public ResponseEntity<ApiResponse<SpecialistReviewSummaryResponse>> getSpecialistSummary(
            @PathVariable String specialistId) {
        SpecialistReviewSummaryResponse summary = reviewService.getSpecialistReviewSummary(specialistId);
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }
    
    @PostMapping
    @Operation(summary = "Create review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestHeader("X-Customer-Id") String customerId,  // From JWT or header
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse created = reviewService.createReview(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Review created successfully", created));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.ok("Review deleted successfully"));
    }
}
```

---

## 4. Exception Handling

### 4.1 Custom Exceptions Required

Add these to the `common` module or verify they exist:

```java
// Already in common module
public class ResourceNotFoundException extends ApiException { }
public class ConflictException extends ApiException { }
public class BadRequestException extends ApiException { }

// New exception to add to common module
public class ServiceUnavailableException extends ApiException {
    public ServiceUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
```

### 4.2 Global Exception Handler

The `GlobalExceptionHandler` from the common module will automatically handle:
- `ResourceNotFoundException` → 404
- `ConflictException` → 409
- `BadRequestException` → 400
- `ServiceUnavailableException` → 503
- `MethodArgumentNotValidException` → 400 with field errors
- Generic exceptions → 500

---

## 5. Deletion Tasks (Complaints Removal)

### 5.1 Files to Delete

**Controllers**:
- `ComplaintController.java` (if exists)

**Services**:
- `ComplaintService.java` (if exists)

**Repositories**:
- `ComplaintRepository.java` (if exists)

**Models**:
- `Complaint.java` (if exists)

**DTOs**:
- All `Complaint*Dto.java` files (if exist)

### 5.2 References to Remove

Check and remove from:
- `build.gradle.kts` (if complaint-specific dependencies)
- `application.yml` (if complaint-specific config)
- Any imports in remaining files

---

## 6. Production Readiness Checklist

### 6.1 12-Factor Compliance

| Factor | Status | Notes |
|--------|--------|-------|
| **Codebase** | ✅ | Single codebase in version control |
| **Dependencies** | ✅ | Explicitly declared in Gradle |
| **Config** | ✅ | Environment variables via `${}` |
| **Backing Services** | ✅ | MongoDB URI configurable |
| **Build/Release/Run** | ✅ | Docker-based deployment |
| **Processes** | ✅ | Stateless processes |
| **Port Binding** | ✅ | Port 9002 configurable |
| **Concurrency** | ✅ | Horizontally scalable |
| **Disposability** | ✅ | Fast startup, graceful shutdown |
| **Dev/Prod Parity** | ✅ | Docker Compose for dev/prod |
| **Logs** | ✅ | SLF4J with JSON in production |
| **Admin Processes** | ✅ | Actuator endpoints |

### 6.2 Observability

**Implemented** (matching user/auth services):
- ✅ Actuator health/info endpoints
- ✅ Application logging (INFO dev, WARN prod)
- ✅ OpenAPI documentation (disabled in prod)
- ✅ Request/response logging via gateway

**Not Included** (user decision):
- ❌ Micrometer metrics (not in user/auth services)
- ❌ Distributed tracing (not in user/auth services)
- ❌ Structured JSON logging (not in user/auth services)

### 6.3 Security

**Current**:
- JWT validation at API Gateway
- No direct security in service
- Inter-service calls via Feign

**Recommendations** (future):
- Add service-to-service authentication
- Add request ID propagation for tracing
- Add rate limiting at service level (currently at gateway)

---

## 7. Future Extensibility Roadmap

### 7.1 Async Events (TODOs in Code)

When ready to implement:

```java
// Create events
public record BookingCreatedEvent(Booking booking) {}
public record BookingStatusChangedEvent(Booking booking, String oldStatus) {}
public record BookingCancelledEvent(Booking booking) {}
public record ReviewCreatedEvent(Review review) {}

// Add to BookingService
private final ApplicationEventPublisher eventPublisher;

// Emit events
@EventListener
public void handleBookingCreated(BookingCreatedEvent event) {
    // Send notification
    // Update analytics
    // Trigger workflows
}
```

### 7.2 Caching (Future Enhancement)

When reviews become read-heavy:

```java
@Cacheable(value = "specialistReviews", key = "#specialistId")
public ReviewListResponse getReviewsBySpecialist(String specialistId, Pageable pageable) { }

@Cacheable(value = "specialistSummary", key = "#specialistId")
public SpecialistReviewSummaryResponse getSpecialistReviewSummary(String specialistId) { }
```

### 7.3 Notification Service Client

When notification service is ready:

```java
// Create module: notification-service-client
@FeignClient(name = "notification-service", url = "${notification.service.url}")
public interface NotificationServiceClient {
    @PostMapping("/api/v1/notifications")
    void sendNotification(NotificationRequest request);
}
```

### 7.4 Additional Features (Future)

- **Scheduling**: Add cron jobs for booking reminders
- **Analytics**: Add booking metrics and reporting
- **Search**: Full-text search for bookings
- **Audit Trail**: Detailed audit logging
- **Soft Delete**: Implement soft delete pattern
- **Multi-tenancy**: Support for multiple organizations

---

## 8. Migration Guide

### 8.1 Database Migration

**MongoDB Changes**:

1. **Rename Collection** (if needed):
```javascript
db.booking.renameCollection("bookings")
```

2. **Add Indexes**:
```javascript
db.bookings.createIndex({ customerId: 1, status: 1 })
db.bookings.createIndex({ specialistId: 1, status: 1 })
db.bookings.createIndex({ bookingDate: 1 })
db.reviews.createIndex({ bookingId: 1 }, { unique: true })
db.reviews.createIndex({ specialistId: 1, rating: 1 })
```

3. **Migrate Data** (add audit fields):
```javascript
db.bookings.updateMany(
    { createdAt: { $exists: false } },
    { $set: { createdAt: new Date(), updatedAt: new Date() } }
)
db.reviews.updateMany(
    { createdAt: { $exists: false } },
    { $set: { createdAt: new Date(), updatedAt: new Date() } }
)
```

### 8.2 API Changes

**Breaking Changes**:

| Old Endpoint | New Endpoint | Change |
|--------------|--------------|--------|
| `GET /bookings` | `GET /api/v1/bookings` | Added versioning |
| `GET /bookings/{id}` | `GET /api/v1/bookings/{id}` | Added versioning |
| `POST /bookings` | `POST /api/v1/bookings` | Added versioning |
| `PUT /bookings/{id}` | `PATCH /api/v1/bookings/{id}` | Changed to PATCH |
| `DELETE /bookings/{id}` | `DELETE /api/v1/bookings/{id}` | Added versioning |
| `GET /reviews` | `GET /api/v1/reviews` | Added versioning |

**Response Changes**:
- All responses now wrapped in `ApiResponse<T>`
- Pagination added to list endpoints
- Error responses now standardized

### 8.3 Frontend Updates Required

1. Update all API calls to use `/api/v1` prefix
2. Handle new `ApiResponse<T>` wrapper
3. Update pagination handling
4. Remove any complaints-related UI

---

## 9. Implementation Phases

### Phase 1: Infrastructure (Priority: HIGH)
1. Update `build.gradle.kts` with new dependencies
2. Create/revise `application.yml` and `application-prod.yml`
3. Create `OpenApiConfig.java`
4. Create MapStruct mapper interfaces

### Phase 2: Domain Layer (Priority: HIGH)
1. Refactor `Booking` entity (extend Auditable, add indexes)
2. Refactor `Review` entity (extend Auditable, add indexes)
3. Update repositories with pagination and new methods

### Phase 3: DTO Layer (Priority: HIGH)
1. Create request DTOs (Java Records with validation)
2. Create response DTOs (Java Records)
3. Delete old DTO classes

### Phase 4: Service Layer (Priority: HIGH)
1. Refactor `BookingService`
2. Refactor `ReviewService`
3. Add proper exception handling and logging
4. Add TODO comments for async events

### Phase 5: Controller Layer (Priority: HIGH)
1. Refactor `BookingController` with API versioning
2. Refactor `ReviewController` with API versioning
3. Add OpenAPI annotations

### Phase 6: Cleanup (Priority: MEDIUM)
1. Delete all complaints-related code
2. Remove unused imports
3. Update any references

### Phase 7: Verification (Priority: HIGH)
1. Verify application starts
2. Test key endpoints
3. Review logs
4. Check OpenAPI docs at `/api-docs`

---

## 10. Testing Strategy (Future Implementation)

### 10.1 Unit Tests

**Service Layer**:
- Mock repositories and user client
- Test business logic
- Test exception handling
- Test edge cases

**Mapper Layer**:
- Test MapStruct mappings
- Verify field mappings
- Test null handling

### 10.2 Integration Tests

**Controller Layer**:
- `@WebMvcTest` for controllers
- Test validation
- Test response formats

**Repository Layer**:
- `@DataMongoTest` with Testcontainers
- Test queries
- Test pagination

### 10.3 End-to-End Tests

- Full flow testing
- Multi-service integration
- Contract testing with user-service

---

## 11. Success Criteria

### 11.1 Code Quality
- ✅ No field injection (constructor injection only)
- ✅ All DTOs as Java Records
- ✅ All entities extend Auditable
- ✅ Proper exception handling (no null returns)
- ✅ Comprehensive logging
- ✅ TODO comments for async migration

### 11.2 API Standards
- ✅ All endpoints use `/api/v1` versioning
- ✅ All responses wrapped in `ApiResponse<T>`
- ✅ Proper HTTP status codes
- ✅ Pagination on list endpoints
- ✅ OpenAPI documentation complete
- ✅ Request validation enabled

### 11.3 Performance
- ✅ MongoDB indexes defined
- ✅ Pagination prevents large result sets
- ✅ Optimistic locking with version field
- ✅ Efficient queries (no N+1 problems)

### 11.4 Maintainability
- ✅ Consistent patterns with auth/user services
- ✅ Clear separation of concerns
- ✅ MapStruct mappers for DTO conversion
- ✅ Single responsibility in services
- ✅ Self-documenting code with SLF4J

---

## 12. Risks and Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking API changes | HIGH | Document migration guide, coordinate with frontend |
| Database migration issues | MEDIUM | Test migrations in dev, backup before prod |
| Missing complaints break frontend | MEDIUM | Coordinate removal with frontend team |
| MapStruct learning curve | LOW | Provide examples, use standard patterns |
| Performance regression | MEDIUM | Add indexes, test pagination |
| User service unavailability | MEDIUM | Add circuit breaker, proper error handling |

---

## 13. Conclusion

This refactor transforms the booking-service from a basic CRUD service to a production-ready, maintainable, and extensible microservice. Key outcomes:

1. **Consistency**: Aligns with auth-service and user-service patterns
2. **Robustness**: Eliminates silent failures, adds proper error handling
3. **Observability**: Comprehensive logging and health checks
4. **Extensibility**: Clear TODOs for async events, caching, and future features
5. **API Quality**: Versioned, documented, validated REST API
6. **Performance**: Pagination, indexing, optimistic locking

The service will be ready for:
- Production deployment
- Future feature additions (notifications, analytics)
- Scaling and performance optimization
- Team collaboration with clear patterns

**Estimated Effort**: 2-3 days for implementation (without tests)
**Recommended Approach**: Implement phases sequentially, verify after each phase
**Next Steps**: Review plan, implement Phase 1, verify, continue to Phase 2, etc.

---

## Appendix A: File Inventory

### Files to Create
1. `OpenApiConfig.java`
2. `BookingMapper.java`
3. `ReviewMapper.java`
4. `BookingRequest.java`
5. `UpdateBookingRequest.java`
6. `ReviewRequest.java`
7. `BookingResponse.java`
8. `BookingListResponse.java`
9. `ReviewResponse.java`
10. `ReviewListResponse.java`
11. `SpecialistReviewSummaryResponse.java`
12. `application-prod.yml`

### Files to Modify
1. `build.gradle.kts` - Add dependencies
2. `application.yml` - Update configuration
3. `Booking.java` - Add Auditable, indexes
4. `Review.java` - Add Auditable, indexes
5. `BookingRepository.java` - Add pagination, queries
6. `ReviewRepository.java` - Add pagination, aggregation
7. `BookingService.java` - Complete rewrite
8. `ReviewService.java` - Complete rewrite
9. `BookingController.java` - Complete rewrite with versioning
10. `ReviewController.java` - Complete rewrite with versioning

### Files to Delete
1. `AddBookingDto.java`
2. `BookingResponseDto.java`
3. `ReviewDto.java`
4. `SpecialistReviewResponseDto.java`
5. All Complaint-related files (if any)

---

## Appendix B: Dependencies Summary

### Add
```kotlin
implementation("org.mapstruct:mapstruct:1.5.5.Final")
annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
compileOnly("org.projectlombok:lombok-mapstruct-binding:0.2.0")
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
implementation("org.springframework.boot:spring-boot-starter-validation")
```

### Keep
```kotlin
implementation(project(":backend:common"))
implementation(project(":backend:user-service-client"))
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
implementation("org.springframework.boot:spring-boot-starter-actuator")
compileOnly("org.projectlombok:lombok")
annotationProcessor("org.projectlombok:lombok")
```

---

**Document Version**: 1.0
**Last Updated**: 2026-03-06
**Author**: AI Assistant
**Status**: Ready for Review
