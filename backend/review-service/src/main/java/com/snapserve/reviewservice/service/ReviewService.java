package com.snapserve.reviewservice.service;

import com.snapserve.reviewservice.dto.RatingSummaryResponse;
import com.snapserve.reviewservice.dto.ReviewRequest;
import com.snapserve.reviewservice.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);
    ReviewResponse getReviewById(String id);
    Page<ReviewResponse> getReviewsBySpecialist(String specialistId, Pageable pageable);
    Page<ReviewResponse> getReviewsByCustomer(String customerId, Pageable pageable);
    void deleteReview(String id);
    void markHelpful(String id);
    RatingSummaryResponse getRatingSummaryForSpecialist(String specialistId);
}
