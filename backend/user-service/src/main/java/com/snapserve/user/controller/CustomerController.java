package com.snapserve.user.controller;

import com.snapserve.common.response.ApiResponse;
import com.snapserve.user.service.UserService;
import com.snapserve.userclient.dto.customer.CustomerListResponse;
import com.snapserve.userclient.dto.customer.CustomerRequest;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management operations")
public class CustomerController {

  private final UserService userService;

  @GetMapping
  @Operation(summary = "Get all customers", description = "Retrieve a list of all customers")
  public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
    List<CustomerResponse> customers = userService.getAllCustomers();
    return ResponseEntity.ok(ApiResponse.ok(customers));
  }

  @GetMapping("/paged")
  @Operation(
      summary = "Get customers with pagination",
      description = "Retrieve customers with pagination support")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Customers retrieved successfully",
        content = @Content(schema = @Schema(implementation = CustomerListResponse.class)))
  })
  public ResponseEntity<ApiResponse<CustomerListResponse>> getCustomersPaged(
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("GET /api/v1/customers/paged - Fetching customers with pagination: {}", pageable);
    CustomerListResponse customers = userService.getCustomers(pageable);
    return ResponseEntity.ok(ApiResponse.ok("Customers retrieved successfully", customers));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get customer by ID",
      description = "Retrieve a specific customer by their ID")
  public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
      @Parameter(description = "Customer ID") @PathVariable String id) {
    CustomerResponse customer = userService.getCustomerById(id);
    return ResponseEntity.ok(ApiResponse.ok(customer));
  }

  @PostMapping
  @Operation(summary = "Create customer", description = "Create a new customer account")
  public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
      @Valid @RequestBody CustomerRequest request) {
    CustomerResponse response = userService.createCustomer(request);
    log.info("Customer created via API: {}", request.email());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Customer created successfully", response));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update customer", description = "Update an existing customer")
  public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
      @Parameter(description = "Customer ID") @PathVariable String id,
      @Valid @RequestBody CustomerRequest request) {
    CustomerResponse response = userService.updateCustomer(id, request);
    log.info("Customer updated via API: {}", request.email());
    return ResponseEntity.ok(ApiResponse.ok("Customer updated successfully", response));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete customer", description = "Delete a customer by ID")
  public ResponseEntity<ApiResponse<Void>> deleteCustomer(
      @Parameter(description = "Customer ID") @PathVariable String id) {
    userService.deleteCustomer(id);
    log.info("Customer deleted via API: {}", id);
    return ResponseEntity.noContent().build();
  }
}
