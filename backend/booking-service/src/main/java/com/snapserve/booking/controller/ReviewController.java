package com.snapserve.booking.controller;

import com.snapserve.booking.dto.ReviewDto;
import com.snapserve.booking.dto.SpecialistReviewResponseDto;
import com.snapserve.booking.model.Review;
import com.snapserve.booking.service.ReviewService;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

  @Autowired private ReviewService reviewService;

  @GetMapping
  public ResponseEntity<List<ReviewDto>> getAllReviews() {
    return ResponseEntity.ok(reviewService.findAllReviews());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Review> getReviewById(@PathVariable String id) {
    Review review = reviewService.findReviewById(new ObjectId(id));
    if (review == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(review);
  }

  @PostMapping
  public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Review> updateReview(@PathVariable String id, @RequestBody Review review) {
    return ResponseEntity.ok(reviewService.updateReview(new ObjectId(id), review));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReview(@PathVariable String id) {
    reviewService.deleteReviewById(new ObjectId(id));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/customer/{customerId}/reviews")
  public ResponseEntity<List<ReviewDto>> getReviewsByCustomer(@PathVariable String customerId) {
    return ResponseEntity.ok(reviewService.getReviewsByCustomer(new ObjectId(customerId)));
  }

  @GetMapping("/specialist/{specialistId}/reviews")
  public ResponseEntity<List<SpecialistReviewResponseDto>> getReviewsForSpecialist(
      @PathVariable String specialistId) {
    return ResponseEntity.ok(reviewService.getReviewsForSpecialist(new ObjectId(specialistId)));
  }
}
