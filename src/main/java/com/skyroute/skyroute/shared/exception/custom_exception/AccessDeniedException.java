package com.skyroute.skyroute.shared.exception.custom_exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
