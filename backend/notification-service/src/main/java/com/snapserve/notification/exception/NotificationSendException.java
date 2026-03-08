package com.snapserve.notification.exception;

import com.snapserve.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class NotificationSendException extends ApiException {

  public NotificationSendException(String message) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send notification: " + message);
  }

  public NotificationSendException(String message, Throwable cause) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send notification: " + message);
  }
}
