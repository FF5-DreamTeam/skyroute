package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.service.publicapi.FlightPublicServiceImpl;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.service.admin.FlightService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlightPublicServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightPublicServiceImpl service;

    private Flight flight;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Route route = new Route();
        route.setId(1L);

        flight = new Flight();
        flight.setId(1L);
        flight.setFlightNumber("SR123");
        flight.setAvailableSeats(10);
        flight.setDepartureTime(LocalDateTime.now().plusHours(5));
        flight.setArrivalTime(LocalDateTime.now().plusHours(7));
        flight.setPrice(200.0);
        flight.setAvailable(true);
        flight.setRoute(route);
    }

    @Test
    void testGetFlightById_found() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        FlightSimpleResponse response = service.getFlightById(1L);

        assertNotNull(response);
        assertEquals("SR123", response.flightNumber());
    }

    @Test
    void testGetFlightById_notFound() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getFlightById(1L));
    }

    @Test
    void testReserveFirstAlternative_within2HoursDifference() {
        // Original flight
        Flight original = new Flight();
        original.setId(1L);
        original.setFlightNumber("SR100");
        original.setPrice(100.0);
        original.setDepartureTime(LocalDateTime.now().plusHours(1));
        original.setRoute(flight.getRoute());

        // Alternative flight
        Flight alternative = new Flight();
        alternative.setId(2L);
        alternative.setFlightNumber("SR200");
        alternative.setAvailableSeats(20);
        alternative.setPrice(150.0);
        alternative.setDepartureTime(LocalDateTime.now().plusHours(2));
        alternative.setArrivalTime(LocalDateTime.now().plusHours(4));
        alternative.setRoute(flight.getRoute());

        // Mocks
        when(flightRepository.findById(1L)).thenReturn(Optional.of(original));
        when(flightRepository.findSimilarFlights(anyLong(), any(), any(), eq(1L)))
                .thenReturn(List.of(alternative));
        when(flightService.findEntityById(2L)).thenReturn(alternative);

        // Act
        FlightSimpleResponse response = service.reserveFirstAlternative(1L, 2);

        // Assert
        assertNotNull(response);
        assertEquals(2L, response.id());
        assertEquals("SR200", response.flightNumber());
        // Price should be difference (150 - 100 = 50)
        assertEquals(50.0, response.price());

        // Verify seats updated
        ArgumentCaptor<FlightRequest> captor = ArgumentCaptor.forClass(FlightRequest.class);
        verify(flightService).updateFlight(eq(2L), captor.capture());
        assertEquals(18, captor.getValue().availableSeats());
    }

    @Test
    void testReserveFirstAlternative_after2HoursDifference() {
        // Arrange
        Route route = new Route();
        route.setId(1L);

        Flight original = new Flight();
        original.setId(1L);
        original.setPrice(100.0);
        original.setDepartureTime(LocalDateTime.now().minusHours(3));
        original.setRoute(route);

        Flight alternative = new Flight();
        alternative.setId(2L);
        alternative.setFlightNumber("SR200"); // <-- faltaba el número de vuelo
        alternative.setPrice(200.0);
        alternative.setAvailableSeats(5);
        alternative.setDepartureTime(LocalDateTime.now().minusHours(1));
        alternative.setArrivalTime(LocalDateTime.now().plusHours(1));
        alternative.setRoute(route);

        // Mocks
        when(flightRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(flightRepository.findSimilarFlights(eq(route.getId()), any(), any(), eq(original.getId())))
                .thenReturn(List.of(alternative));
        when(flightService.findEntityById(alternative.getId())).thenReturn(alternative);

        // 👉 aquí devolvemos un FlightResponse en lugar de un Flight
        when(flightService.updateFlight(eq(alternative.getId()), any()))
                .thenReturn(new FlightResponse(
                        alternative.getId(),
                        alternative.getFlightNumber(),
                        alternative.getAvailableSeats(),
                        alternative.getDepartureTime(),
                        alternative.getArrivalTime(),
                        alternative.getPrice(),
                        alternative.isAvailable(),
                        null,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));

        // Act
        FlightSimpleResponse response = service.reserveFirstAlternative(original.getId(), 1);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(2L, response.id());
        assertEquals("SR200", response.flightNumber());
        assertEquals(200.0, response.price(),
                "After 2 hours, the full price of the alternative should be charged");
    }

    @Test
    void testReserveFirstAlternative_noAlternatives() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.findSimilarFlights(anyLong(), any(), any(), eq(1L)))
                .thenReturn(List.of());

        assertThrows(EntityNotFoundException.class,
                () -> service.reserveFirstAlternative(1L, 1));
    }
}


