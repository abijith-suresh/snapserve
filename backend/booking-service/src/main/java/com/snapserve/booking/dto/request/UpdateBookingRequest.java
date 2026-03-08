package com.snapserve.booking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdateBookingRequest(
    @Future(message = "Booking date must be in the future") LocalDateTime bookingDate,
    @Pattern(
            regexp = "PENDING|CONFIRMED|CANCELLED|COMPLETED",
            message = "Invalid status. Must be one of: PENDING, CONFIRMED, CANCELLED, COMPLETED")
        String status,
    @Size(max = 1000, message = "Notes must not exceed 1000 characters") String notes) {}
