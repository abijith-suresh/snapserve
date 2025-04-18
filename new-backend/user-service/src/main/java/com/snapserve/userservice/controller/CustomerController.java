package com.snapserve.userservice.controller;

import com.snapserve.userservice.dto.ApiResponse;
import com.snapserve.userservice.dto.CustomerRequest;
import com.snapserve.userservice.dto.CustomerResponse;
import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.service.GenericUserService;
import com.snapserve.userservice.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final GenericUserService<Customer, CustomerRequest, CustomerResponse> customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createUser(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.createUser(request);
        return ResponseBuilder.created(response, "Customer created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getUser(@PathVariable String id) {
        CustomerResponse response = customerService.getUserById(id);
        return ResponseBuilder.ok(response, "Customer retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllUsers() {
        List<CustomerResponse> responses = customerService.getAllUsers();
        return ResponseBuilder.ok(responses, "Customers retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateUser(@PathVariable String id, @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.updateUser(id, request);
        return ResponseBuilder.ok(response, "Customer updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        customerService.deleteUser(id);
        return ResponseBuilder.deleted("Customer deleted successfully");
    }
}
