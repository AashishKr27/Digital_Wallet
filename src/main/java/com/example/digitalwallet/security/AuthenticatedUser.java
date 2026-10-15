package com.example.digitalwallet.security;

import com.example.digitalwallet.exception.AuthenticationFailedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

    private Long hiddenFunction() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if(authentication == null) {
            throw new AuthenticationFailedException();
        }

        String userId = authentication.getName();

        return Long.valueOf(userId);
    }

    public Long authenticatedUserId() {
        return hiddenFunction();
    }
}
