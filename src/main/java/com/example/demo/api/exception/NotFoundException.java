package com.example.demo.api.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resourceName) {
        super(String.format("Resource not found: %s", resourceName));
    }
}
