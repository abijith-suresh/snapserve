package com.snapserve.notificationservice.exception;

import java.io.IOException;

public class TemplateLoadException extends RuntimeException {
    public TemplateLoadException(String message, IOException e) {
        super(message);
    }
}
