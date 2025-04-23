package com.snapserve.bookingservice.dto;

import com.snapserve.bookingservice.model.BookingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    @NotNull
    private String customerId;

    @NotNull
    private String specialistId;

    @NotNull
    private LocalDateTime bookingDate;

    @NotNull
    private LocalDateTime appointmentTime;

    @NotBlank
    private String service;

    @NotNull
    private BookingStatus status;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;
}