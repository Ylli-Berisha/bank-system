package com.ylli.shared.exceptions;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Insufficient funds for this operation");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(Double available, Double required) {
        super(String.format("Insufficient funds: %.2f available, %.2f required", available, required));
    }
}