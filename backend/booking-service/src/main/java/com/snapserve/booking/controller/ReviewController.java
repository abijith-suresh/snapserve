package com.snapserve.booking.controller;

import com.snapserve.booking.dto.request.ReviewRequest;
import com.snapserve.booking.dto.response.ReviewListResponse;
import com.snapserve.booking.dto.response.ReviewResponse;
import com.snapserve.booking.dto.response.SpecialistReviewSummaryResponse;
import com.snapserve.booking.service.ReviewService;
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
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management endpoints")
public class ReviewController {

  private final ReviewService reviewService;

  @Operation(
      summary = "Get review by ID",
      description = "Retrieve a review by its unique identifier")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Review found",
        content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Review not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(
      @Parameter(description = "Review ID", required = true) @PathVariable String id) {
    log.info("GET /api/v1/reviews/{} - Fetching review by id", id);
    ReviewResponse review = reviewService.getReviewById(id);
    log.info("Review {} retrieved successfully", id);
    return ResponseEntity.ok(ApiResponse.ok("Review retrieved successfully", review));
  }

  @Operation(
      summary = "Get review by booking ID",
      description = "Retrieve the review associated with a specific booking")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Review found",
        content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Review not found for the specified booking")
  })
  @GetMapping("/booking/{bookingId}")
  public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByBookingId(
      @Parameter(description = "Booking ID", required = true) @PathVariable String bookingId) {
    log.info("GET /api/v1/reviews/booking/{} - Fetching review by booking id", bookingId);
    ReviewResponse review = reviewService.getReviewByBookingId(bookingId);
    log.info("Review for booking {} retrieved successfully", bookingId);
    return ResponseEntity.ok(ApiResponse.ok("Review retrieved successfully", review));
  }

  @Operation(
      summary = "Get reviews by specialist",
      description = "Retrieve all reviews for a specific specialist with pagination")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Reviews retrieved successfully",
        content = @Content(schema = @Schema(implementation = ReviewListResponse.class)))
  })
  @GetMapping("/specialist/{specialistId}")
  public ResponseEntity<ApiResponse<ReviewListResponse>> getReviewsBySpecialist(
      @Parameter(description = "Specialist ID", required = true) @PathVariable String specialistId,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("GET /api/v1/reviews/specialist/{} - Fetching reviews for specialist", specialistId);
    ReviewListResponse reviews = reviewService.getReviewsBySpecialist(specialistId, pageable);
    log.info("Retrieved {} reviews for specialist {}", reviews.totalElements(), specialistId);
    return ResponseEntity.ok(ApiResponse.ok("Reviews retrieved successfully", reviews));
  }

  @Operation(
      summary = "Get specialist review summary",
      description = "Get aggregated review statistics for a specialist")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Review summary retrieved successfully",
        content =
            @Content(schema = @Schema(implementation = SpecialistReviewSummaryResponse.class)))
  })
  @GetMapping("/specialist/{specialistId}/summary")
  public ResponseEntity<ApiResponse<SpecialistReviewSummaryResponse>> getSpecialistReviewSummary(
      @Parameter(description = "Specialist ID", required = true) @PathVariable
          String specialistId) {
    log.info(
        "GET /api/v1/reviews/specialist/{}/summary - Fetching review summary for specialist",
        specialistId);
    SpecialistReviewSummaryResponse summary =
        reviewService.getSpecialistReviewSummary(specialistId);
    log.info("Review summary for specialist {} retrieved successfully", specialistId);
    return ResponseEntity.ok(ApiResponse.ok("Review summary retrieved successfully", summary));
  }

  @Operation(summary = "Create review", description = "Create a new review for a completed booking")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Review created successfully",
        content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request data or booking not completed"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Booking not found"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Review already exists for this booking")
  })
  @PostMapping("/")
  public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
      @Parameter(description = "Customer ID", required = true) @RequestHeader("X-Customer-Id")
          String customerId,
      @Parameter(description = "Review request", required = true) @Valid @RequestBody
          ReviewRequest request) {
    log.info(
        "POST /api/v1/reviews/ - Creating review for booking: {} by customer: {}",
        request.bookingId(),
        customerId);
    ReviewResponse review = reviewService.createReview(customerId, request);
    log.info("Review created successfully with id: {}", review.id());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Review created successfully", review));
  }

  @Operation(summary = "Delete review", description = "Delete a review")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "204",
        description = "Review deleted successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Can only delete own reviews"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Review not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteReview(
      @Parameter(description = "Review ID", required = true) @PathVariable String id,
      @Parameter(description = "Customer ID", required = true) @RequestHeader("X-Customer-Id")
          String customerId) {
    log.info("DELETE /api/v1/reviews/{} - Deleting review by customer: {}", id, customerId);
    reviewService.deleteReview(id, customerId);
    log.info("Review {} deleted successfully", id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(ApiResponse.ok("Review deleted successfully"));
  }
}
