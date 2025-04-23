package com.snapserve.notificationservice.exception;

public class TemplateNotFoundException extends RuntimeException {
  public TemplateNotFoundException(String templateName) {
    super("Template '" + templateName + "' not found or failed to render.");
  }
}
