package com.eventbooking.eventservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "must not be blank") String username,
        @NotBlank(message = "must not be blank") @Size(min = 4, message = "must be at least 4 characters") String password,
        String role
) {
}
