package com.eventbooking.bookingservice.service;

import com.eventbooking.bookingservice.client.EventClient;
import com.eventbooking.bookingservice.client.EventDto;
import com.eventbooking.bookingservice.domain.Booking;
import com.eventbooking.bookingservice.exception.ApiException;
import com.eventbooking.bookingservice.repository.BookingRepository;
import com.eventbooking.bookingservice.web.dto.AvailabilityResponse;
import com.eventbooking.bookingservice.web.dto.BookingRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private static final int MAX_SEATS_PER_USER_PER_EVENT = 2;

    private final BookingRepository bookingRepository;
    private final EventClient eventClient;

    public BookingService(BookingRepository bookingRepository, EventClient eventClient) {
        this.bookingRepository = bookingRepository;
        this.eventClient = eventClient;
    }

    public Booking book(BookingRequest request, String attendeeName) {
        EventDto event = eventClient.getEvent(request.eventId());

        if (!event.isPublished()) {
            throw ApiException.conflict("EVENT_NOT_PUBLISHED",
                    "Event with id " + request.eventId() + " is not published, current status: " + event.status());
        }

        long bookedByUser = bookingRepository.countByEventIdAndAttendeeName(request.eventId(), attendeeName);
        if (bookedByUser >= MAX_SEATS_PER_USER_PER_EVENT) {
            throw ApiException.conflict("MAX_SEATS_PER_USER_EXCEEDED",
                    "You already booked the maximum of " + MAX_SEATS_PER_USER_PER_EVENT + " seats for this event");
        }

        long booked = bookingRepository.countByEventId(request.eventId());
        if (booked >= event.totalSeats()) {
            throw ApiException.conflict("NO_SEATS_AVAILABLE",
                    "No seats available for event " + request.eventId());
        }

        Booking booking = new Booking(request.eventId(), attendeeName);
        return bookingRepository.save(booking);
    }

    public List<Booking> findByEvent(UUID eventId) {
        return bookingRepository.findByEventId(eventId);
    }

    public AvailabilityResponse availability(UUID eventId) {
        EventDto event = eventClient.getEvent(eventId);
        long booked = bookingRepository.countByEventId(eventId);
        long remaining = Math.max(0, event.totalSeats() - booked);
        return new AvailabilityResponse(eventId, event.totalSeats(), booked, remaining);
    }
}
