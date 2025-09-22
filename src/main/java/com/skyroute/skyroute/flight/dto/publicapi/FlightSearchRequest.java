package com.skyroute.skyroute.flight.dto.publicapi;

import java.time.LocalDateTime;

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
