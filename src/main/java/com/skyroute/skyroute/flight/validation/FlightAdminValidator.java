package com.skyroute.skyroute.flight.validation;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FlightAdminValidator {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;

    public FlightAdminValidator(AircraftRepository aircraftRepository,
                                FlightRepository flightRepository) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
    }

    public void validateFlight(Long aircraftId, Integer availableSeats,
                               LocalDateTime departureTime, LocalDateTime arrivalTime) {

        validateAircraftExists(aircraftId);
        validateAircraftCapacity(aircraftId, availableSeats);
        validateTimes(departureTime, arrivalTime);
        validateAircraftSchedule(aircraftId, departureTime, arrivalTime);
    }

    private void validateAircraftExists(Long aircraftId) {
        if (aircraftId == null || !aircraftRepository.existsById(aircraftId)) {
            throw new EntityNotFoundException("Aircraft not found with id: " + aircraftId);
        }
    }

    private void validateAircraftCapacity(Long aircraftId, Integer availableSeats) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new EntityNotFoundException("Aircraft not found with id: " + aircraftId));

        if (availableSeats == null || availableSeats < 0) {
            throw new BusinessException("Available seats cannot be null or negative");
        }

        if (availableSeats > aircraft.getCapacity()) {
            throw new BusinessException(
                    "Available seats (" + availableSeats + ") exceed aircraft capacity (" + aircraft.getCapacity() + ")"
            );
        }
    }

    private void validateTimes(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (departureTime == null || arrivalTime == null) {
            throw new BusinessException("Departure and arrival times must be provided");
        }

        if (departureTime.isAfter(arrivalTime)) {
            throw new BusinessException("Departure time must be before arrival time");
        }

        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Departure time must be in the future");
        }
    }

    private void validateAircraftSchedule(Long aircraftId, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Pageable pageable = Pageable.unpaged();
        Page<Flight> flights = flightRepository.findAll(pageable);

        List<Flight> conflictingFlights = flights.stream()
                .filter(f -> f.getAircraft().getId().equals(aircraftId))
                .filter(f -> timesOverlap(f.getDepartureTime(), f.getArrivalTime(), departureTime, arrivalTime))
                .toList();

        if (!conflictingFlights.isEmpty()) {
            throw new BusinessException("Aircraft has conflicting flight schedules in the requested timeframe.");
        }
    }

    private boolean timesOverlap(LocalDateTime existingStart, LocalDateTime existingEnd,
                                 LocalDateTime newStart, LocalDateTime newEnd) {
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }
}

