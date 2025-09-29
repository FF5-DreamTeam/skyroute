package com.skyroute.skyroute.flight.validation;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FlightValidator {

    public void validateAircraftCapacity(Aircraft aircraft, Integer availableSeats){
        if (aircraft == null){
            throw new BusinessException("Aircraft cannot be null");
        }
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

        if (departureTime.isEqual(arrivalTime) || departureTime.isAfter(arrivalTime)) {
            throw new BusinessException("Departure time must be before arrival time");
        }

        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Departure time must be in the future");
        }
    }

    public void validateFlightCreation(Aircraft aircraft, Integer availableSeats,
                                       LocalDateTime departureTime, LocalDateTime arrivalTime) {

        validateAircraftCapacity(aircraft, availableSeats);
        validateTimes(departureTime, arrivalTime);
    }

    public void validateFlightUpdate(Flight existingFlight, Aircraft aircraft,
                                     Integer availableSeats, LocalDateTime departureTime, LocalDateTime arrivalTime){
        LocalDateTime departure = departureTime != null ? departureTime : existingFlight.getDepartureTime();
        LocalDateTime arrival = arrivalTime != null ? arrivalTime : existingFlight.getArrivalTime();
        Integer seats = availableSeats != null ? availableSeats : existingFlight.getAvailableSeats();
        Aircraft aircraftToValidate = aircraft != null ? aircraft : existingFlight.getAircraft();

        validateTimes(departure, arrival);
        validateAircraftCapacity(aircraftToValidate, seats);
    }

    public void validateSeatsToBook(Flight flight, int seatsRequested){
        if (seatsRequested <= 0){
            throw new BusinessException("Seats requested must be greater than 0");
        }

        if (seatsRequested > flight.getAvailableSeats()){
            throw new BusinessException("Not enough seats available. requested: " + seatsRequested
            + ". Available: " + flight.getAvailableSeats());
        }
    }

    public void validateSeatsToRelease(int seatsToRelease){
        if (seatsToRelease <= 0){
            throw new BusinessException("Seats to release must be positive");
        }
    }
}