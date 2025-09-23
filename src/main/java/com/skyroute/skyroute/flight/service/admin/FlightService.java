package com.skyroute.skyroute.flight.service.admin;

import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
<<<<<<< HEAD:src/main/java/com/skyroute/skyroute/flight/service/FlightService.java
=======
import com.skyroute.skyroute.flight.dto.admin.FlightRequest.FlightUpdate;
>>>>>>> dev:src/main/java/com/skyroute/skyroute/flight/service/admin/FlightService.java
import com.skyroute.skyroute.flight.entity.Flight;

import java.util.List;

public interface FlightService {

    FlightResponse createFlight(FlightRequest request);

    FlightResponse updateFlight(Long id, FlightRequest request);

    FlightResponse updateFlight(Long id, FlightUpdate request);

    void deleteFlight(Long id);

    FlightResponse getFlightById(Long id);

    List<FlightResponse> getAllFlights();

    boolean isFlightAvailable(Long flightId);

<<<<<<< HEAD:src/main/java/com/skyroute/skyroute/flight/service/FlightService.java
    boolean isFlightAvailable(Long flightId);

    boolean hasAvailableSeats(Long flightId, int requiredSeats);

    Flight findById(Long id);

    void bookSeats(Long flightId, int seatsBooked);
=======
    boolean hasAvailableSeats(Long flightId, int seatsRequested);

    Flight findEntityById(Long id);
>>>>>>> dev:src/main/java/com/skyroute/skyroute/flight/service/admin/FlightService.java
}


