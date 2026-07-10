package com.eventbooking.bookingservice.service;

import com.eventbooking.bookingservice.client.EventClient;
import com.eventbooking.bookingservice.client.EventDto;
import com.eventbooking.bookingservice.domain.Booking;
import com.eventbooking.bookingservice.exception.ApiException;
import com.eventbooking.bookingservice.repository.BookingRepository;
import com.eventbooking.bookingservice.web.dto.AvailabilityResponse;
import com.eventbooking.bookingservice.web.dto.BookingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventClient eventClient;

    @InjectMocks
    private BookingService bookingService;

    private final UUID eventId = UUID.randomUUID();

    @Test
    void book_succeedsWhenEventPublishedAndSeatsAvailable() {
        EventDto event = new EventDto(eventId, "Spring Meetup", 100, "PUBLISHED");
        BookingRequest request = new BookingRequest(eventId);
        when(eventClient.getEvent(eventId)).thenReturn(event);
        when(bookingRepository.countByEventId(eventId)).thenReturn(5L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.book(request, "Taghreed");

        assertThat(booking.getAttendeeName()).isEqualTo("Taghreed");
        assertThat(booking.getEventId()).isEqualTo(eventId);
    }

    @Test
    void book_throwsWhenEventNotPublished() {
        EventDto event = new EventDto(eventId, "Spring Meetup", 100, "DRAFT");
        BookingRequest request = new BookingRequest(eventId);
        when(eventClient.getEvent(eventId)).thenReturn(event);

        assertThatThrownBy(() -> bookingService.book(request, "Taghreed"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not published");
    }

    @Test
    void book_throwsWhenNoSeatsAvailable() {
        EventDto event = new EventDto(eventId, "Spring Meetup", 10, "PUBLISHED");
        BookingRequest request = new BookingRequest(eventId);
        when(eventClient.getEvent(eventId)).thenReturn(event);
        when(bookingRepository.countByEventId(eventId)).thenReturn(10L);

        assertThatThrownBy(() -> bookingService.book(request, "Taghreed"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("No seats available");
    }

    @Test
    void book_throwsWhenUserAlreadyBookedMaxSeatsForEvent() {
        EventDto event = new EventDto(eventId, "Spring Meetup", 100, "PUBLISHED");
        BookingRequest request = new BookingRequest(eventId);
        when(eventClient.getEvent(eventId)).thenReturn(event);
        when(bookingRepository.countByEventIdAndAttendeeName(eventId, "Taghreed")).thenReturn(2L);

        assertThatThrownBy(() -> bookingService.book(request, "Taghreed"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("maximum");
    }

    @Test
    void availability_returnsRemainingSeats() {
        EventDto event = new EventDto(eventId, "Spring Meetup", 100, "PUBLISHED");
        when(eventClient.getEvent(eventId)).thenReturn(event);
        when(bookingRepository.countByEventId(eventId)).thenReturn(30L);

        AvailabilityResponse response = bookingService.availability(eventId);

        assertThat(response.remainingSeats()).isEqualTo(70);
        assertThat(response.bookedSeats()).isEqualTo(30);
    }
}
