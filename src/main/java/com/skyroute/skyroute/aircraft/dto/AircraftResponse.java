package com.skyroute.skyroute.aircraft.dto;

public record AircraftResponse(
        Long id,
        String model,
        String manufacturer,
        Integer capacity
) {
}
