package com.skyroute.skyroute.flight.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record FlightRequest(
        @NotBlank
        @Size(min = 3, max = 10)
        String flightNumber,

        @NotNull
        @Min(0)
        Integer availableSeats,

        @NotNull
        @Future
        LocalDateTime departureTime,

        @NotNull
        @Future
        LocalDateTime arrivalTime,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        Double price,

        @NotNull
        Long aircraftId,

        @NotNull
        Long routeId,

        Boolean available
) {}