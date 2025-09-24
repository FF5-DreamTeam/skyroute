package com.skyroute.skyroute.flight.dto.admin;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FlightRequest(
        @NotBlank(message = "Flight number is required")
        @Size(min = 3, max = 10, message = "Flight number must be between 3 and 10 characters")
        String flightNumber,

        @NotNull(message = "Available seats is required")
        @Min(value = 0, message = "Available seats cannot be negative")
        Integer availableSeats,

        @NotNull(message = "Departure time is required")
        @Future(message = "Departure time must be in the future")
        LocalDateTime departureTime,

        @NotNull(message = "Arrival time is required")
        @Future(message = "Arrival time must be in the future")
        LocalDateTime arrivalTime,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        Double price,

        @NotNull(message = "Aircraft ID is required")
        Long aircraftId,

        @NotNull(message = "Route ID is required")
        Long routeId,

        Boolean available
) {
    public static record FlightUpdate(
            @Size(min = 3, max = 10, message = "Flight number must be between 2 and 19 characters")
            String flightNumber,

            @Min(value = 0, message = "Available seats cannot be negative")
            Integer availableSeats,

            @Future(message = "Departure time must be in the future")
            LocalDateTime departureTime,

            @Future(message = "Arrival time must be in the future")
            LocalDateTime arrivalTime,

            @DecimalMin(value = " 0.0", message = "Price cannot be negative")
            Double price,

            Boolean available,

            Long aircraftId,

            Long routeId

    ) {
    }
}
