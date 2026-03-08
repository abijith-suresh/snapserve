package com.snapserve.booking.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingRequest(
    @NotBlank(message = "Customer ID is required") String customerId,
    @NotBlank(message = "Specialist ID is required") String specialistId,
    @NotNull(message = "Booking date is required")
        @Future(message = "Booking date must be in the future")
        LocalDateTime bookingDate,
    @Size(max = 1000, message = "Notes must not exceed 1000 characters") String notes,
    @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,
    @NotBlank(message = "Service type is required")
        @Size(max = 100, message = "Service type must not exceed 100 characters")
        String serviceType) {}
