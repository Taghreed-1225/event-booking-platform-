package com.eventbooking.eventservice.security;

import com.eventbooking.eventservice.domain.Role;
import com.eventbooking.eventservice.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        return "GET".equals(method)
                || "OPTIONS".equals(method)
                || path.startsWith("/api/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Missing bearer token");
            return;
        }

        AuthenticatedUser user;
        try {
            user = jwtService.parse(header.substring(7));
        } catch (InvalidTokenException e) {
            writeError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage());
            return;
        }

        if (request.getRequestURI().startsWith("/api/events") && !Role.ADMIN.name().equals(user.role())) {
            writeError(response, HttpStatus.FORBIDDEN, "FORBIDDEN", "Only ADMIN can manage events");
            return;
        }

        request.setAttribute("authenticatedName", user.username());
        request.setAttribute("authenticatedRole", user.role());
        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String errorCode, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(status.value(), errorCode, message);
        response.getWriter().write(MAPPER.writeValueAsString(body));
    }
}
