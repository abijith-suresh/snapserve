package com.snapserve.notification.exception;

import com.snapserve.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class TemplateNotFoundException extends ApiException {

  public TemplateNotFoundException(String templateName) {
    super(HttpStatus.NOT_FOUND, "Template not found: " + templateName);
  }
}
