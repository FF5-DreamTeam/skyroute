package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FlightPublicServiceImpl implements FlightPublicService {

    private final FlightRepository flightRepository;

    @Override
    public List<FlightSimpleResponse> searchFlights(FlightSearchRequest request) {
        LocalDateTime now = LocalDateTime.now();

        List<Flight> flights = flightRepository.searchFlightsWithFilters(
                request.origin(),
                request.destination(),
                request.departureDate(),
                request.minPrice(),
                request.maxPrice(),
                request.passengers(),
                now
        );

        return flights.stream()
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FlightSimpleResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id:  not found" + id));
        return toSimpleResponse(flight);
    }

    @Override
    public Flight findEntityById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id:  not found" + id));
    }

    @Override
    public boolean isFlightAvailable(Long flightId) {
        Flight flight = findEntityById(flightId);
        return flight.isAvailable();
    }

    @Override
    public boolean hasAvailableSeats(Long flightId, int seatsRequested) {
        Flight flight = findEntityById(flightId);
        return flight.getAvailableSeats() >= seatsRequested;
    }

    @Override
    public Flight findById(Long id) {
        return flightRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Flight with id: " + id+ " not found"));
    }

    @Override
    public void bookSeats(Long flightId, int bookedSeats) {
        if (bookedSeats <= 0) {
            throw new IllegalArgumentException("Seats booked must be greater than 0");
        }

        Flight flight = findById(flightId);
        int availableSeats = flight.getAvailableSeats();

        if (bookedSeats > availableSeats) {
            throw new BusinessException("Not enought seats available. Requested: " + bookedSeats + ". Available: " + availableSeats);
        }

        flight.setAvailableSeats(availableSeats - bookedSeats);
        flightRepository.save(flight);
    }

    private FlightSimpleResponse toSimpleResponse(Flight flight) {
        return new FlightSimpleResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.isAvailable(),
                flight.getAircraft() != null ? flight.getAircraft().getModel() : null,
                flight.getRoute() != null && flight.getRoute().getOrigin() != null
                        ? flight.getRoute().getOrigin().getCity() : null,
                flight.getRoute() != null && flight.getRoute().getDestination() != null
                        ? flight.getRoute().getDestination().getCity() : null
        );
    }
}
