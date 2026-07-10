package com.eventbooking.bookingservice.web.dto;

import java.util.UUID;

public record AvailabilityResponse(
        UUID eventId,
        int totalSeats,
        long bookedSeats,
        long remainingSeats
) {
}
