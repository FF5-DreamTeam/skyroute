package com.skyroute.skyroute.shared.exception.custom_exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}