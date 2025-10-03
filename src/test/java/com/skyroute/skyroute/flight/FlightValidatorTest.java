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
}