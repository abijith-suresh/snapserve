package com.snapserve.common.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private Instant timestamp;

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, null, data, Instant.now());
  }

  public static <T> ApiResponse<T> ok(String message, T data) {
    return new ApiResponse<>(true, message, data, Instant.now());
  }

  public static ApiResponse<Void> ok(String message) {
    return new ApiResponse<>(true, message, null, Instant.now());
  }
}
