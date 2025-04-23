package com.snapserve.userservice.controller;

import com.snapserve.userservice.dto.response.UserDetailResponse;
import com.snapserve.userservice.dto.response.UserSummaryResponse;
import com.snapserve.userservice.dto.response.ApiResponse;
import com.snapserve.userservice.dto.response.PagedResponse;
import com.snapserve.userservice.client.dto.response.BookingResponse;
import com.snapserve.userservice.client.dto.response.BookingSearchCriteria;
import com.snapserve.userservice.service.UserManagementService;
import com.snapserve.userservice.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping("/users")
    public ApiResponse<PagedResponse<UserSummaryResponse>> getAllUsers(
            @RequestParam(value = "type", required = false, defaultValue = "ALL") String type,
            @RequestParam(value = "search", required = false) String search,
            Pageable pageable) {
        return ResponseBuilder.ok(userManagementService.getAllUsers(type, pageable, search)).getBody();
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserDetailResponse> getUserDetails(@PathVariable String id) {
        return ResponseBuilder.ok(userManagementService.getUserDetails(id)).getBody();
    }

    @PatchMapping("/users/{id}/activate")
    public ApiResponse<String> activateUser(@PathVariable String id) {
        userManagementService.updateUserActiveStatus(id, true);
        return ResponseBuilder.ok("User activated successfully.").getBody();
    }

    @PatchMapping("/users/{id}/deactivate")
    public ApiResponse<String> deactivateUser(@PathVariable String id) {
        userManagementService.updateUserActiveStatus(id, false);
        return ResponseBuilder.ok("User deactivated successfully.").getBody();
    }

    @GetMapping("/bookings")
    public ApiResponse<PagedResponse<BookingResponse>> getAllBookings(
            BookingSearchCriteria searchCriteria,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseBuilder.ok(userManagementService.getAllBookings(pageable, searchCriteria)).getBody();
    }
}
