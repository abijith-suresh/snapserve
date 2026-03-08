package com.snapserve.booking.dto.response;

import java.util.Map;

public record SpecialistReviewSummaryResponse(
    String specialistId,
    double averageRating,
    long totalReviews,
    Map<Integer, Long> ratingDistribution) {}
