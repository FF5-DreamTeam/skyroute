package com.skyroute.skyroute.flight.mapper;

import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public Flight toEntity(FlightRequest request) {
        if (request == null) {
            return null;
        }

        Flight flight = Flight.builder()
                .flightNumber(request.flightNumber())
                .availableSeats(request.availableSeats())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .price(request.price())
                .available(request.available() != null ? request.available() : true)
                .build();

        return flight;
    }

    public FlightResponse toResponse(Flight flight) {
        if (flight == null) {
            return null;
        }

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

    public void updateEntityFromRequest(FlightRequest request, Flight flight) {
        if (request == null || flight == null) {
            return;
        }

        flight.setFlightNumber(request.flightNumber());
        flight.setAvailableSeats(request.availableSeats());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());
        flight.setPrice(request.price());
        if (request.available() != null) {
            flight.setAvailable(request.available());
        }
    }

    public FlightSimpleResponse toSimpleResponse(Flight flight) {
        if (flight == null) {
            return null;
        }

        return new FlightSimpleResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.isAvailable(),
                flight.getAircraft() != null ? flight.getAircraft().getModel() : null,
                flight.getRoute() != null ? flight.getRoute().getOrigin().getCity() : null,
                flight.getRoute() != null ? flight.getRoute().getDestination().getCity() : null
        );
    }
}

