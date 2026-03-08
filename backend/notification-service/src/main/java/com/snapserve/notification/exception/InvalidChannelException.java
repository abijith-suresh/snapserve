package com.snapserve.notification.exception;

import com.snapserve.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidChannelException extends ApiException {

  public InvalidChannelException(String channel) {
    super(HttpStatus.BAD_REQUEST, "Invalid notification channel: " + channel);
  }
}
