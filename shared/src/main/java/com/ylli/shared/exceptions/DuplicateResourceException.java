package com.ylli.shared.exceptions;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException() {
        super("Resource already exists");
    }

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}