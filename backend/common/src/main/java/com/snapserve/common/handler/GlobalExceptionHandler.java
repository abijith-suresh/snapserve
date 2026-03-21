package com.snapserve.common.handler;

import com.snapserve.common.exception.ApiException;
import com.snapserve.common.response.ErrorResponse;
import com.snapserve.common.response.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApiException(
      ApiException ex, HttpServletRequest request) {
    ErrorResponse body =
        ErrorResponse.builder()
            .status(ex.getStatus().value())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .timestamp(Instant.now())
            .build();
    return ResponseEntity.status(ex.getStatus()).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<FieldValidationError> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new FieldValidationError(fe.getField(), fe.getDefaultMessage()))
            .toList();
    ErrorResponse body =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Validation failed")
            .errors(fieldErrors)
            .path(request.getRequestURI())
            .timestamp(Instant.now())
            .build();
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    ErrorResponse body =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Malformed or missing request body")
            .path(request.getRequestURI())
            .timestamp(Instant.now())
            .build();
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResource(
      NoResourceFoundException ex, HttpServletRequest request) {
    ErrorResponse body =
        ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message("No route found for " + request.getMethod() + " " + request.getRequestURI())
            .path(request.getRequestURI())
            .timestamp(Instant.now())
            .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception at {}", request.getRequestURI(), ex);
    ErrorResponse body =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .timestamp(Instant.now())
            .build();
    return ResponseEntity.internalServerError().body(body);
  }
}
