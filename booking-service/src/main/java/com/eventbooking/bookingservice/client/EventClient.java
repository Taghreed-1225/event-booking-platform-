package com.eventbooking.bookingservice.client;

import com.eventbooking.bookingservice.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class EventClient {

    private final RestClient restClient;

    public EventClient(RestClient.Builder builder, @Value("${event-service.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public EventDto getEvent(UUID eventId) {
        try {
            EventDto event = restClient.get()
                    .uri("/api/events/{id}", eventId)
                    .retrieve()
                    .body(EventDto.class);
            if (event == null) {
                throw ApiException.notFound("EVENT_NOT_FOUND", "Event with id " + eventId + " not found");
            }
            return event;
        } catch (HttpClientErrorException.NotFound e) {
            throw ApiException.notFound("EVENT_NOT_FOUND", "Event with id " + eventId + " not found");
        } catch (ResourceAccessException e) {
            throw ApiException.serviceUnavailable("EVENT_SERVICE_UNAVAILABLE", "event-service is currently unreachable");
        }
    }
}
