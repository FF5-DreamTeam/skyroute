package com.skyroute.skyroute.flight.service.admin;

import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.validation.FlightAdminValidator;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.repository.RouteRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final RouteRepository routeRepository;
    private final FlightAdminValidator flightAdminValidator;

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponse> getFlightsPage(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return flightRepository.findAll(pageRequest)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public FlightResponse createFlight(FlightRequest request) {
        flightAdminValidator.validateFlight(
                request.aircraftId(),
                request.availableSeats(),
                request.departureTime(),
                request.arrivalTime()
        );

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() -> new EntityNotFoundException("Aircraft not found with id: " + request.aircraftId()));

        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + request.routeId()));

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
    @Transactional
    public FlightResponse updateFlight(Long id, FlightRequest request) {
        flightAdminValidator.validateFlight(
                request.aircraftId(),
                request.availableSeats(),
                request.departureTime(),
                request.arrivalTime()
        );

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with id: " + id));

        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() -> new EntityNotFoundException("Aircraft not found with id: " + request.aircraftId()));

        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + request.routeId()));

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
    @Transactional(readOnly = true)
    public FlightResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with id: " + id));
        return toResponse(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with id: " + id));
        flightRepository.delete(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFlightAvailable(Long flightId) {
        return findById(flightId).isAvailable();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAvailableSeats(Long flightId, int seatsRequested) {
        return findById(flightId).getAvailableSeats() >= seatsRequested;
    }

    @Override
    @Transactional(readOnly = true)
    public Flight findById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id: " + id + " not found"));
    }

    @Override
    @Transactional
    public void bookSeats(Long flightId, int bookedSeats) {
        if (bookedSeats <= 0) {
            throw new IllegalArgumentException("Seats booked must be greater than 0");
        }

        Flight flight = findById(flightId);
        if (bookedSeats > flight.getAvailableSeats()) {
            throw new BusinessException("Not enough seats available. Requested: " + bookedSeats + ". Available: " + flight.getAvailableSeats());
        }

        flight.setAvailableSeats(flight.getAvailableSeats() - bookedSeats);
        flightRepository.save(flight);
    }

    @Override
    @Transactional
    public void releaseSeats(Long flightId, int seatsToRelease) {
        if (seatsToRelease <= 0) {
            throw new IllegalArgumentException("Seats to release must be positive");
        }

        Flight flight = findById(flightId);
        flight.setAvailableSeats(flight.getAvailableSeats() + seatsToRelease);
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






