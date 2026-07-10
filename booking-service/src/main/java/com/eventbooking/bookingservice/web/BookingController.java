package com.eventbooking.bookingservice.web;

import com.eventbooking.bookingservice.domain.Booking;
import com.eventbooking.bookingservice.service.BookingService;
import com.eventbooking.bookingservice.web.dto.AvailabilityResponse;
import com.eventbooking.bookingservice.web.dto.BookingRequest;
import com.eventbooking.bookingservice.web.dto.BookingResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> book(@Valid @RequestBody BookingRequest request, HttpServletRequest servletRequest) {
        String attendeeName = (String) servletRequest.getAttribute("authenticatedName");
        Booking booking = bookingService.book(request, attendeeName);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(booking));
    }

    @GetMapping("/bookings/event/{eventId}")
    public List<BookingResponse> findByEvent(@PathVariable UUID eventId) {
        return bookingService.findByEvent(eventId).stream().map(BookingResponse::from).toList();
    }

    @GetMapping("/events/{eventId}/availability")
    public AvailabilityResponse availability(@PathVariable UUID eventId) {
        return bookingService.availability(eventId);
    }
}
