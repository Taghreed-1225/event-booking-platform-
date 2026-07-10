package com.eventbooking.eventservice.web.dto;

import com.eventbooking.eventservice.domain.Event;
import com.eventbooking.eventservice.domain.EventStatus;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String title,
        String description,
        String venue,
        Instant startsAt,
        Integer totalSeats,
        EventStatus status
) {
    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getStartsAt(),
                event.getTotalSeats(),
                event.getStatus()
        );
    }
}
