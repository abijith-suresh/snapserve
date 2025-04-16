package com.snapserve.reviewservice.service;

import com.snapserve.reviewservice.dto.ReviewRequest;
import com.snapserve.reviewservice.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);
    ReviewResponse getReviewById(String id);
    List<ReviewResponse> getReviewsBySpecialist(String specialistId);
    List<ReviewResponse> getReviewsByCustomer(String customerId);
    void deleteReview(String id);
    void markHelpful(String id);
}
