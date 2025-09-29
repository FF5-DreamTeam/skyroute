package com.skyroute.skyroute.flight.service;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import com.skyroute.skyroute.flight.dto.*;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.validation.FlightValidator;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.service.RouteService;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AircraftService aircraftService;
    private final RouteService routeService;
    private final FlightValidator flightValidator;
    private final FlightMapper flightMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<FlightSimpleResponse> searchFlights(
            String origin,
            String destination,
            String departureDate,
            String returnDate,
            Integer passengers,
            Pageable pageable
    ) {
        LocalDateTime departureStart = null;
        LocalDateTime departureEnd = null;

        if (departureDate != null && !departureDate.isBlank()) {
            departureStart = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .atStartOfDay();
            departureEnd = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .atTime(LocalTime.MAX);
        }

        List<Flight> flights = flightRepository.searchFlightsWithFilters(
                origin,
                destination,
                departureStart,
                departureEnd,
                null,
                null,
                passengers,
                LocalDateTime.now()
        );

        return paginateFlights(flights, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightSimpleResponse> searchFlightsByBudget(Double budget, Pageable pageable) {
        List<Flight> flights = flightRepository.searchFlightsWithFilters(
                null,
                null,
                null,
                null,
                null,
                budget,
                null,
                LocalDateTime.now()
        );

        if (flights.isEmpty()) {
            return Page.empty(pageable);
        }

        return paginateFlights(flights, pageable);
    }

    private Page<FlightSimpleResponse> paginateFlights(List<Flight> flights, Pageable pageable) {
        flights.forEach(flight -> {
            if (flight.getAvailableSeats() <= 0) {
                flight.setAvailable(false);
            }
        });

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), flights.size());

        if (start >= end) {
            return Page.empty(pageable);
        }

        List<FlightSimpleResponse> results = flights.subList(start, end).stream()
                .map(flightMapper::toSimpleResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, flights.size());
    }

    @Override
    @Transactional(readOnly = true)
    public FlightSimpleResponse getFlightSimpleById(Long id) {
        return flightMapper.toSimpleResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightSimpleResponse> getAvailableFlightsByCity(String city) {
        List<Flight> flights = flightRepository.findAvailableFlightsByCity(city, LocalDateTime.now());
        return flights.stream()
                .map(flightMapper::toSimpleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponse> getFlightsPage(Pageable pageable) {
        Page<Flight> flights = flightRepository.findAll(pageable);
        List<FlightResponse> responses = flights.stream()
                .map(flightMapper::toResponse)
                .toList();
        return new PageImpl<>(responses, pageable, flights.getTotalElements());
    }

    @Override
    @Transactional
    public FlightResponse createFlight(FlightRequest request) {
        flightValidator.validateFlight(
                request.aircraftId(),
                request.availableSeats(),
                request.departureTime(),
                request.arrivalTime()
        );

        Aircraft aircraft = aircraftService.findById(request.aircraftId())
                .orElseThrow(() -> new EntityNotFoundException("Aircraft not found with id: " + request.aircraftId()));

        Route route = routeService.findById(request.routeId())
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

        return flightMapper.toResponse(flightRepository.save(flight));
    }

    @Override
    @Transactional
    public FlightResponse updateFlight(Long id, FlightUpdate request) {
        Flight flight = findById(id);

        if (request.flightNumber() != null) flight.setFlightNumber(request.flightNumber());
        if (request.availableSeats() != null) flight.setAvailableSeats(request.availableSeats());
        if (request.departureTime() != null) flight.setDepartureTime(request.departureTime());
        if (request.arrivalTime() != null) flight.setArrivalTime(request.arrivalTime());
        if (request.price() != null) flight.setPrice(request.price());
        if (request.available() != null) flight.setAvailable(request.available());

        if (request.aircraftId() != null) {
            Aircraft aircraft = aircraftService.findById(request.aircraftId())
                    .orElseThrow(() -> new EntityNotFoundException("Aircraft not found with id: " + request.aircraftId()));
            flight.setAircraft(aircraft);
        }

        if (request.routeId() != null) {
            Route route = routeService.findById(request.routeId())
                    .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + request.routeId()));
            flight.setRoute(route);
        }

        return flightMapper.toResponse(flightRepository.save(flight));
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponse getFlightById(Long id) {
        return flightMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(flightMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {
        flightRepository.delete(findById(id));
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

}







