package com.skyroute.skyroute.flight.dto;

import java.time.LocalDateTime;

public record FlightResponse(
        Long id,
        String flightNumber,
        int availableSeats,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        double price,
        boolean isAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
