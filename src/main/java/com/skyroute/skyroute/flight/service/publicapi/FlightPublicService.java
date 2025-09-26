package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightPublicService {

    FlightSimpleResponse getFlightById(Long id);

    Flight findById(Long id);

    Page<FlightSimpleResponse> searchFlights(FlightSearchRequest request, Pageable pageable);
}

