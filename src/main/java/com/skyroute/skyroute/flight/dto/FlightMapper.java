package com.skyroute.skyroute.flight.dto;

import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightUpdate;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.dto.FlightSimpleResponse;
import org.springframework.stereotype.Component;

@Component
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

    public void updateEntityFromRequest(FlightUpdate request, Flight flight) {
        if (request == null || flight == null) return;
        flight.setFlightNumber(request.flightNumber());
        flight.setAvailableSeats(request.availableSeats());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());
        flight.setPrice(request.price());
        if (request.available() != null) flight.setAvailable(request.available());
    }

    public FlightSimpleResponse toSimpleResponse(Flight flight) {
        if (flight == null) return null;
        return new FlightSimpleResponse(flight);
    }

    public FlightResponse toResponse(Flight flight) {
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


