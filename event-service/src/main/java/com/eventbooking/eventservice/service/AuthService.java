package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.domain.Role;
import com.eventbooking.eventservice.domain.User;
import com.eventbooking.eventservice.exception.ApiException;
import com.eventbooking.eventservice.repository.UserRepository;
import com.eventbooking.eventservice.security.JwtService;
import com.eventbooking.eventservice.web.dto.LoginRequest;
import com.eventbooking.eventservice.web.dto.RegisterRequest;
import com.eventbooking.eventservice.web.dto.TokenResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw ApiException.conflict("USERNAME_TAKEN", "Username " + request.username() + " is already taken");
        }
        Role role = parseRole(request.role());
        User user = new User(request.username(), passwordEncoder.encode(request.password()), role);
        userRepository.save(user);
        return toTokenResponse(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> ApiException.unauthorized("INVALID_CREDENTIALS", "Invalid username or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("INVALID_CREDENTIALS", "Invalid username or password");
        }
        return toTokenResponse(user);
    }

    private TokenResponse toTokenResponse(User user) {
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new TokenResponse(token, user.getUsername(), user.getRole().name());
    }

    private Role parseRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(rawRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("INVALID_ROLE", "role must be ADMIN or USER");
        }
    }
}
