package com.snapserve.userservice.client;

import com.snapserve.userservice.dto.PagedResponse;
import com.snapserve.userservice.dto.booking.BookingResponse;
import com.snapserve.userservice.enums.BookingStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "booking-service", url = "${booking.service.url}")
public interface BookingClient {

    @GetMapping("/bookings")
    PagedResponse<BookingResponse> getBookings(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "specialistId", required = false) String specialistId,
            @RequestParam(value = "status", required = false) BookingStatus status,
            @RequestParam(value = "service", required = false) String service,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    );
}
