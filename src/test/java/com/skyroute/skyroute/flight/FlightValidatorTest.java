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

    @Mock
    private AircraftRepository aircraftRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightValidator flightAdminValidator;

    private Aircraft aircraft;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        aircraft = new Aircraft();
        aircraft.setId(1L);
        aircraft.setCapacity(200);
    }

    @Test
    void validateFlight_Creation_ShouldPass_WhenAllValidationsPass() {
        when(aircraftRepository.existsById(1L)).thenReturn(true);
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        when(flightRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of()));

        assertDoesNotThrow(() -> flightAdminValidator.validateFlightCreation(
                1L,
                150,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2)
        ));
    }

    @Test
    void validateFlight_Creation_ShouldThrow_WhenAircraftDoesNotExist() {
        when(aircraftRepository.existsById(99L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> flightAdminValidator.validateFlightCreation(
                        99L,
                        100,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1).plusHours(2)
                ));

        assertEquals("Aircraft not found with id: 99", ex.getMessage());
    }

    @Test
    void validateFlight_Creation_ShouldThrow_WhenAvailableSeatsExceedCapacity() {
        when(aircraftRepository.existsById(1L)).thenReturn(true);
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        when(flightRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of()));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightAdminValidator.validateFlightCreation(
                        1L,
                        300,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1).plusHours(2)
                ));

        assertEquals("Available seats (300) exceed aircraft capacity (200)", ex.getMessage());
    }

    @Test
    void validateFlight_Creation_ShouldThrow_WhenDepartureAfterArrival() {
        when(aircraftRepository.existsById(1L)).thenReturn(true);
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        when(flightRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of()));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightAdminValidator.validateFlightCreation(
                        1L,
                        150,
                        LocalDateTime.now().plusDays(2),
                        LocalDateTime.now().plusDays(1)
                ));

        assertEquals("Departure time must be before arrival time", ex.getMessage());
    }

    @Test
    void validateFlight_Creation_ShouldThrow_WhenDepartureInPast() {
        when(aircraftRepository.existsById(1L)).thenReturn(true);
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        when(flightRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of()));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightAdminValidator.validateFlightCreation(
                        1L,
                        150,
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1)
                ));

        assertEquals("Departure time must be in the future", ex.getMessage());
    }

    @Test
    void validateFlight_Creation_ShouldThrow_WhenScheduleConflicts() {
        when(aircraftRepository.existsById(1L)).thenReturn(true);
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));

        Flight existingFlight = new Flight();
        existingFlight.setAircraft(aircraft);
        existingFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        existingFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));

        when(flightRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(existingFlight)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> flightAdminValidator.validateFlightCreation(
                        1L,
                        150,
                        LocalDateTime.now().plusDays(1).plusMinutes(30),
                        LocalDateTime.now().plusDays(1).plusHours(3)
                ));

        assertEquals("Aircraft has conflicting flight schedules in the requested timeframe.", ex.getMessage());
    }
}