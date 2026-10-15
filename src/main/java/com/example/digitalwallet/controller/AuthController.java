package com.example.digitalwallet.controller;

import com.example.digitalwallet.dto.request.LoginRequest;
import com.example.digitalwallet.dto.response.LoginResponse;
import com.example.digitalwallet.service.AuthService;
import com.example.digitalwallet.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService  jwtService;

    @Autowired
    public AuthController(
            AuthService authService,
            JwtService jwtService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String TOKEN =  authService.login(loginRequest);
        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setToken(TOKEN);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/test-token-id")
    public String testToken(String token) {
        return jwtService.extractUserId(token);
    }
}
