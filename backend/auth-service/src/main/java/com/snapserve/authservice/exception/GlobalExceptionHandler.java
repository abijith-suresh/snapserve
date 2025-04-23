package com.snapserve.authservice.exception;

import com.snapserve.authservice.dto.response.ApiResponse;
import com.snapserve.authservice.util.ResponseBuilder;
import jakarta.ws.rs.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleInvalidToken(InvalidTokenException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseBuilder.error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleTokenExpired(TokenExpiredException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Token has expired");
    }

    @ExceptionHandler(InvalidRoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleInvalidRole(InvalidRoleException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid role specified");
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleRefreshTokenInvalid(RefreshTokenInvalidException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid refresh token");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleBadRequest(BadRequestException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleAll(Exception ex) {
        return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}

