package com.snapserve.reviewservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingSummaryResponse {
    private String specialistId;
    private double averageRating;
    private long totalReviews;
}
