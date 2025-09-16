package com.skyroute.skyroute.route.dto;

import com.skyroute.skyroute.airport.dto.AirportResponse;

public record RouteResponse(
        Long id,
        AirportResponse origin,
        AirportResponse destination
) {}