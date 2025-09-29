package com.skyroute.skyroute.flight.dto;

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
) {}