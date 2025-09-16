package com.skyroute.skyroute.booking.exception.custom_exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
