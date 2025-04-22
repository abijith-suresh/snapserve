package com.snapserve.userservice.dto.booking;

import com.snapserve.userservice.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private String id;
    private UserInfoResponse customer;
    private UserInfoResponse specialist;
    private LocalDateTime bookingDate;
    private LocalDateTime appointmentTime;
    private String service;
    private BookingStatus status;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
