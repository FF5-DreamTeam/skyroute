package com.skyroute.skyroute.flight.dto;

import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightUpdate;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
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
                RouteMapper.toDto(flight.getRoute()),
                flight.getCreatedAt(),
                flight.getUpdatedAt()
        );
    }
}


