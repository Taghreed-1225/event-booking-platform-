package com.eventbooking.eventservice.security;

public record AuthenticatedUser(String username, String role) {
}
