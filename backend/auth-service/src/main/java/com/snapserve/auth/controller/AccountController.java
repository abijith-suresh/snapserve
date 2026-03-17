package com.snapserve.auth.controller;

import com.snapserve.auth.dto.AuthResponse;
import com.snapserve.auth.dto.LoginRequest;
import com.snapserve.auth.dto.RefreshTokenRequest;
import com.snapserve.auth.dto.RegisterRequest;
import com.snapserve.auth.service.AccountService;
import com.snapserve.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AccountController {
  private final AccountService accountService;

  @PostMapping("/register")
  @Operation(
      summary = "Register a new user",
      description = "Create a new customer or specialist account")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "User registered successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "User with this email already exists")
  })
  public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
    log.info("POST /api/v1/auth/register - Registering user");
    accountService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("User registered successfully"));
  }

  @PostMapping("/login")
  @Operation(
      summary = "Login user",
      description = "Authenticate user and return access/refresh tokens")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid credentials")
  })
  public ResponseEntity<ApiResponse<AuthResponse>> login(
      @Valid @RequestBody LoginRequest request,
      @RequestHeader(value = "X-Device-Id", defaultValue = "unknown") String deviceId,
      @RequestHeader(value = "X-Real-IP", defaultValue = "unknown") String ipAddress) {
    log.info("POST /api/v1/auth/login - Login attempt");
    AuthResponse response = accountService.login(request, deviceId, ipAddress);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  @PostMapping("/refresh")
  @Operation(
      summary = "Refresh access token",
      description = "Use refresh token to get new access token")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Token refreshed successfully",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid or expired refresh token")
  })
  public ResponseEntity<ApiResponse<AuthResponse>> refreshAccessToken(
      @Valid @RequestBody RefreshTokenRequest request,
      @RequestHeader(value = "X-Device-Id", defaultValue = "unknown") String deviceId,
      @RequestHeader(value = "X-Real-IP", defaultValue = "unknown") String ipAddress) {
    log.info("POST /api/v1/auth/refresh - Refreshing access token");
    AuthResponse response = accountService.refreshAccessToken(request, deviceId, ipAddress);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout user", description = "Invalidate the refresh token")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Logged out successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid token")
  })
  public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
    log.info("POST /api/v1/auth/logout - Logging out user");
    accountService.logout(request.getRefreshToken());
    return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
  }
}
