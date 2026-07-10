package com.eventbooking.eventservice.web;

import com.eventbooking.eventservice.domain.Event;
import com.eventbooking.eventservice.domain.EventStatus;
import com.eventbooking.eventservice.service.EventService;
import com.eventbooking.eventservice.web.dto.EventRequest;
import com.eventbooking.eventservice.web.dto.EventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        Event created = eventService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(created));
    }

    @GetMapping
    public List<EventResponse> findAll(@RequestParam(required = false) EventStatus status) {
        return eventService.findAll(status).stream().map(EventResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EventResponse findById(@PathVariable UUID id) {
        return EventResponse.from(eventService.findById(id));
    }

    @PatchMapping("/{id}/publish")
    public EventResponse publish(@PathVariable UUID id) {
        return EventResponse.from(eventService.publish(id));
    }

    @PatchMapping("/{id}/cancel")
    public EventResponse cancel(@PathVariable UUID id) {
        return EventResponse.from(eventService.cancel(id));
    }
}
