package com.skyroute.skyroute.flight.dto;

public record MinPriceResponse(
        String destinationCode,
        String destinationCity,
        Double minPrice) {
}
