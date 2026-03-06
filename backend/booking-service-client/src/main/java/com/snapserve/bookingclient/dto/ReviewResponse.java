package com.snapserve.bookingclient.dto;

import java.time.Instant;

public record ReviewResponse(
    String id,
    String bookingId,
    String customerId,
    String specialistId,
    Integer rating,
    String comment,
    Instant createdAt,
    Instant updatedAt) {}
