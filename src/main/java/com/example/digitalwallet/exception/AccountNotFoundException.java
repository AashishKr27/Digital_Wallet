package com.example.digitalwallet.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long userId) {
        super("Account not found with userId: " + userId);
    }
}
