package com.skyroute.skyroute.flight.dto.publicapi;

import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.route.entity.Route;

import java.time.Duration;

public class FlightMapper {
    public Flight toEntity(FlightRequest request, Aircraft aircraft, Route route) {
        if (request == null) return null;

        return Flight.builder()
                .flightNumber(request.flightNumber())
                .availableSeats(request.availableSeats())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .price(request.price())
                .available(request.available() != null ? request.available() : true)
                .aircraft(aircraft)
                .route(route)
                .build();
    }

    public void updateEntityFromRequest(FlightRequest.FlightUpdate request, Flight flight, Aircraft aircraft, Route route) {
        if (request == null || flight == null) return;

        if (request.flightNumber() != null) flight.setFlightNumber(request.flightNumber());
        if (request.availableSeats() != null) flight.setAvailableSeats(request.availableSeats());
        if (request.departureTime() != null) flight.setDepartureTime(request.departureTime());
        if (request.arrivalTime() != null) flight.setArrivalTime(request.arrivalTime());
        if (request.price() != null) flight.setPrice(request.price());
        if (request.available() != null) flight.setAvailable(request.available());
        if (aircraft != null) flight.setAircraft(aircraft);
        if (route != null) flight.setRoute(route);
    }

    public FlightResponse toResponse(Flight flight) {
        if (flight == null) return null;

        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.isAvailable(),
                AircraftMapper.toDto(flight.getAircraft()),
                flight.getRoute() != null ? RouteMapper.toDto(flight.getRoute()) : null,
                flight.getCreatedAt(),
                flight.getUpdatedAt()
        );
    }

    public FlightSimpleResponse toSimpleResponse(Flight flight) {
        if (flight == null) return null;

        String aircraftModel = flight.getAircraft() != null ? flight.getAircraft().getModel() : null;
        String originCity = flight.getRoute() != null && flight.getRoute().getOrigin() != null
                ? flight.getRoute().getOrigin().getCity()
                : null;
        String destinationCity = flight.getRoute() != null && flight.getRoute().getDestination() != null
                ? flight.getRoute().getDestination().getCity()
                : null;

        return new FlightSimpleResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.isAvailable(),
                aircraftModel,
                originCity,
                destinationCity
        );
    }

    public static long calculateEstimatedDurationMinutes(Flight flight) {
        if (flight == null || flight.getDepartureTime() == null || flight.getArrivalTime() == null) {
            return 0;
        }
        return Duration.between(flight.getDepartureTime(), flight.getArrivalTime()).toMinutes();
    }
}
