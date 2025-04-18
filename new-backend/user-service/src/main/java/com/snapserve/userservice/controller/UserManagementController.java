package com.snapserve.userservice.controller;

import com.snapserve.userservice.dto.UserSummaryResponse;
import com.snapserve.userservice.dto.ApiResponse;
import com.snapserve.userservice.dto.PagedResponse;
import com.snapserve.userservice.service.UserManagementService;
import com.snapserve.userservice.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
