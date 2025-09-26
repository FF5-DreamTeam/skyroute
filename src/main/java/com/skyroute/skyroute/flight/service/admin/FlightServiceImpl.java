package com.skyroute.skyroute.flight.service.admin;

import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.validation.FlightValidator;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.repository.RouteRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Builder
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final RouteRepository routeRepository;
    private final FlightValidator flightValidator;

    @Override
    @Transactional
    public FlightResponse createFlight(FlightRequest request) {
        flightValidator.validateFlight(
                request.aircraftId(),
                request.availableSeats(),
                request.departureTime(),
                request.arrivalTime()
        );

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aircraft not found with id: " + request.aircraftId()
                ));

        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Route not found with id: " + request.routeId()
                ));

        Flight flight = Flight.builder()
                .flightNumber(request.flightNumber())
                .availableSeats(request.availableSeats())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .price(request.price())
                .available(request.available() != null ? request.available() : true)
                .aircraft(aircraft)
                .route(route)
                .build();

        return toResponse(flightRepository.save(flight));
    }

    @Override
    public FlightResponse updateFlight(Long id, FlightRequest request) {
        flightValidator.validateFlight(
                request.aircraftId(),
                request.availableSeats(),
                request.departureTime(),
                request.arrivalTime()
        );

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Flight not found with id: " + id
                ));

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aircraft not found with id: " + request.aircraftId()
                ));

        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Route not found with id: " + request.routeId()
                ));

        flight.setFlightNumber(request.flightNumber());
        flight.setAvailableSeats(request.availableSeats());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());
        flight.setPrice(request.price());
        flight.setAvailable(request.available() != null ? request.available() : true);
        flight.setAircraft(aircraft);
        flight.setRoute(route);

        return toResponse(flightRepository.save(flight));
    }

    @Override
    public FlightResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with id: " + id));
        return toResponse(flight);
    }

    @Override
    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FlightResponse> getFlightsPage(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with id: " + id));
        flightRepository.delete(flight);
    }

    @Override
    public boolean isFlightAvailable(Long flightId) {
        Flight flight = findById(flightId);
        return flight.isAvailable();
    }

    @Override
    public boolean hasAvailableSeats(Long flightId, int seatsRequested) {
        Flight flight = findById(flightId);
        return flight.getAvailableSeats() >= seatsRequested;
    }

    @Override
    public Flight findById(Long id) {
        return flightRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Flight with id: " + id + " not found"));
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

    @Override
    public void releaseSeats(Long flightId, int seatsToRelease) {
        if (seatsToRelease <= 0) {
            throw new IllegalArgumentException("Seats to release must be positive");
        }

        Flight flight = findById(flightId);
        int updatedSeats = flight.getAvailableSeats() + seatsToRelease;
        flight.setAvailableSeats(updatedSeats);
        flightRepository.save(flight);
    }

    private FlightResponse toResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.isAvailable(),
                flight.getAircraft() != null ? AircraftMapper.toDto(flight.getAircraft()) : null,
                flight.getRoute() != null ? RouteMapper.toDto(flight.getRoute()) : null,
                flight.getCreatedAt(),
                flight.getUpdatedAt()
        );
    }
}




