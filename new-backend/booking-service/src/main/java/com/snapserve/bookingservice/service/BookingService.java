package com.snapserve.bookingservice.service;

import com.snapserve.bookingservice.dto.BookingRequest;
import com.snapserve.bookingservice.dto.BookingResponse;
import com.snapserve.bookingservice.dto.BookingSearchCriteria;
import com.snapserve.bookingservice.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse getBookingById(String id);
    List<BookingResponse> getAllBookings();
    BookingResponse updateBooking(String id, BookingRequest request);
    void deleteBooking(String id);
    PagedResponse<BookingResponse> getBookingsWithPaginationAndSearch(Pageable pageable, BookingSearchCriteria searchCriteria);
}
