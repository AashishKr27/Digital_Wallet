package com.example.digitalwallet.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(Long userId) {
        super("Account already exists for user: " + userId);
    }
}
