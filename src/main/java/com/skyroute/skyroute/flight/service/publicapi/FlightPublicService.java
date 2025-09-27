package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;

import java.time.LocalDate;

public interface FlightPublicService {
    public Page<FlightSimpleResponse> getFlightsPage(
            Pageable pageable,
            String origin,
            String destination,
            LocalDate departureDate,
            Double minPrice,
            Double maxPrice,
            Integer passengers
    );

    Page<FlightSimpleResponse> searchFlights(FlightSearchRequest request, Pageable pageable);

    FlightSimpleResponse getFlightById(Long id);
}