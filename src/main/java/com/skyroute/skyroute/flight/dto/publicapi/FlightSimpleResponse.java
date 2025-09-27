package com.skyroute.skyroute.flight.dto.publicapi;

import java.time.LocalDateTime;

public record FlightSimpleResponse(
        Long id,
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime departureDate,
        LocalDateTime arrivalDate,
        Double price,
        Integer availableSeats
) {
    public FlightSimpleResponse(com.skyroute.skyroute.flight.entity.Flight flight) {
        this(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getRoute() != null && flight.getRoute().getOrigin() != null
                        ? flight.getRoute().getOrigin().getCode() : null,
                flight.getRoute() != null && flight.getRoute().getDestination() != null
                        ? flight.getRoute().getDestination().getCode() : null,
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.getAvailableSeats()
        );
    }
}