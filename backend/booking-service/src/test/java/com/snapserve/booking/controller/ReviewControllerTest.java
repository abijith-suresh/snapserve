package com.snapserve.booking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snapserve.booking.dto.request.ReviewRequest;
import com.snapserve.booking.dto.response.ReviewResponse;
import com.snapserve.booking.service.ReviewService;
import java.lang.reflect.Constructor;
import java.time.Instant;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

  @Mock private ReviewService reviewService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() throws Exception {
    Constructor<ReviewController> constructor =
        ReviewController.class.getDeclaredConstructor(ReviewService.class);
    constructor.setAccessible(true);
    ReviewController controller = constructor.newInstance(reviewService);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void createReviewUsesTrustedUserEmailHeader() throws Exception {
    String bookingId = new ObjectId().toString();
    ReviewResponse response =
        new ReviewResponse(
            new ObjectId().toString(),
            bookingId,
            "customer-1",
            "specialist-1",
            5,
            "Great service",
            Instant.now(),
            Instant.now());
    when(reviewService.createReview(eq("customer@snapserve.com"), any(ReviewRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/reviews/")
                .header("X-User-Email", "customer@snapserve.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"bookingId":"%s","rating":5,"comment":"Great service"}
                    """
                        .formatted(bookingId)))
        .andExpect(status().isCreated());

    verify(reviewService)
        .createReview(
            eq("customer@snapserve.com"), eq(new ReviewRequest(bookingId, 5, "Great service")));
  }

  @Test
  void deleteReviewRejectsLegacyCustomerIdHeaderWithoutTrustedUserEmail() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/reviews/{id}", new ObjectId().toString())
                .header("X-Customer-Id", "spoofed-customer-id"))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(reviewService);
  }
}
