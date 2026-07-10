package com.eventbooking.eventservice.exception;

public record ErrorResponse(int status, String errorCode, String message) {
}
