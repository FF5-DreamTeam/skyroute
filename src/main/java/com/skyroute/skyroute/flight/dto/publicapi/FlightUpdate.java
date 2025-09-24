package com.skyroute.skyroute.flight.dto.publicapi;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FlightUpdate(
        @Size(min = 3, max = 10, message = "Flight number must be between 3 and 10 characters")
        String flightNumber,

        @Min(value = 0, message = "Available seats cannot be negative")
        Integer availableSeats,

        @Future(message = "Arrival time must be in the future")
        LocalDateTime arrivalTime,

        @DecimalMin(value = "0.0", message = "price cannot be negative")
        Double price,

        Boolean available,

        Long aircraftId,

        Long routeId

) {
}


