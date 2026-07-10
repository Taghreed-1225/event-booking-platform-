package com.eventbooking.eventservice.repository;

import com.eventbooking.eventservice.domain.Event;
import com.eventbooking.eventservice.domain.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByStatus(EventStatus status);
}
