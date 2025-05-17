package com.ylli.shared.exceptions;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Account is currently locked");
    }

    public AccountLockedException(String message) {
        super(message);
    }

    public AccountLockedException(String accountId, String reason) {
        super(String.format("Account %s is locked: %s", accountId, reason));
    }
}