package com.skyroute.skyroute.flight.dto;

import java.time.LocalDateTime;

public record FlightRequest(
        String flightNumber,
        int availableSeats,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        double price,
        boolean isAvailable
) {
}
