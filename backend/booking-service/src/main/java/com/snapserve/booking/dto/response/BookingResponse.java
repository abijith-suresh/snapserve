package com.snapserve.booking.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record BookingResponse(
    String id,
    String customerId,
    String specialistId,
    LocalDateTime bookingDate,
    String status,
    String notes,
    BigDecimal price,
    String serviceType,
    Instant createdAt,
    Instant updatedAt) {}
