package com.skyroute.skyroute.flight.service;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import com.skyroute.skyroute.flight.dto.*;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.helper.FlightHelper;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.specification.FlightSpecification;
import com.skyroute.skyroute.flight.specification.FlightSpecificationBuilder;
import com.skyroute.skyroute.flight.validation.FlightValidator;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.service.RouteService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AircraftService aircraftService;
    private final RouteService routeService;
    private final FlightValidator flightValidator;
    private final FlightHelper flightHelper;

    @Override
    @Transactional(readOnly = true)
    public Page<FlightSimpleResponse> searchFlights(
            Optional<String> origin,
            Optional<String> destination,
            Optional<String> departureDate,
            Optional<Integer> passengers,
            Pageable pageable) {
        Specification<Flight> specification = FlightSpecificationBuilder.builder()
                .originEquals(origin)
                .destinationEquals(destination)
                .departureDateEquals(departureDate)
                .passengersAvailable(passengers)
                .build();

        return flightRepository.findAll(specification, pageable)
                .map(FlightMapper::toSimpleResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightSimpleResponse getFlightSimpleById(Long id) {
        return FlightMapper.toSimpleResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightSimpleResponse> searchFlightsByBudgetAndCity(
            Optional<String> origin,
            Optional<String> destination,
            Optional<Double> budget,
            Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        Specification<Flight> specification = FlightSpecificationBuilder.builder()
                .originEquals(origin)
                .destinationEquals(destination)
                .pricelessThanOrEqual(budget)
                .onlyAvailable(Optional.of(now), true)
                .build();
        return flightRepository.findAll(specification, pageable)
                .map(FlightMapper::toSimpleResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponse> getFlightsPage(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(FlightMapper::toResponse);
    }

    @Override
    @Transactional
    public FlightResponse createFlight(FlightRequest request) {
        Aircraft aircraft = aircraftService.findById(request.aircraftId());
        Route route = routeService.findRouteById(request.routeId());

        flightValidator.validateFlightCreation(
                aircraft,
                request.availableSeats(),
                request.departureTime(),
                request.arrivalTime());

        Flight flight = flightHelper.buildFlightFromRequest(request, aircraft, route);

        return FlightMapper.toResponse(flightRepository.save(flight));
    }

    @Override
    @Transactional
    public FlightResponse updateFlight(Long id, FlightUpdate request) {
        Flight flight = findById(id);
        Aircraft aircraft = flightHelper.resolveAircraftForUpdate(request.aircraftId());

        validateFlightUpdate(flight, aircraft, request);
        flightHelper.applyFlightUpdates(flight, request, aircraft);
        return FlightMapper.toResponse(flightRepository.save(flight));
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponse getFlightById(Long id) {
        return FlightMapper.toResponse(findById(id));
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
        Flight flight = findById(flightId);

        flightValidator.validateSeatsToBook(flight, bookedSeats);

        flight.setAvailableSeats(flight.getAvailableSeats() - bookedSeats);
        flightRepository.save(flight);
    }

    @Override
    @Transactional
    public void releaseSeats(Long flightId, int seatsToRelease) {
        flightValidator.validateSeatsToRelease(seatsToRelease);

        Flight flight = findById(flightId);
        flight.setAvailableSeats(flight.getAvailableSeats() + seatsToRelease);
        flightRepository.save(flight);
    }

    @Override
    public List<MinPriceResponse> getMinPricesByDestinations(List<String> destinationCodes) {
        return flightRepository.findMinPricesByDestinations(destinationCodes)
                .stream()
                .map(result -> new MinPriceResponse(
                        (String) result[0],
                        (String) result[1],
                        (Double) result[2]))
                .toList();
    }

    private void validateFlightUpdate(Flight flight, Aircraft aircraft, FlightUpdate request) {
        flightValidator.validateFlightUpdate(
                flight,
                aircraft,
                request.availableSeats(),
                request.departureTime(),
                request.arrivalTime());
    }

    @Override
    public int markFlightsAsUnavailableAndReleaseSeats(LocalDateTime now) {
        Specification<Flight> spec = FlightSpecification.hasDepartedBefore(now);
        List<Flight> flightsToUpdate = flightRepository.findAll(spec);

        flightsToUpdate.forEach(flight -> {
            flight.setAvailable(false);
            flight.setAvailableSeats(0);
        });

        flightRepository.saveAll(flightsToUpdate);

        return flightsToUpdate.size();
    }

    @Override
    @Transactional
    public FlightResponse updateFlightStatus(Long id, FlightStatusUpdateRequest request) {
        Flight flight = findById(id);
        flight.setAvailable(request.available());
        Flight savedFlight = flightRepository.save(flight);
        return FlightMapper.toResponse(savedFlight);
    }

    @Override
    public void updateAvailabilityIfNeeded(Long flightId) {
        Flight flight = findById(flightId);
        LocalDateTime now = LocalDateTime.now();

        if (flight.getAvailableSeats() <= 0 || flight.getDepartureTime().isBefore(now)) {
            flight.setAvailable(false);
            flightRepository.save(flight);
        }
    }


}