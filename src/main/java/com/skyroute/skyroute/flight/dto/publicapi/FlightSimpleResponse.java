package com.skyroute.skyroute.flight.dto.publicapi;

import java.time.LocalDateTime;

public record FlightSimpleResponse(
        Long id,
        String FlightNumber,
        int availableSeats,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        double price,
        boolean available,
        String aircraftModel,
        String originCity,
        String destinationCity

) {
}
