package com.skyroute.skyroute.flight.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record FlightUpdate(
        @Size(min = 3, max = 10)
        String flightNumber,

        @Min(0)
        Integer availableSeats,

        @Future
        LocalDateTime arrivalTime,

        @DecimalMin(value = "0.0")
        Double price,

        Boolean available,

        Long aircraftId,
        Long routeId
) {
}


