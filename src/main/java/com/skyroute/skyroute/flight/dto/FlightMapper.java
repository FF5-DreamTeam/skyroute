package com.skyroute.skyroute.flight.dto;

import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.dto.RouteMapper;

public class FlightMapper {

    public Flight toEntity(FlightRequest request) {
        if (request == null) return null;
        return Flight.builder()
                .flightNumber(request.flightNumber())
                .availableSeats(request.availableSeats())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .price(request.price())
                .available(request.available() != null ? request.available() : true)
                .build();
    }

    public static FlightSimpleResponse toSimpleResponse(Flight flight) {
        return new FlightSimpleResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getRoute() != null && flight.getRoute().getOrigin() != null
                        ? flight.getRoute().getOrigin().getCity() : null,
                flight.getRoute() != null && flight.getRoute().getDestination() != null
                        ? flight.getRoute().getDestination().getCity() : null,
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.getAvailableSeats()
        );
    }

    public static FlightResponse toResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.isAvailable(),
                flight.getAircraft() != null ? AircraftMapper.toDto(flight.getAircraft()) : null,
                flight.getRoute() != null ? RouteMapper.toDto(flight.getRoute()) : null,
                flight.getCreatedAt(),
                flight.getUpdatedAt()
        );
    }
}