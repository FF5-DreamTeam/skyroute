package com.skyroute.skyroute.flight.helper;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightUpdate;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FlightHelper {
    private final AircraftService aircraftService;
    private final RouteService routeService;

    public Flight buildFlightFromRequest(FlightRequest request, Aircraft aircraft, Route route){
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

    public Aircraft resolveAircraftForUpdate(Long aircraftId){
        return aircraftId != null ? aircraftService.findById(aircraftId) : null;
    }

    public void applyFlightUpdates(Flight flight, FlightUpdate request, Aircraft aircraft){
        Optional.ofNullable(request.flightNumber()).ifPresent(flight::setFlightNumber);
        Optional.ofNullable(request.availableSeats()).ifPresent(flight::setAvailableSeats);
        Optional.ofNullable(request.departureTime()).ifPresent(flight::setDepartureTime);
        Optional.ofNullable(request.arrivalTime()).ifPresent(flight::setArrivalTime);
        Optional.ofNullable(request.price()).ifPresent(flight::setPrice);
        Optional.ofNullable(request.available()).ifPresent(flight::setAvailable);

        Optional.ofNullable(aircraft).ifPresent(flight::setAircraft);

        Optional.ofNullable(request.routeId())
                .map(routeService::findRouteById)
                .ifPresent(flight::setRoute);
    }
}