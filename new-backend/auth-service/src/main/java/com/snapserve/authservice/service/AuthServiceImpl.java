package com.snapserve.authservice.service;

import com.snapserve.authservice.dto.request.LoginRequest;
import com.snapserve.authservice.dto.request.RefreshTokenRequest;
import com.snapserve.authservice.dto.request.RegisterRequest;
import com.snapserve.authservice.dto.response.AuthResponse;
import com.snapserve.authservice.exception.*;
import com.snapserve.authservice.model.AuthUser;
import com.snapserve.authservice.repository.AuthUserRepository;
import com.snapserve.authservice.security.RoleConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        Set<String> roles = request.getRoles();

        if (roles == null || roles.isEmpty() || !roles.contains(RoleConstants.ROLE_ADMIN) && !roles.contains(RoleConstants.ROLE_CUSTOMER) && !roles.contains(RoleConstants.ROLE_SPECIALIST)) {
            throw new InvalidRoleException("Invalid role specified");
        }

        AuthUser user = new AuthUser();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);
        user.setEnabled(false);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String accessToken = tokenService.generateAccessToken(user.getEmail());
        String refreshToken = tokenService.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken, 3600);
    }

    @Override
    public void verifyEmail(String token) {
        AuthUser user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token"));

        if (user.getTokenExpiry().isBefore(Instant.now())) {
            throw new TokenExpiredException("Verification token has expired");
        }

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenService.validateToken(request.getRefreshToken())) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String email = tokenService.getEmailFromToken(request.getRefreshToken());
        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken = tokenService.generateAccessToken(user.getEmail());
        String newRefreshToken = tokenService.generateRefreshToken(user.getEmail());

        return new AuthResponse(newAccessToken, newRefreshToken, 3600);
    }
}