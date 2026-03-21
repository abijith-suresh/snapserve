package com.snapserve.booking.service;

import com.snapserve.booking.dto.request.ReviewRequest;
import com.snapserve.booking.dto.response.ReviewListResponse;
import com.snapserve.booking.dto.response.ReviewResponse;
import com.snapserve.booking.dto.response.SpecialistReviewSummaryResponse;
import com.snapserve.booking.model.Booking;
import com.snapserve.booking.model.BookingStatus;
import com.snapserve.booking.model.Review;
import com.snapserve.booking.repository.BookingRepository;
import com.snapserve.booking.repository.ReviewRepository;
import com.snapserve.booking.repository.ReviewRepository.ReviewAggregationResult;
import com.snapserve.booking.service.mapper.ReviewMapper;
import com.snapserve.common.exception.BadRequestException;
import com.snapserve.common.exception.ConflictException;
import com.snapserve.common.exception.ResourceNotFoundException;
import com.snapserve.common.exception.ServiceUnavailableException;
import com.snapserve.common.mongo.ObjectIdParser;
import com.snapserve.common.response.ApiResponse;
import com.snapserve.userclient.client.UserServiceClient;
import feign.FeignException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final BookingRepository bookingRepository;
  private final ReviewMapper reviewMapper;
  private final UserServiceClient userServiceClient;

  @Transactional(readOnly = true)
  public ReviewResponse getReviewById(String id) {
    log.info("Fetching review with id: {}", id);

    Review review =
        reviewRepository
            .findById(parseObjectId(id, "review"))
            .orElseThrow(() -> ResourceNotFoundException.of("Review", id));

    log.debug("Found review: {}", review);
    return reviewMapper.toResponse(review);
  }

  @Transactional(readOnly = true)
  public ReviewResponse getReviewByBookingId(String bookingId) {
    log.info("Fetching review for booking: {}", bookingId);

    Review review =
        reviewRepository
            .findByBookingId(bookingId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Review not found for booking with id: " + bookingId));

    log.debug("Found review for booking: {}", bookingId);
    return reviewMapper.toResponse(review);
  }

  @Transactional(readOnly = true)
  public ReviewListResponse getReviewsBySpecialist(String specialistId, Pageable pageable) {
    log.info("Fetching reviews for specialist: {} with pagination: {}", specialistId, pageable);

    Page<Review> reviewPage = reviewRepository.findBySpecialistId(specialistId, pageable);

    log.debug("Found {} reviews for specialist {}", reviewPage.getTotalElements(), specialistId);
    return toReviewListResponse(reviewPage);
  }

  @Transactional(readOnly = true)
  public ReviewListResponse getReviewsByCustomer(String customerId, Pageable pageable) {
    log.info("Fetching reviews for customer: {} with pagination: {}", customerId, pageable);

    Page<Review> reviewPage = reviewRepository.findByCustomerId(customerId, pageable);

    log.debug("Found {} reviews for customer {}", reviewPage.getTotalElements(), customerId);
    return toReviewListResponse(reviewPage);
  }

  @Transactional(readOnly = true)
  public SpecialistReviewSummaryResponse getSpecialistReviewSummary(String specialistId) {
    log.info("Fetching review summary for specialist: {}", specialistId);

    ReviewAggregationResult stats =
        reviewRepository
            .calculateSpecialistStats(specialistId)
            .orElse(
                new ReviewAggregationResult() {
                  {
                    setAvgRating(0.0);
                    setCount(0L);
                    setRatings(List.of());
                  }
                });

    Map<Integer, Long> ratingDistribution = new HashMap<>();
    for (int i = 1; i <= 5; i++) {
      ratingDistribution.put(i, 0L);
    }

    if (stats.getRatings() != null) {
      for (Integer rating : stats.getRatings()) {
        ratingDistribution.merge(rating, 1L, Long::sum);
      }
    }

    log.debug(
        "Review summary for specialist {}: avg={}, total={}",
        specialistId,
        stats.getAvgRating(),
        stats.getCount());

    return new SpecialistReviewSummaryResponse(
        specialistId,
        stats.getAvgRating() != null ? stats.getAvgRating() : 0.0,
        stats.getCount() != null ? stats.getCount() : 0L,
        ratingDistribution);
  }

  @Transactional
  public ReviewResponse createReview(String userEmail, ReviewRequest request) {
    log.info(
        "Creating review for booking: {} by authenticated user: {}",
        request.bookingId(),
        userEmail);

    ObjectId bookingObjectId = parseBookingId(request.bookingId());
    String customerId = resolveAuthenticatedCustomerId(userEmail);

    Booking booking =
        bookingRepository
            .findById(bookingObjectId)
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", request.bookingId()));

    if (!booking.getCustomerId().equals(customerId)) {
      log.warn(
          "Customer {} attempted to review booking {} which belongs to customer {}",
          customerId,
          request.bookingId(),
          booking.getCustomerId());
      throw new BadRequestException("You can only review bookings you have made.");
    }

    if (booking.getStatus() != BookingStatus.COMPLETED) {
      log.warn(
          "Customer {} attempted to review booking {} which is not completed. Status: {}",
          customerId,
          request.bookingId(),
          booking.getStatus());
      throw new BadRequestException("You can only review completed bookings.");
    }

    if (reviewRepository.existsByBookingId(request.bookingId())) {
      log.warn("Review already exists for booking: {}", request.bookingId());
      throw new ConflictException("A review has already been submitted for this booking.");
    }

    Review review = reviewMapper.toEntity(request);
    review.setBookingId(request.bookingId());
    review.setCustomerId(customerId);
    review.setSpecialistId(booking.getSpecialistId());

    Review savedReview = reviewRepository.save(review);

    log.info("Review created successfully with id: {}", savedReview.getId());

    // TODO: Publish async event for notification-service to notify specialist of new review
    // eventPublisher.publishEvent(new ReviewCreatedEvent(savedReview));

    return reviewMapper.toResponse(savedReview);
  }

  @Transactional
  public void deleteReview(String id, String userEmail) {
    log.info("Deleting review with id: {} by authenticated user: {}", id, userEmail);

    ObjectId reviewObjectId = parseObjectId(id, "review");
    String customerId = resolveAuthenticatedCustomerId(userEmail);

    Review review =
        reviewRepository
            .findById(reviewObjectId)
            .orElseThrow(() -> ResourceNotFoundException.of("Review", id));

    if (!review.getCustomerId().equals(customerId)) {
      log.warn(
          "Customer {} attempted to delete review {} which belongs to customer {}",
          customerId,
          id,
          review.getCustomerId());
      throw new BadRequestException("You can only delete your own reviews.");
    }

    reviewRepository.delete(review);

    log.info("Review deleted successfully with id: {}", id);

    // TODO: Publish async event for notification-service to notify specialist of deleted review
    // eventPublisher.publishEvent(new ReviewDeletedEvent(review));
  }

  private ReviewListResponse toReviewListResponse(Page<Review> page) {
    return new ReviewListResponse(
        reviewMapper.toResponseList(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  private ObjectId parseBookingId(String bookingId) {
    return parseObjectId(bookingId, "booking");
  }

  private ObjectId parseObjectId(String id, String resourceName) {
    return ObjectIdParser.parse(id, resourceName);
  }

  private String resolveAuthenticatedCustomerId(String userEmail) {
    try {
      ApiResponse<com.snapserve.userclient.dto.customer.CustomerResponse> response =
          userServiceClient.getCustomerByEmail(userEmail);
      if (response == null || response.getData() == null) {
        throw new ServiceUnavailableException(
            "Unable to resolve authenticated customer. Please try again later.");
      }
      return response.getData().id();
    } catch (FeignException.NotFound e) {
      log.warn("Authenticated customer not found for email: {}", userEmail);
      throw new ResourceNotFoundException(
          "Authenticated customer not found for email: " + userEmail);
    } catch (FeignException e) {
      log.error(
          "Error resolving authenticated customer for email {}: {}", userEmail, e.getMessage());
      throw new ServiceUnavailableException(
          "Unable to resolve authenticated customer. Please try again later.");
    }
  }
}
