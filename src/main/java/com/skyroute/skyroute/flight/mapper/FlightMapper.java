package com.skyroute.skyroute.flight.mapper;

import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.entity.Flight;
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
                .available(request.isAvailable())
                .build();
    }

    public FlightResponse toResponse(Flight entity) {
        if (entity == null) return null;

        return new FlightResponse(
                entity.getId(),
                entity.getFlightNumber(),
                entity.getAvailableSeats(),
                entity.getDepartureTime(),
                entity.getArrivalTime(),
                entity.getPrice(),
                entity.isAvailable(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(FlightRequest request, Flight entity) {
        if (request == null || entity == null) return;

        entity.setFlightNumber(request.flightNumber());
        entity.setAvailableSeats(request.availableSeats());
        entity.setDepartureTime(request.departureTime());
        entity.setArrivalTime(request.arrivalTime());
        entity.setPrice(request.price());
        entity.setAvailable(request.isAvailable());
    }
}
