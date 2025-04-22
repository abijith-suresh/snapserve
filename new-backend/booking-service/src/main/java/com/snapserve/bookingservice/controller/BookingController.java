package com.snapserve.bookingservice.controller;

import com.snapserve.bookingservice.dto.*;
import com.snapserve.bookingservice.model.BookingStatus;
import com.snapserve.bookingservice.service.BookingService;
import com.snapserve.bookingservice.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseBuilder.created(bookingService.createBooking(request), "Booking created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable String id) {
        return ResponseBuilder.ok(bookingService.getBookingById(id), "Booking fetched successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        return ResponseBuilder.ok(bookingService.getAllBookings(), "All bookings fetched successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(@PathVariable String id, @Valid @RequestBody BookingRequest request) {
        return ResponseBuilder.ok(bookingService.updateBooking(id, request), "Booking updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
        return ResponseBuilder.deleted("Booking deleted successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>> getBookingsWithPaginationAndSearch(
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "specialistId", required = false) String specialistId,
            @RequestParam(value = "status", required = false) BookingStatus status,
            @RequestParam(value = "service", required = false) String service,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Pageable pageable) {

        BookingSearchCriteria searchCriteria = BookingSearchCriteria.builder()
                .customerId(customerId)
                .specialistId(specialistId)
                .status(status)
                .service(service)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        PagedResponse<BookingResponse> bookings = bookingService.getBookingsWithPaginationAndSearch(pageable, searchCriteria);

        return ResponseBuilder.ok(bookings);
    }
}
