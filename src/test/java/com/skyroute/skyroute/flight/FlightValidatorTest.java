package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.validation.FlightValidator;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightValidatorTest {
    @InjectMocks
    private FlightValidator flightValidator;

    private Aircraft aircraft;
    private final LocalDateTime FUTURE_DEPARTURE = LocalDateTime.now().plusDays(1).plusHours(1);
    private final LocalDateTime FUTURE_ARRIVAL = LocalDateTime.now().plusDays(1).plusHours(3);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aircraft = new Aircraft();
        aircraft.setId(1L);
        aircraft.setCapacity(200);
    }

    @Test
    void validateFlightCreation_ShouldPass_WhenAllValidationsPass() {
        assertDoesNotThrow(() -> flightValidator.validateFlightCreation(
                aircraft,
                150,
                FUTURE_DEPARTURE,
                FUTURE_ARRIVAL
        ));
    }

    @Test
    void validateCapacity_ShouldThrow_WhenAvailableSeatsExceedCapacity() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateFlightCreation(
                        aircraft,
                        300,
                        FUTURE_DEPARTURE,
                        FUTURE_ARRIVAL
                ));
        assertEquals("Available seats (300) exceed aircraft capacity (200)", ex.getMessage());
    }

    @Test
    void validateTimes_ShouldThrow_WhenDepartureAfterArrival() {
        LocalDateTime departure = LocalDateTime.now().plusDays(1).plusHours(5);
        LocalDateTime arrival = LocalDateTime.now().plusDays(1).plusHours(2);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateFlightCreation(
                        aircraft,
                        150,
                        departure,
                        arrival
                ));

        assertEquals("Departure time must be before arrival time", ex.getMessage());
    }

    @Test
    void validateTimes_ShouldThrow_WhenDepartureInPast() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateFlightCreation(
                        aircraft,
                        150,
                        LocalDateTime.now().minusDays(1),
                        FUTURE_ARRIVAL
                ));

        assertEquals("Departure time must be in the future", ex.getMessage());
    }

    @Test
    void validateAircraftCapacity_ShouldThrow_WhenAircraftIsNull() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateFlightCreation(
                        null,
                        10,
                        FUTURE_DEPARTURE,
                        FUTURE_ARRIVAL
                ));
        assertEquals("Aircraft cannot be null", ex.getMessage());
    }

    @Test
    void validateAircraftCapacity_ShouldThrow_WhenSeatsNegative() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateFlightCreation(
                        aircraft,
                        -5,
                        FUTURE_DEPARTURE,
                        FUTURE_ARRIVAL
                ));
        assertEquals("Available seats cannot be null or negative", ex.getMessage());
    }

    @Test
    void validateFlightUpdate_ShouldUseExistingFlightData_WhenArgsAreNull() {
        Flight existing = new Flight();
        existing.setAircraft(aircraft);
        existing.setAvailableSeats(50);
        existing.setDepartureTime(FUTURE_DEPARTURE);
        existing.setArrivalTime(FUTURE_ARRIVAL);

        assertDoesNotThrow(() -> flightValidator.validateFlightUpdate(
                existing,
                null,
                null,
                null,
                null
        ));
    }

    @Test
    void validateSeatsToBook_ShouldThrow_WhenSeatsRequestedExceedAvailable() {
        Flight flight = new Flight();
        flight.setAvailableSeats(10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateSeatsToBook(flight, 20));
        assertTrue(ex.getMessage().contains("Not enough seats available"));
    }

    @Test
    void validateSeatsToBook_ShouldThrow_WhenSeatsRequestedZeroOrNegative() {
        Flight flight = new Flight();
        flight.setAvailableSeats(10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateSeatsToBook(flight, 0));
        assertEquals("Seats requested must be greater than 0", ex.getMessage());
    }

    @Test
    void validateSeatsToRelease_ShouldThrow_WhenZeroOrNegative() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightValidator.validateSeatsToRelease(0));
        assertEquals("Seats to release must be positive", ex.getMessage());
    }

    @Test
    void validateSeatsToRelease_ShouldPass_WhenPositive() {
        assertDoesNotThrow(() -> flightValidator.validateSeatsToRelease(5));
    }
}