package com.snapserve.bookingservice.repository;

import com.snapserve.bookingservice.dto.BookingResponse;
import com.snapserve.bookingservice.dto.BookingSearchCriteria;
import com.snapserve.bookingservice.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CustomBookingRepository {
    PagedResponse<BookingResponse> searchBookings(Pageable pageable, BookingSearchCriteria criteria);
}