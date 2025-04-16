package com.snapserve.bookingservice.service;

import com.snapserve.bookingservice.dto.BookingRequest;
import com.snapserve.bookingservice.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse getBookingById(String id);
    List<BookingResponse> getAllBookings();
    BookingResponse updateBooking(String id, BookingRequest request);
    void deleteBooking(String id);
}
