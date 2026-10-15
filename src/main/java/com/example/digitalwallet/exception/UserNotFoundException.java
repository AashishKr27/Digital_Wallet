package com.example.digitalwallet.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User doesn't exist with id: " + userId);
    }
}
