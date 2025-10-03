package com.skyroute.skyroute.auth.dto;

public record PasswordResetResponse(
        String message,
        boolean success) {
}
