package com.skyroute.skyroute.flight.service.admin;

import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.dto.admin.FlightRequest.FlightUpdate;
import com.skyroute.skyroute.flight.entity.Flight;

import java.util.List;

public interface FlightService {

    FlightResponse createFlight(FlightRequest request);

    FlightResponse updateFlight(Long id, FlightRequest request);

    void deleteFlight(Long id);

    FlightResponse getFlightById(Long id);

    List<FlightResponse> getAllFlights();

    boolean isFlightAvailable(Long flightId);

    boolean hasAvailableSeats(Long flightId, int seatsRequested);

    Flight findEntityById(Long id);
}


