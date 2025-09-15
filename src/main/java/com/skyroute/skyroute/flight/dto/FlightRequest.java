package com.skyroute.skyroute.flight.dto;

import java.time.LocalDateTime;

public record FlightRequest(
        String flightNumber,
        int availablesSeats,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        double price,
        boolean isAvailable
) {
}
