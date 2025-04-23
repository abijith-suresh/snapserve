package com.snapserve.authservice.service;

import com.snapserve.authservice.dto.request.LoginRequest;
import com.snapserve.authservice.dto.request.RefreshTokenRequest;
import com.snapserve.authservice.dto.request.RegisterRequest;
import com.snapserve.authservice.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyEmail(String token);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
