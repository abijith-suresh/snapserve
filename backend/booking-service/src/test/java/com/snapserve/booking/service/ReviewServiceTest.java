package com.snapserve.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.snapserve.booking.dto.request.ReviewRequest;
import com.snapserve.booking.dto.response.ReviewResponse;
import com.snapserve.booking.model.Booking;
import com.snapserve.booking.model.BookingStatus;
import com.snapserve.booking.model.Review;
import com.snapserve.booking.repository.BookingRepository;
import com.snapserve.booking.repository.ReviewRepository;
import com.snapserve.booking.service.mapper.ReviewMapper;
import com.snapserve.common.exception.BadRequestException;
import com.snapserve.common.exception.ServiceUnavailableException;
import com.snapserve.common.response.ApiResponse;
import com.snapserve.userclient.client.UserServiceClient;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock private ReviewRepository reviewRepository;
  @Mock private BookingRepository bookingRepository;
  @Mock private UserServiceClient userServiceClient;

  private ReviewService reviewService;

  @BeforeEach
  void setUp() throws Exception {
    ReviewMapper reviewMapper = Mappers.getMapper(ReviewMapper.class);
    Constructor<ReviewService> constructor =
        ReviewService.class.getDeclaredConstructor(
            ReviewRepository.class,
            BookingRepository.class,
            ReviewMapper.class,
            UserServiceClient.class);
    constructor.setAccessible(true);
    reviewService =
        constructor.newInstance(
            reviewRepository, bookingRepository, reviewMapper, userServiceClient);
  }

  @Test
  void createReviewRejectsBookingsThatAreNotCompleted() {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId());
    ReflectionTestUtils.setField(booking, "customerId", "customer-1");
    ReflectionTestUtils.setField(booking, "specialistId", "specialist-1");
    ReflectionTestUtils.setField(booking, "status", BookingStatus.CONFIRMED);

    ReviewRequest request =
        new ReviewRequest(
            ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString(),
            5,
            "Great service");

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse("customer-1", "customer@snapserve.com")));
    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));

    assertThatThrownBy(() -> reviewService.createReview("customer@snapserve.com", request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("You can only review completed bookings.");
  }

  @Test
  void createReviewUsesAuthenticatedCustomerResolvedFromTrustedEmail() {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId());
    ReflectionTestUtils.setField(booking, "customerId", "customer-1");
    ReflectionTestUtils.setField(booking, "specialistId", "specialist-99");
    ReflectionTestUtils.setField(booking, "status", BookingStatus.COMPLETED);

    Review savedReview = new Review();
    ReflectionTestUtils.setField(savedReview, "id", new ObjectId());
    ReflectionTestUtils.setField(
        savedReview,
        "bookingId",
        ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString());
    ReflectionTestUtils.setField(savedReview, "customerId", "customer-1");
    ReflectionTestUtils.setField(savedReview, "specialistId", "specialist-99");
    ReflectionTestUtils.setField(savedReview, "rating", 5);
    ReflectionTestUtils.setField(savedReview, "comment", "Great service");

    ReviewRequest request =
        new ReviewRequest(
            ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString(),
            5,
            "Great service");

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse("customer-1", "customer@snapserve.com")));
    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(reviewRepository.existsByBookingId(request.bookingId())).thenReturn(false);
    when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

    ReviewResponse response = reviewService.createReview("customer@snapserve.com", request);

    assertThat(response.specialistId()).isEqualTo("specialist-99");
    assertThat(response.customerId()).isEqualTo("customer-1");
  }

  @Test
  void createReviewRejectsBookingsOwnedByAnotherAuthenticatedCustomer() {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId());
    ReflectionTestUtils.setField(booking, "customerId", "customer-2");
    ReflectionTestUtils.setField(booking, "specialistId", "specialist-1");
    ReflectionTestUtils.setField(booking, "status", BookingStatus.COMPLETED);

    ReviewRequest request =
        new ReviewRequest(
            ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString(),
            5,
            "Great service");

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse("customer-1", "customer@snapserve.com")));
    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));

    assertThatThrownBy(() -> reviewService.createReview("customer@snapserve.com", request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("You can only review bookings you have made.");
  }

  @Test
  void createReviewRejectsMalformedBookingIdBeforeRepositoryAccess() {
    ReviewRequest request = new ReviewRequest("not-an-object-id", 5, "Great service");

    assertThatThrownBy(() -> reviewService.createReview("customer@snapserve.com", request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid booking ID format.");

    verifyNoInteractions(userServiceClient, bookingRepository, reviewRepository);
  }

  @Test
  void getReviewByIdRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(() -> reviewService.getReviewById("not-an-object-id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid review ID format.");

    verifyNoInteractions(reviewRepository);
  }

  @Test
  void deleteReviewDeletesOwnedReviewForAuthenticatedCustomer() {
    ObjectId reviewId = new ObjectId();
    Review review = new Review();
    ReflectionTestUtils.setField(review, "id", reviewId);
    ReflectionTestUtils.setField(review, "customerId", "customer-1");

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse("customer-1", "customer@snapserve.com")));
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

    reviewService.deleteReview(reviewId.toString(), "customer@snapserve.com");

    verify(reviewRepository).delete(review);
  }

  @Test
  void deleteReviewRejectsDeletingAnotherCustomersReview() {
    ObjectId reviewId = new ObjectId();
    Review review = new Review();
    ReflectionTestUtils.setField(review, "id", reviewId);
    ReflectionTestUtils.setField(review, "customerId", "customer-2");

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse("customer-1", "customer@snapserve.com")));
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

    assertThatThrownBy(
            () -> reviewService.deleteReview(reviewId.toString(), "customer@snapserve.com"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("You can only delete your own reviews.");
  }

  @Test
  void deleteReviewRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(
            () -> reviewService.deleteReview("not-an-object-id", "customer@snapserve.com"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid review ID format.");

    verifyNoInteractions(userServiceClient, reviewRepository);
  }

  @Test
  void createReviewRejectsMissingCustomerDataFromUserService() {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId());

    ReviewRequest request =
        new ReviewRequest(
            ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString(),
            5,
            "Great service");

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok("lookup succeeded", null));

    assertThatThrownBy(() -> reviewService.createReview("customer@snapserve.com", request))
        .isInstanceOf(ServiceUnavailableException.class)
        .hasMessage("Unable to resolve authenticated customer. Please try again later.");

    verifyNoInteractions(bookingRepository, reviewRepository);
  }

  private CustomerResponse customerResponse(String id, String email) {
    return new CustomerResponse(
        id,
        email,
        "Jamie Customer",
        "+15555550101",
        "123 Main St",
        "PAYPAL",
        Instant.now(),
        Instant.now());
  }
}
