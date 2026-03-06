package com.snapserve.auth.controller;

import com.snapserve.auth.dto.AuthResponse;
import com.snapserve.auth.dto.LoginRequest;
import com.snapserve.auth.dto.RefreshTokenRequest;
import com.snapserve.auth.dto.RegisterRequest;
import com.snapserve.auth.service.AccountService;
import com.snapserve.common.response.ApiResponse;
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
public class AccountController {
  private final AccountService accountService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
    accountService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("User registered successfully"));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(
      @Valid @RequestBody LoginRequest request,
      @RequestHeader(value = "X-Device-Id", defaultValue = "unknown") String deviceId,
      @RequestHeader(value = "X-Real-IP", defaultValue = "unknown") String ipAddress) {
    AuthResponse response = accountService.login(request, deviceId, ipAddress);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponse>> refreshAccessToken(
      @Valid @RequestBody RefreshTokenRequest request,
      @RequestHeader(value = "X-Device-Id", defaultValue = "unknown") String deviceId,
      @RequestHeader(value = "X-Real-IP", defaultValue = "unknown") String ipAddress) {
    AuthResponse response = accountService.refreshAccessToken(request, deviceId, ipAddress);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
    accountService.logout(request.getRefreshToken());
    return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
  }
}
