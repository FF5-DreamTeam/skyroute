package com.skyroute.skyroute.shared.exception.custom_exception;

public class InvalidUpdateRequestException extends RuntimeException {
    public InvalidUpdateRequestException(String message) {
        super(message);
    }
}
