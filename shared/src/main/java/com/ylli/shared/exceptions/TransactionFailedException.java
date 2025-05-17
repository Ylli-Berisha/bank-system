package com.ylli.shared.exceptions;

public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException() {
        super("Transaction failed to complete");
    }

    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}