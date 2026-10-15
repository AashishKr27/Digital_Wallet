package com.example.digitalwallet.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException() {
        super("Authentication Failed");
    }
}
