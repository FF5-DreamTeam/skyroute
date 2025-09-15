package com.skyroute.skyroute.airport.dto;

public record AirportResponse(
        Long id,
        String code,
        String city,
        String imageUrl
){}