package com.snapserve.userservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String value) {
        super(String.format("%s with value %s not found", resource, value));
    }
}
