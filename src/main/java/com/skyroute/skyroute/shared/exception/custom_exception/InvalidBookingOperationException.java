package com.skyroute.skyroute.shared.exception.custom_exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBookingOperationException extends BusinessException {
    public InvalidBookingOperationException(String message) {
        super(message);
    }
}
