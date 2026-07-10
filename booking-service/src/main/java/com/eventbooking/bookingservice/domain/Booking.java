package com.eventbooking.bookingservice.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "attendee_name", nullable = false)
    private String attendeeName;

    @Column(name = "booked_at", nullable = false)
    private Instant bookedAt;

    protected Booking() {
        // required by JPA
    }

    public Booking(UUID eventId, String attendeeName) {
        this.eventId = eventId;
        this.attendeeName = attendeeName;
        this.bookedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }
}
