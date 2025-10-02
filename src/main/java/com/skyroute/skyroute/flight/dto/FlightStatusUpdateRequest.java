package com.skyroute.skyroute.flight.dto;

import jakarta.validation.constraints.NotNull;

public record FlightStatusUpdateRequest(
        @NotNull(message = "Available status is required") Boolean available) {
}
