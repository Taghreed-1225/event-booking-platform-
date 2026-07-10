package com.eventbooking.bookingservice.client;

import java.util.UUID;

public record EventDto(
        UUID id,
        String title,
        Integer totalSeats,
        String status
) {
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }
}
