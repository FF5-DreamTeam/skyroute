package com.skyroute.skyroute.flight.dto;

import java.time.LocalDateTime;

public record FlightSearch(
        String origin,
        String destination,
        LocalDateTime departureDate,
        LocalDateTime returnDate,
        Integer passengers,
        Double minPrice,
        Double maxPrice

) {
}
