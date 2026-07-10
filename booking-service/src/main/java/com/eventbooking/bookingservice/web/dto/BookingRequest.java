package com.eventbooking.bookingservice.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingRequest(
        @NotNull(message = "must not be null") UUID eventId
) {
}
