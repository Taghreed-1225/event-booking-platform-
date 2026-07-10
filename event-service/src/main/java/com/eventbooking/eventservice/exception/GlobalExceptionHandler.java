package com.eventbooking.eventservice.exception;

import com.eventbooking.eventservice.security.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse body = new ErrorResponse(ex.getStatus().value(), ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        ErrorResponse body = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse body = new ErrorResponse(HttpStatus.CONFLICT.value(), "INVALID_STATE_TRANSITION", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation failed");
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR", "Unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
