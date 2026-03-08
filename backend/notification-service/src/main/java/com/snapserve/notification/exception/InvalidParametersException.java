package com.snapserve.notification.exception;

import com.snapserve.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidParametersException extends ApiException {

  public InvalidParametersException(String message) {
    super(HttpStatus.BAD_REQUEST, "Invalid parameters: " + message);
  }
}
