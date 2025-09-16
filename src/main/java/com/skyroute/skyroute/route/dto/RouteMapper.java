package com.skyroute.skyroute.route.dto;

import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.route.entity.Route;

public class RouteMapper {
    public static Route toEntity(RouteRequest dto, Airport origin, Airport destination){
        return Route.builder()
                .origin(origin)
                .destination(destination)
                .build();
    }

    public static RouteResponse toDto(Route route){
        return new RouteResponse(
                route.getId(),
                new AirportResponse(
                        route.getOrigin().getId(),
                        route.getOrigin().getCode(),
                        route.getOrigin().getCity(),
                        route.getOrigin().getImageUrl()
                ),
                new AirportResponse(
                        route.getDestination().getId(),
                        route.getDestination().getCode(),
                        route.getDestination().getCity(),
                        route.getDestination().getImageUrl()
                )
        );
    }
}