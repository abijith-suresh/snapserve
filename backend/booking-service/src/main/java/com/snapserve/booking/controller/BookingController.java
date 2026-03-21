package com.snapserve.booking.controller;

import com.snapserve.booking.dto.request.BookingRequest;
import com.snapserve.booking.dto.request.UpdateBookingRequest;
import com.snapserve.booking.dto.response.BookingListResponse;
import com.snapserve.booking.dto.response.BookingResponse;
import com.snapserve.booking.service.BookingService;
import com.snapserve.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management endpoints")
public class BookingController {

  private final BookingService bookingService;

  @Operation(
      summary = "Get booking by ID",
      description = "Retrieve a booking by its unique identifier")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Booking found",
        content = @Content(schema = @Schema(implementation = BookingResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Booking not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
      @Parameter(description = "Booking ID", required = true) @PathVariable String id) {
    log.info("GET /api/v1/bookings/{} - Fetching booking by id", id);
    BookingResponse booking = bookingService.getBookingById(id);
    log.info("Booking {} retrieved successfully", id);
    return ResponseEntity.ok(ApiResponse.ok("Booking retrieved successfully", booking));
  }

  @Operation(summary = "Get all bookings", description = "Retrieve all bookings with pagination")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Bookings retrieved successfully",
        content = @Content(schema = @Schema(implementation = BookingListResponse.class)))
  })
  @GetMapping("/")
  public ResponseEntity<ApiResponse<BookingListResponse>> getAllBookings(
      @ParameterObject
          @PageableDefault(size = 20, sort = "bookingDate", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("GET /api/v1/bookings/ - Fetching all bookings with pagination: {}", pageable);
    BookingListResponse bookings = bookingService.getAllBookings(pageable);
    log.info("Retrieved {} bookings", bookings.totalElements());
    return ResponseEntity.ok(ApiResponse.ok("Bookings retrieved successfully", bookings));
  }

  @Operation(
      summary = "Get bookings by customer",
      description = "Retrieve all bookings for a specific customer")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Customer bookings retrieved successfully",
        content = @Content(schema = @Schema(implementation = BookingListResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Customer not found")
  })
  @GetMapping("/customer/{customerId}")
  public ResponseEntity<ApiResponse<BookingListResponse>> getBookingsByCustomer(
      @Parameter(description = "Customer ID", required = true) @PathVariable String customerId,
      @RequestHeader("X-User-Email") String userEmail,
      @RequestHeader("X-User-Roles") String userRoles,
      @ParameterObject
          @PageableDefault(size = 20, sort = "bookingDate", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("GET /api/v1/bookings/customer/{} - Fetching bookings for customer", customerId);
    BookingListResponse bookings =
        bookingService.getBookingsByCustomer(customerId, userEmail, userRoles, pageable);
    log.info("Retrieved {} bookings for customer {}", bookings.totalElements(), customerId);
    return ResponseEntity.ok(ApiResponse.ok("Customer bookings retrieved successfully", bookings));
  }

  @Operation(
      summary = "Get bookings by specialist",
      description = "Retrieve all bookings for a specific specialist")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Specialist bookings retrieved successfully",
        content = @Content(schema = @Schema(implementation = BookingListResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Specialist not found")
  })
  @GetMapping("/specialist/{specialistId}")
  public ResponseEntity<ApiResponse<BookingListResponse>> getBookingsBySpecialist(
      @Parameter(description = "Specialist ID", required = true) @PathVariable String specialistId,
      @ParameterObject
          @PageableDefault(size = 20, sort = "bookingDate", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("GET /api/v1/bookings/specialist/{} - Fetching bookings for specialist", specialistId);
    BookingListResponse bookings = bookingService.getBookingsBySpecialist(specialistId, pageable);
    log.info("Retrieved {} bookings for specialist {}", bookings.totalElements(), specialistId);
    return ResponseEntity.ok(
        ApiResponse.ok("Specialist bookings retrieved successfully", bookings));
  }

  @Operation(summary = "Create booking", description = "Create a new booking")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Booking created successfully",
        content = @Content(schema = @Schema(implementation = BookingResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request data"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Customer or specialist not found"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Booking conflict - time slot already booked")
  })
  @PostMapping("/")
  public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
      @Parameter(description = "Authenticated customer email", required = true)
          @RequestHeader("X-User-Email")
          String userEmail,
      @Parameter(description = "Authenticated user roles", required = true)
          @RequestHeader("X-User-Roles")
          String userRoles,
      @Parameter(description = "Booking request", required = true) @Valid @RequestBody
          BookingRequest request) {
    log.info(
        "POST /api/v1/bookings/ - Creating booking for authenticated user: {} with specialist: {}",
        userEmail,
        request.specialistId());
    BookingResponse booking = bookingService.createBooking(userEmail, userRoles, request);
    log.info("Booking created successfully with id: {}", booking.id());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Booking created successfully", booking));
  }

  @Operation(summary = "Update booking", description = "Update an existing booking")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Booking updated successfully",
        content = @Content(schema = @Schema(implementation = BookingResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request data"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Booking not found"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Booking conflict - time slot already booked")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(
      @Parameter(description = "Booking ID", required = true) @PathVariable String id,
      @Parameter(description = "Update booking request", required = true) @Valid @RequestBody
          UpdateBookingRequest request) {
    log.info("PATCH /api/v1/bookings/{} - Updating booking", id);
    BookingResponse booking = bookingService.updateBooking(id, request);
    log.info("Booking {} updated successfully", id);
    return ResponseEntity.ok(ApiResponse.ok("Booking updated successfully", booking));
  }

  @Operation(summary = "Cancel booking", description = "Cancel an existing booking")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Booking cancelled successfully",
        content = @Content(schema = @Schema(implementation = BookingResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Booking not found")
  })
  @PostMapping("/{id}/cancel")
  public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
      @Parameter(description = "Booking ID", required = true) @PathVariable String id) {
    log.info("POST /api/v1/bookings/{}/cancel - Cancelling booking", id);
    UpdateBookingRequest cancelRequest = new UpdateBookingRequest(null, "CANCELLED", null);
    BookingResponse booking = bookingService.updateBooking(id, cancelRequest);
    log.info("Booking {} cancelled successfully", id);
    return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully", booking));
  }

  @Operation(summary = "Delete booking", description = "Delete a booking permanently")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "204",
        description = "Booking deleted successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Booking not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteBooking(
      @Parameter(description = "Booking ID", required = true) @PathVariable String id) {
    log.info("DELETE /api/v1/bookings/{} - Deleting booking", id);
    bookingService.deleteBooking(id);
    log.info("Booking {} deleted successfully", id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(ApiResponse.ok("Booking deleted successfully"));
  }
}
