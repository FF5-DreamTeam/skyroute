package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;

import java.util.List;

public interface FlightPublicService {

    List<FlightSimpleResponse> searchFlights(FlightSearchRequest request);

    FlightSimpleResponse getFlightById(Long id);

    Flight findEntityById(Long id);

    boolean isFlightAvailable(Long flightId);

    boolean hasAvailableSeats(Long flightId, int requiredSeats);

    Flight findById(Long id);

    void bookSeats(Long flightId, int bookedSeats);
}

