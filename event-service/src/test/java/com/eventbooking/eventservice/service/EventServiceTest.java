package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.domain.Event;
import com.eventbooking.eventservice.domain.EventStatus;
import com.eventbooking.eventservice.exception.ApiException;
import com.eventbooking.eventservice.repository.EventRepository;
import com.eventbooking.eventservice.web.dto.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event draftEvent;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        draftEvent = new Event("Spring Meetup", "desc", "Cairo", Instant.now().plus(1, ChronoUnit.DAYS), 100);
        eventId = UUID.randomUUID();
    }

    @Test
    void create_savesEventInDraftStatus() {
        EventRequest request = new EventRequest("Spring Meetup", "desc", "Cairo",
                Instant.now().plus(1, ChronoUnit.DAYS), 100);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event created = eventService.create(request);

        assertThat(created.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(created.getTitle()).isEqualTo("Spring Meetup");
    }

    @Test
    void publish_transitionsDraftToPublished() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(draftEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event published = eventService.publish(eventId);

        assertThat(published.getStatus()).isEqualTo(EventStatus.PUBLISHED);
    }

    @Test
    void publish_throwsWhenEventAlreadyPublished() {
        draftEvent.publish();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(draftEvent));

        assertThatThrownBy(() -> eventService.publish(eventId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void cancel_throwsWhenEventAlreadyCancelled() {
        draftEvent.cancel();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(draftEvent));

        assertThatThrownBy(() -> eventService.cancel(eventId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    void findById_throwsApiExceptionWhenNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findById(eventId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(eventId.toString());
    }
}
