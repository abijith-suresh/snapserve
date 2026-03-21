package com.snapserve.user.controller;

import com.snapserve.common.response.ApiResponse;
import com.snapserve.user.service.UserService;
import com.snapserve.userclient.dto.specialist.SpecialistListResponse;
import com.snapserve.userclient.dto.specialist.SpecialistRequest;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
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
@RequestMapping("/api/v1/specialists")
@RequiredArgsConstructor
@Tag(name = "Specialists", description = "Specialist management operations")
public class SpecialistController {

  private final UserService userService;

  @GetMapping
  @Operation(summary = "Get all specialists", description = "Retrieve a list of all specialists")
  public ResponseEntity<ApiResponse<List<SpecialistResponse>>> getAllSpecialists() {
    List<SpecialistResponse> specialists = userService.getAllSpecialists();
    return ResponseEntity.ok(ApiResponse.ok(specialists));
  }

  @GetMapping("/paged")
  @Operation(
      summary = "Get specialists with pagination",
      description = "Retrieve specialists with pagination support")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Specialists retrieved successfully",
        content = @Content(schema = @Schema(implementation = SpecialistListResponse.class)))
  })
  public ResponseEntity<ApiResponse<SpecialistListResponse>> getSpecialistsPaged(
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("GET /api/v1/specialists/paged - Fetching specialists with pagination: {}", pageable);
    SpecialistListResponse specialists = userService.getSpecialists(pageable);
    return ResponseEntity.ok(ApiResponse.ok("Specialists retrieved successfully", specialists));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get specialist by ID",
      description = "Retrieve a specific specialist by their ID")
  public ResponseEntity<ApiResponse<SpecialistResponse>> getSpecialistById(
      @Parameter(description = "Specialist ID") @PathVariable String id) {
    SpecialistResponse specialist = userService.getSpecialistById(id);
    return ResponseEntity.ok(ApiResponse.ok(specialist));
  }

  @GetMapping("/by-service/{service}")
  @Operation(
      summary = "Get specialists by service",
      description = "Retrieve specialists who provide a specific service")
  public ResponseEntity<ApiResponse<List<SpecialistResponse>>> getSpecialistsByService(
      @Parameter(description = "Service name") @PathVariable String service) {
    List<SpecialistResponse> specialists = userService.getSpecialistsByService(service);
    return ResponseEntity.ok(ApiResponse.ok(specialists));
  }

  @GetMapping("/by-service/{service}/paged")
  @Operation(
      summary = "Get specialists by service with pagination",
      description = "Retrieve specialists who provide a specific service with pagination")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Specialists retrieved successfully",
        content = @Content(schema = @Schema(implementation = SpecialistListResponse.class)))
  })
  public ResponseEntity<ApiResponse<SpecialistListResponse>> getSpecialistsByServicePaged(
      @Parameter(description = "Service name") @PathVariable String service,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info(
        "GET /api/v1/specialists/by-service/{}/paged - Fetching specialists with pagination: {}",
        service,
        pageable);
    SpecialistListResponse specialists = userService.getSpecialistsByService(service, pageable);
    return ResponseEntity.ok(ApiResponse.ok("Specialists retrieved successfully", specialists));
  }

  @PostMapping
  @Operation(summary = "Create specialist", description = "Create a new specialist account")
  public ResponseEntity<ApiResponse<SpecialistResponse>> createSpecialist(
      @Valid @RequestBody SpecialistRequest request) {
    SpecialistResponse response = userService.createSpecialist(request);
    log.info("Specialist created via API: {}", request.email());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Specialist created successfully", response));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update specialist", description = "Update an existing specialist")
  public ResponseEntity<ApiResponse<SpecialistResponse>> updateSpecialist(
      @Parameter(description = "Specialist ID") @PathVariable String id,
      @RequestHeader("X-User-Email") String userEmail,
      @RequestHeader("X-User-Roles") String userRoles,
      @Valid @RequestBody SpecialistRequest request) {
    SpecialistResponse response = userService.updateSpecialist(id, userEmail, userRoles, request);
    log.info("Specialist updated via API: {}", request.email());
    return ResponseEntity.ok(ApiResponse.ok("Specialist updated successfully", response));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete specialist", description = "Delete a specialist by ID")
  public ResponseEntity<ApiResponse<Void>> deleteSpecialist(
      @Parameter(description = "Specialist ID") @PathVariable String id) {
    userService.deleteSpecialist(id);
    log.info("Specialist deleted via API: {}", id);
    return ResponseEntity.noContent().build();
  }
}
