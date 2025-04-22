package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.UserDetailResponse;
import com.snapserve.userservice.dto.UserSummaryResponse;
import com.snapserve.userservice.dto.PagedResponse;
import com.snapserve.userservice.dto.booking.BookingResponse;
import com.snapserve.userservice.dto.booking.BookingSearchCriteria;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    PagedResponse<UserSummaryResponse> getAllUsers(String type, Pageable pageable, String search);
    UserDetailResponse getUserDetails(String id);
    void updateUserActiveStatus(String id, boolean active);
    PagedResponse<BookingResponse> getAllBookings(Pageable pageable, BookingSearchCriteria searchCriteria);
}