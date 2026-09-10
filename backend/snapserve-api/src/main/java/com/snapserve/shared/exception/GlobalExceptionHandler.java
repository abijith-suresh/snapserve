package com.snapserve.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  ResponseEntity<ProblemDetail> handleApiException(
      ApiException exception, HttpServletRequest request) {
    return ResponseEntity.status(exception.status())
        .body(problem(exception.status(), exception.getMessage(), request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ProblemDetail> handleValidation(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    var problem = problem(HttpStatus.BAD_REQUEST, "Request validation failed", request);
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
    problem.setProperty("fieldErrors", fieldErrors);
    return ResponseEntity.badRequest().body(problem);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ProblemDetail> handleUnreadableMessage(
      HttpMessageNotReadableException exception, HttpServletRequest request) {
    return ResponseEntity.badRequest()
        .body(problem(HttpStatus.BAD_REQUEST, "Request body could not be read", request));
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ProblemDetail> handleUnexpectedException(
      Exception exception, HttpServletRequest request) {
    return ResponseEntity.internalServerError()
        .body(problem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request));
  }

  private ProblemDetail problem(HttpStatus status, String detail, HttpServletRequest request) {
    var problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setProperty("path", request.getRequestURI());
    return problem;
  }
}
