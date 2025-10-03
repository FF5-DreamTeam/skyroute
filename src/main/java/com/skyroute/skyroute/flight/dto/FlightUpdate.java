package com.skyroute.skyroute.flight.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record FlightUpdate(
        @Size(min = 3, max = 10, message = "Flight number must be between 3 and 10 characters")
        String flightNumber,

        @Min(value = 0, message = "Available seats cannot be negative")
        Integer availableSeats,

        @Future(message = "Departure time must be in the future")
        LocalDateTime departureTime,

        @Future(message = "Arrival time must be in the future")
        LocalDateTime arrivalTime,

        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        Double price,

        Boolean available,

        Long aircraftId,

        Long routeId
) {}