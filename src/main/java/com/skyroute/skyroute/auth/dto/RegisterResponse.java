package com.skyroute.skyroute.auth.dto;

import com.skyroute.skyroute.user.dto.UserResponse;

public record RegisterResponse(
        UserResponse user,
        String accessToken,
        String refreshToken,
        String tokenType) {
}
