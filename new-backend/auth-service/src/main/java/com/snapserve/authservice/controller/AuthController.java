package com.snapserve.authservice.controller;

import com.snapserve.authservice.dto.request.LoginRequest;
import com.snapserve.authservice.dto.request.RefreshTokenRequest;
import com.snapserve.authservice.dto.request.RegisterRequest;
import com.snapserve.authservice.dto.response.ApiResponse;
import com.snapserve.authservice.dto.response.AuthResponse;
import com.snapserve.authservice.service.AuthService;
import com.snapserve.authservice.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseBuilder.ok(null, "Registration successful—check your email");
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseBuilder.ok(null, "Email verified successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseBuilder.ok(authService.login(req), "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return ResponseBuilder.ok(authService.refreshToken(req), "Token refreshed");
    }
}
