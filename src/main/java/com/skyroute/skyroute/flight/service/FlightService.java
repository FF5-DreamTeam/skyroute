package com.skyroute.skyroute.flight.service;

import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightResponse;

import java.util.List;

public interface FlightService {
    FlightResponse createFlight(FlightRequest request);

    FlightResponse updateFlight(Long id, FlightRequest request);

    FlightResponse getFlightById(Long id);

    List<FlightResponse> getAllFlights();

    void deleteFlight(Long id);

}

