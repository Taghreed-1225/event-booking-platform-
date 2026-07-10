package com.eventbooking.eventservice.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventRequest(
        @NotBlank(message = "must not be blank") String title,
        String description,
        @NotBlank(message = "must not be blank") String venue,
        @NotNull(message = "must not be null") @Future(message = "must be in the future") Instant startsAt,
        @NotNull(message = "must not be null") @Min(value = 1, message = "must be at least 1") Integer totalSeats
) {
}
