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

