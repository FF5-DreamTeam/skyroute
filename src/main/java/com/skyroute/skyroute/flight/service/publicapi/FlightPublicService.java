package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;

import java.util.List;

public interface FlightPublicService {

    List<FlightSimpleResponse> searchFlights(FlightSearchRequest request);

    FlightSimpleResponse getFlightById(Long id);

    Flight findEntityById(Long id);

    FlightSimpleResponse reserveFirstAlternative(Long originalFlightId, int passengers);
}

