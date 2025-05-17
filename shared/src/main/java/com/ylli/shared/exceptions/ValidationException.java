package com.ylli.shared.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super("Validation failed");
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String field, String message) {
        super(String.format("Validation failed for %s: %s", field, message));
    }
}