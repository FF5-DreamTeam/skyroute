package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FlightPublicService {

    Page<FlightSimpleResponse> searchFlightsByParams(
            String origin,
            String destination,
            String departureDate,
            String returnDate,
            Integer passengers,
            Pageable pageable
    );

    Page<FlightSimpleResponse> searchFlightsByBudget(Double budget, Pageable pageable);

    FlightSimpleResponse getFlightById(Long id);

    List<FlightSimpleResponse> getAvailableFlightsByCity(String city);
}

