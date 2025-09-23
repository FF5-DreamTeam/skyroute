package com.skyroute.skyroute.flight.dto.publicapi;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FlightSimpleResponse(
        Long id,
        String flightNumber,
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
