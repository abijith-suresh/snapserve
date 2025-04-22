package com.snapserve.authservice.util;

import com.snapserve.authservice.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseBuilder {

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<T>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Created")
                        .data(data)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<T>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message(message)
                        .data(data)
                        .build());
    }

    public static ResponseEntity<ApiResponse<Void>> deleted() {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Deleted")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponse<Void>> deleted(String message) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(message)
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    public static ApiResponse<String> error(HttpStatus status, String message) {
        return ApiResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .data(null)
                .build();
    }

}
