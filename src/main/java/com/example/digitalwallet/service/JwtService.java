package com.example.digitalwallet.service;

import com.example.digitalwallet.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final String SECRET_KEY;
    private final long EXPIRY;

    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.SECRET_KEY = secretKey;
        this.EXPIRY = expiration;
    }

    public String generateToken(User user) {
        JwtBuilder builder = Jwts.builder();
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(
                StandardCharsets.UTF_8
        ));

        builder.subject(user.getId().toString());
        builder.claim("email", user.getEmail());
        builder.expiration(new Date(System.currentTimeMillis() + EXPIRY));
        builder.signWith(key);

        return builder.compact();
    }

    public boolean isTokenValid(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(
                StandardCharsets.UTF_8
        ));

        JwtParserBuilder parser = Jwts.parser();
        parser.verifyWith(key);
        JwtParser  jwtParser = parser.build();

        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

        JwtParser parser = Jwts.parser()
                .verifyWith(key)
                .build();

        return parser.parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
