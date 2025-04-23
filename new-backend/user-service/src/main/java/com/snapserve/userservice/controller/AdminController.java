package com.snapserve.userservice.controller;

import com.snapserve.userservice.dto.request.AdminRequest;
import com.snapserve.userservice.dto.response.AdminResponse;
import com.snapserve.userservice.dto.response.ApiResponse;
import com.snapserve.userservice.dto.response.PagedResponse;
import com.snapserve.userservice.model.Admin;
import com.snapserve.userservice.service.GenericUserService;
import com.snapserve.userservice.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final GenericUserService<Admin, AdminRequest, AdminResponse> adminService;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminResponse>> createUser(@Valid @RequestBody AdminRequest request) {
        AdminResponse response = adminService.createUser(request);
        return ResponseBuilder.created(response, "Admin created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> getUser(@PathVariable String id) {
        AdminResponse response = adminService.getUserById(id);
        return ResponseBuilder.ok(response, "Admin retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AdminResponse>>> getAllCustomers(Pageable pageable) {
        return ResponseBuilder.ok(adminService.getAllUsers(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> updateUser(@PathVariable String id, @Valid @RequestBody AdminRequest request) {
        AdminResponse response = adminService.updateUser(id, request);
        return ResponseBuilder.ok(response, "Admin updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return ResponseBuilder.deleted("Admin deleted successfully");
    }
}
