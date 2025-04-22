package com.snapserve.authservice.service;

import com.snapserve.authservice.dto.request.LoginRequest;
import com.snapserve.authservice.dto.request.RefreshTokenRequest;
import com.snapserve.authservice.dto.request.RegisterRequest;
import com.snapserve.authservice.dto.response.AuthResponse;
import com.snapserve.authservice.model.AuthUser;
import com.snapserve.authservice.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository userRepository;
    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        AuthUser user = new AuthUser();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton("ROLE_USER"));
        user.setEnabled(false);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = tokenService.generateAccessToken(user.getEmail());
        String refreshToken = tokenService.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken, 3600);
    }

    @Override
    public void verifyEmail(String token) {
        AuthUser user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenService.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = tokenService.getEmailFromToken(request.getRefreshToken());
        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = tokenService.generateAccessToken(user.getEmail());
        String newRefreshToken = tokenService.generateRefreshToken(user.getEmail());

        return new AuthResponse(newAccessToken, newRefreshToken, 3600);
    }
}