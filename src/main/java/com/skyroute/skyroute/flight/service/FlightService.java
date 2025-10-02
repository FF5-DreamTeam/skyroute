package com.skyroute.skyroute.flight.service;

import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.dto.FlightSimpleResponse;
import com.skyroute.skyroute.flight.dto.FlightStatusUpdateRequest;
import com.skyroute.skyroute.flight.dto.FlightUpdate;
import com.skyroute.skyroute.flight.dto.MinPriceResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightService {

    Page<FlightSimpleResponse> searchFlights(
            Optional<String> origin,
            Optional<String> destination,
            Optional<String> departureDate,
            Optional<Integer> passengers,
            Pageable pageable);

    FlightSimpleResponse getFlightSimpleById(Long id);

    Page<FlightSimpleResponse> searchFlightsByBudgetAndCity(
            Optional<String> origin,
            Optional<String> destination,
            Optional<Double> budget,
            Pageable pageable
    );

    Page<FlightResponse> getFlightsPage(Pageable pageable);

    FlightResponse createFlight(FlightRequest request);

    FlightResponse updateFlight(Long id, FlightUpdate request);

    FlightResponse getFlightById(Long id);

    void deleteFlight(Long id);

    boolean isFlightAvailable(Long flightId);

    boolean hasAvailableSeats(Long flightId, int seatsRequested);

    Flight findById(Long id);

    void bookSeats(Long flightId, int bookedSeats);

    void releaseSeats(Long flightId, int seatsToRelease);

    List<MinPriceResponse> getMinPricesByDestinations(List<String> destinationCodes);

    int markFlightsAsUnavailableAndReleaseSeats(LocalDateTime now);

    FlightResponse updateFlightStatus(Long id, FlightStatusUpdateRequest request);

}