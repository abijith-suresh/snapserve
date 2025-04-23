package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.response.UserDetailResponse;
import com.snapserve.userservice.dto.response.UserSummaryResponse;
import com.snapserve.userservice.dto.response.PagedResponse;
import com.snapserve.userservice.client.dto.response.BookingResponse;
import com.snapserve.userservice.client.dto.response.BookingSearchCriteria;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    PagedResponse<UserSummaryResponse> getAllUsers(String type, Pageable pageable, String search);
    UserDetailResponse getUserDetails(String id);
    void updateUserActiveStatus(String id, boolean active);
    PagedResponse<BookingResponse> getAllBookings(Pageable pageable, BookingSearchCriteria searchCriteria);
}