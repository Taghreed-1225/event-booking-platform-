package com.eventbooking.bookingservice.web.dto;

import com.eventbooking.bookingservice.domain.Booking;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID eventId,
        String attendeeName,
        Instant bookedAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getEventId(), booking.getAttendeeName(), booking.getBookedAt());
    }
}
