package com.skyroute.skyroute.shared.exception.custom_exception;

public class InvalidRouteException extends RuntimeException {
    public InvalidRouteException(String message) {
        super(message);
    }
}
