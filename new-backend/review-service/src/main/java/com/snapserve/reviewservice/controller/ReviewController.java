package com.snapserve.reviewservice.controller;

import com.snapserve.reviewservice.dto.ApiResponse;
import com.snapserve.reviewservice.dto.RatingSummaryResponse;
import com.snapserve.reviewservice.dto.ReviewRequest;
import com.snapserve.reviewservice.dto.ReviewResponse;
import com.snapserve.reviewservice.service.ReviewService;
import com.snapserve.reviewservice.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@Valid @RequestBody ReviewRequest request) {
        return ResponseBuilder.created(reviewService.createReview(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(@PathVariable String id) {
        return ResponseBuilder.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/specialist/{specialistId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsBySpecialist(@PathVariable String specialistId) {
        return ResponseBuilder.ok(reviewService.getReviewsBySpecialist(specialistId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByCustomer(@PathVariable String customerId) {
        return ResponseBuilder.ok(reviewService.getReviewsByCustomer(customerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<Void> markHelpful(@PathVariable String id) {
        reviewService.markHelpful(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/specialist/{specialistId}/summary")
    public ResponseEntity<ApiResponse<RatingSummaryResponse>> getRatingSummary(@PathVariable String specialistId) {
        return ResponseBuilder.ok(reviewService.getRatingSummaryForSpecialist(specialistId));
    }

}

