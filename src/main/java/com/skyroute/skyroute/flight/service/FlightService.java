package com.skyroute.skyroute.flight.service;

import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.dto.FlightSimpleResponse;
import com.skyroute.skyroute.flight.dto.FlightUpdate;
import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FlightService {

    Page<FlightSimpleResponse> searchFlights(
            String origin,
            String destination,
            String departureDate,
            String returnDate,
            Integer passengers,
            Pageable pageable
    );

    Page<FlightSimpleResponse> searchFlightsByBudget(Double budget, Pageable pageable);

    FlightSimpleResponse getFlightSimpleById(Long id);

    List<FlightSimpleResponse> getAvailableFlightsByCity(String city);

    Page<FlightResponse> getFlightsPage(Pageable pageable);

    FlightResponse createFlight(FlightRequest request);

    FlightResponse updateFlight(Long id, FlightUpdate request);

    FlightResponse getFlightById(Long id);

    List<FlightResponse> getAllFlights();

    void deleteFlight(Long id);

    boolean isFlightAvailable(Long flightId);

    boolean hasAvailableSeats(Long flightId, int seatsRequested);

    Flight findById(Long id);

    void bookSeats(Long flightId, int bookedSeats);

    void releaseSeats(Long flightId, int seatsToRelease);
}




