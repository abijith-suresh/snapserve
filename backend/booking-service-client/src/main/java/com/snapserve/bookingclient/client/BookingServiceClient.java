package com.snapserve.bookingclient.client;

import com.snapserve.bookingclient.dto.BookingResponse;
import com.snapserve.bookingclient.dto.ReviewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", url = "${booking.service.url}")
public interface BookingServiceClient {

  @GetMapping("/api/v1/bookings/{id}")
  BookingResponse getBookingById(@PathVariable("id") String id);

  @GetMapping("/api/v1/reviews/{id}")
  ReviewResponse getReviewById(@PathVariable("id") String id);

  @GetMapping("/api/v1/reviews/booking/{bookingId}")
  ReviewResponse getReviewByBookingId(@PathVariable("bookingId") String bookingId);
}
