package com.eventbooking.eventservice.web.dto;

public record TokenResponse(String token, String username, String role) {
}
