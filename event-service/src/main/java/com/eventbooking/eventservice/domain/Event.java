package com.eventbooking.eventservice.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String venue;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    protected Event() {
        // required by JPA
    }

    public Event(String title, String description, String venue, Instant startsAt, Integer totalSeats) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.startsAt = startsAt;
        this.totalSeats = totalSeats;
        this.status = EventStatus.DRAFT;
    }

    public void publish() {
        if (status != EventStatus.DRAFT) {
            throw new IllegalStateException("Only a DRAFT event can be published, current status: " + status);
        }
        this.status = EventStatus.PUBLISHED;
    }

    public void cancel() {
        if (status == EventStatus.CANCELLED) {
            throw new IllegalStateException("Event is already cancelled");
        }
        this.status = EventStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVenue() {
        return venue;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public EventStatus getStatus() {
        return status;
    }
}
