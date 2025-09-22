package com.skyroute.skyroute.shared.exception.custom_exception;

public class AircraftDeletionException extends RuntimeException {
    public AircraftDeletionException(String message) {
        super(message);

    }
    public AircraftDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
