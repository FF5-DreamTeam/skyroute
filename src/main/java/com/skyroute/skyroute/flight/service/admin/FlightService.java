package com.skyroute.skyroute.flight.service.admin;

import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FlightService {

    Page<FlightResponse> getFlightsPage(int page, int size, String sortBy, String sortDirection);

    FlightResponse createFlight(FlightRequest request);

    FlightResponse updateFlight(Long id, FlightRequest request);

    FlightResponse getFlightById(Long id);

    List<FlightResponse> getAllFlights();

    void deleteFlight(Long id);

    boolean isFlightAvailable(Long flightId);

    boolean hasAvailableSeats(Long flightId, int seatsRequested);

    Flight findById(Long id);

    void bookSeats(Long flightId, int bookedSeats);

    void releaseSeats(Long flightId, int seatsToRelease);
}



