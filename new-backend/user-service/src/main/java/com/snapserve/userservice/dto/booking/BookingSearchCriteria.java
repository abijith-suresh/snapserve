package com.snapserve.userservice.dto.booking;

import com.snapserve.userservice.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSearchCriteria {
    private String customerId;
    private String specialistId;
    private BookingStatus status;
    private String service;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
