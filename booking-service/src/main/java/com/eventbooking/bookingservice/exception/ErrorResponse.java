package com.eventbooking.bookingservice.exception;

public record ErrorResponse(int status, String errorCode, String message) {
}
