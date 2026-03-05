package com.snapserve.common.response;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
  private int status;
  private String message;
  private List<FieldValidationError> errors;
  private String path;
  private Instant timestamp;
}
