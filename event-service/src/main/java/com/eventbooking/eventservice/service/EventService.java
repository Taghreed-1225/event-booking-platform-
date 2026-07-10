package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.domain.Event;
import com.eventbooking.eventservice.domain.EventStatus;
import com.eventbooking.eventservice.exception.ApiException;
import com.eventbooking.eventservice.repository.EventRepository;
import com.eventbooking.eventservice.web.dto.EventRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event create(EventRequest request) {
        Event event = new Event(
                request.title(),
                request.description(),
                request.venue(),
                request.startsAt(),
                request.totalSeats()
        );
        return eventRepository.save(event);
    }

    public List<Event> findAll(EventStatus status) {
        return status == null ? eventRepository.findAll() : eventRepository.findByStatus(status);
    }

    public Event findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("EVENT_NOT_FOUND", "Event with id " + id + " not found"));
    }

    public Event publish(UUID id) {
        Event event = findById(id);
        event.publish();
        return eventRepository.save(event);
    }

    public Event cancel(UUID id) {
        Event event = findById(id);
        event.cancel();
        return eventRepository.save(event);
    }
}
