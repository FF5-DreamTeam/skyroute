package com.skyroute.skyroute.flight.dto.publicapi;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FlightSearchRequest(
        String origin,
        String destination,
        LocalDateTime departureDate,
        LocalDateTime returnDate,
        Integer passengers,
        Double minPrice,
        Double maxPrice

) {
}
