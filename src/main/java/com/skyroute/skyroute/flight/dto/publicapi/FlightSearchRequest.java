package com.skyroute.skyroute.flight.dto;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record FlightSearchRequest(
        @Parameter(description = "Origin airport code or city", example = "MAD")
        String origin,

        @Parameter(description = "Destination airport code or city", example = "NYC")
        String destination,

        @Parameter(description = "Departure date in format dd/MM/yyyy", example = "30/09/2025")
        @DateTimeFormat(pattern = "dd/MM/yyyy")
        LocalDate departureDate,

        @Parameter(description = "Return date in format dd/MM/yyyy", example = "26/10/2025")
        @DateTimeFormat(pattern = "dd/MM/yyyy")
        LocalDate returnDate,

        @Parameter(description = "Number of passengers", example = "2")
        Integer passengers,

        @Parameter(description = "Minimum price filter", example = "100")
        Double minPrice,

        @Parameter(description = "Maximum price filter", example = "200")
        Double maxPrice
) {
}