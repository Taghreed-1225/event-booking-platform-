package com.eventbooking.bookingservice.repository;

import com.eventbooking.bookingservice.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByEventId(UUID eventId);

    long countByEventId(UUID eventId);

    long countByEventIdAndAttendeeName(UUID eventId, String attendeeName);
}
