package com.skyroute.skyroute.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
