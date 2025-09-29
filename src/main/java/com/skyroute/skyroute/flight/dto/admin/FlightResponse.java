package com.skyroute.skyroute.flight.dto;

import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.route.dto.RouteResponse;

import java.time.LocalDateTime;

public record FlightResponse(
        Long id,
        String flightNumber,
        int availableSeats,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        double price,
        boolean available,
        AircraftResponse aircraft,
        RouteResponse route,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
