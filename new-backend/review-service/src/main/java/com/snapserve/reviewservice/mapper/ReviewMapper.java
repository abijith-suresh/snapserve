package com.snapserve.reviewservice.mapper;

import com.snapserve.reviewservice.dto.ReviewRequest;
import com.snapserve.reviewservice.dto.ReviewResponse;
import com.snapserve.reviewservice.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public Review toEntity(ReviewRequest request) {
        return Review.builder()
                .customerId(request.getCustomerId())
                .specialistId(request.getSpecialistId())
                .rating(request.getRating())
                .comment(request.getComment())
                .helpfulCount(0)
                .build();
    }

    public ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId().toHexString())
                .customerId(review.getCustomerId())
                .specialistId(review.getSpecialistId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .helpfulCount(review.getHelpfulCount())
                .build();
    }
}
