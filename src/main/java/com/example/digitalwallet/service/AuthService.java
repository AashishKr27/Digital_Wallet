package com.example.digitalwallet.service;

import com.example.digitalwallet.dto.request.LoginRequest;
import com.example.digitalwallet.entity.User;
import com.example.digitalwallet.exception.InvalidCredentialsException;
import com.example.digitalwallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.getEmail()).get();

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return jwtService.generateToken(user);
    }
}
