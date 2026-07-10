package com.eventbooking.eventservice.security;

import com.eventbooking.eventservice.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtService {

    private static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-minutes:60}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String username, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(ROLE_CLAIM, role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public AuthenticatedUser parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return new AuthenticatedUser(claims.getSubject(), claims.get(ROLE_CLAIM, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }
}
