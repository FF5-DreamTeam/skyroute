package com.skyroute.skyroute.route.dto;

import jakarta.validation.constraints.NotNull;

public record RouteRequest(
        @NotNull(message = "Origin airport ID cannot be null")
        Long originId,

        @NotNull(message = "Destination airport ID cannot be null")
        Long destinationId
) {}