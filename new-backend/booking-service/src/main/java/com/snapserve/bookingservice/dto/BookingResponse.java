package com.snapserve.bookingservice.dto;

import com.snapserve.bookingservice.model.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private String id;
    private String customerId;
    private String specialistId;
    private LocalDateTime bookingDate;
    private LocalDateTime appointmentTime;
    private String service;
    private BookingStatus status;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
