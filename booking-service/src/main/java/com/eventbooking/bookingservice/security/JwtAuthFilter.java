package com.eventbooking.bookingservice.security;

import com.eventbooking.bookingservice.exception.ErrorResponse;
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
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeUnauthorized(response, "Missing bearer token");
            return;
        }
        try {
            String subject = jwtService.extractSubject(header.substring(7));
            request.setAttribute("authenticatedName", subject);
        } catch (InvalidTokenException e) {
            writeUnauthorized(response, e.getMessage());
            return;
        }
        chain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED", message);
        response.getWriter().write(MAPPER.writeValueAsString(body));
    }
}
