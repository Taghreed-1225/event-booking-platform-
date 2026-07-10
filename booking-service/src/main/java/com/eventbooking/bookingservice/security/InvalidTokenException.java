package com.eventbooking.bookingservice.security;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
