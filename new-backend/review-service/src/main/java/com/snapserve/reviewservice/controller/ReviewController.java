package com.snapserve.reviewservice.controller;

import com.snapserve.reviewservice.dto.ReviewRequest;
import com.snapserve.reviewservice.dto.ReviewResponse;
import com.snapserve.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        return new ResponseEntity<>(reviewService.createReview(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable String id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/specialist/{specialistId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsBySpecialist(@PathVariable String specialistId) {
        return ResponseEntity.ok(reviewService.getReviewsBySpecialist(specialistId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
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
}

