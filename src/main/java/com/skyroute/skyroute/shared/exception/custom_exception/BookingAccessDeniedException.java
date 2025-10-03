package com.skyroute.skyroute.shared.exception.custom_exception;

public class BookingAccessDeniedException extends BusinessException {
    public BookingAccessDeniedException(String message) {
        super(message);
    }
}
