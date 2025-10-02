package com.skyroute.skyroute.shared.exception.custom_exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NotEnoughSeatsException extends BusinessException {
    public NotEnoughSeatsException(String message) {
        super(message);
    }
}
