package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.dto.FlightSimpleResponse;
import com.skyroute.skyroute.flight.dto.MinPriceResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.helper.FlightHelper;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.service.FlightServiceImpl;
import com.skyroute.skyroute.flight.validation.FlightValidator;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;
    @Mock
    private AircraftService aircraftService;
    @Mock
    private RouteService routeService;
    @Mock
    private FlightValidator flightValidator;
    @Mock
    private FlightHelper flightHelper;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Flight testFlight;
    private Aircraft testAircraft;
    private Route testRoute;
    private Airport originAirport;
    private Airport destinationAirport;

    @BeforeEach
    void setUp() {
        flightService = new FlightServiceImpl(flightRepository, aircraftService, routeService, flightValidator, flightHelper);
        originAirport = createAirport(1L, "MAD", "Madrid");
        destinationAirport = createAirport(2L, "BCN", "Barcelona");
        testRoute = createRoute(1L, originAirport, destinationAirport);
        testAircraft = createAircraft(1L, "Boeing 737", 180);
        testFlight = createFlight(1L, "SR001", testAircraft, testRoute);
    }

    @Nested
    class  SearchFlightsTests {
        @Test
        void searchFlights_shouldReturnPageOfFlights_whenValidParameters(){
            Pageable pageable = PageRequest.of(0, 10);
            List<Flight> flights = List.of(testFlight);
            Page <Flight> flightPage = new PageImpl<>(flights, pageable, flights.size());

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(flightPage);

            Page<FlightSimpleResponse> result = flightService.searchFlights(
                    Optional.of("MAD"),
                    Optional.of("BCN"),
                    Optional.of("01/12/2025"),
                    Optional.of(2),
                    pageable
            );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("SR001", result.getContent().getFirst().flightNumber());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void searchFlights_shouldReturnEmptyPage_whenNoFlightsFound() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Flight> emptyPage = new PageImpl<>(List.of(),pageable, 0);

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

            Page<FlightSimpleResponse> result = flightService.searchFlights(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    pageable
            );

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void searchFlights_shouldHandleOptionalParameters(){
            Pageable pageable = PageRequest.of(0, 10);
            Page<Flight> flightPage = new PageImpl<>(List.of(testFlight), pageable, 1);

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(flightPage);

            Page<FlightSimpleResponse> result = flightService.searchFlights(
                    Optional.of("MAD"),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    pageable
            );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    class GetFlightSimpleByIdTests {
        @Test
        void getFlightSimpleById_shouldReturnFlightSimpleResponse_whenFlightExists(){
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            FlightSimpleResponse result = flightService.getFlightSimpleById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("SR001", result.flightNumber());
            assertEquals("Madrid", result.origin());
            assertEquals("Barcelona", result.destination());
            verify(flightRepository).findById(1L);
        }

        @Test
        void getFlightSimpleById_shouldThrowEntityNotFoundException_whenFlightDoesNotExist() {
            when(flightRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.getFlightSimpleById(99L)
            );

            assertEquals("Flight with id: 99 not found", exception.getMessage());
            verify(flightRepository).findById(99L);
        }
    }

    @Nested
    class SearchFlightsByBudgetAndCityTests {
        @Test
        void searchFlightsByBudgetAndCity_shouldReturnFilteredFlights(){
            Pageable pageable = PageRequest.of(0, 10);
            List<Flight> flights = List.of(testFlight);
            Page<Flight> flightPage = new PageImpl<>(flights, pageable, flights.size());

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(flightPage);

            Page<FlightSimpleResponse> result = flightService.searchFlightsByBudgetAndCity(
                    Optional.of("MAD"),
                    Optional.of("BCN"),
                    Optional.of(500.0),
                    pageable
            );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void searchFlightsByBudgetAndCity_shouldReturnOnlyAvailableFlights() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Flight> flightPage = new PageImpl<>(List.of(), pageable, 0);

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(flightPage);

            Page<FlightSimpleResponse> result = flightService.searchFlightsByBudgetAndCity(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    pageable
            );

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    class GetFlightsPageTest {
        @Test
        void getFlightsPage_shouldReturnPageOfFlightResponses() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Flight> flights = List.of(testFlight);
            Page<Flight> flightPage = new PageImpl<>(flights, pageable, flights.size());

            when(flightRepository.findAll(pageable)).thenReturn(flightPage);

            Page<FlightResponse> result = flightService.getFlightsPage(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("SR001", result.getContent().getFirst().flightNumber());
            verify(flightRepository).findAll(pageable);
        }

        @Test
        void getFlightsPage_shouldReturnEmptyPage_whenNoFlights() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Flight> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(flightRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<FlightResponse> result = flightService.getFlightsPage(pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(flightRepository).findAll(pageable);
        }
    }

    @Test
    void getMinPricesByDestinations_shouldReturnMinPrices_whenValidDestinations() {
        List<String> destinationCodes = List.of("BCN", "MAD");
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[] { "BCN", "Barcelona", 299.0 });
        mockResults.add(new Object[] { "MAD", "Madrid", 249.0 });

        when(flightRepository.findMinPricesByDestinations(anyList())).thenReturn(mockResults);

        List<MinPriceResponse> result = flightService.getMinPricesByDestinations(destinationCodes);

        assertNotNull(result);
        assertEquals(2, result.size());

        MinPriceResponse bcnResponse = result.get(0);
        assertEquals("BCN", bcnResponse.destinationCode());
        assertEquals("Barcelona", bcnResponse.destinationCity());
        assertEquals(299.0, bcnResponse.minPrice());

        MinPriceResponse madResponse = result.get(1);
        assertEquals("MAD", madResponse.destinationCode());
        assertEquals("Madrid", madResponse.destinationCity());
        assertEquals(249.0, madResponse.minPrice());
    }

    @Test
    void getMinPricesByDestinations_shouldReturnEmptyList_whenNoResults() {
        List<String> destinationCodes = List.of("NONEXISTENT");
        when(flightRepository.findMinPricesByDestinations(anyList())).thenReturn(List.of());

        List<MinPriceResponse> result = flightService.getMinPricesByDestinations(destinationCodes);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMinPricesByDestinations_shouldHandleSingleDestination() {
        List<String> destinationCodes = List.of("BCN");
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[] { "BCN", "Barcelona", 299.0 });

        when(flightRepository.findMinPricesByDestinations(anyList())).thenReturn(mockResults);

        List<MinPriceResponse> result = flightService.getMinPricesByDestinations(destinationCodes);

        assertNotNull(result);
        assertEquals(1, result.size());

        MinPriceResponse response = result.get(0);
        assertEquals("BCN", response.destinationCode());
        assertEquals("Barcelona", response.destinationCity());
        assertEquals(299.0, response.minPrice());
    }

    private Airport createAirport(Long id, String code, String city){
        return Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl("http://example.com/" + code.toLowerCase() + ".jpg")
                .build();
    }

    private Route createRoute(Long id, Airport origin, Airport destination){
        return Route.builder()
                .id(id)
                .origin(origin)
                .destination(destination)
                .build();
    }

    private Aircraft createAircraft(Long id, String model, int capacity){
        return Aircraft.builder()
                .id(id)
                .model(model)
                .capacity(capacity)
                .build();
    }

    private Flight createFlight(Long id, String flightNumber, Aircraft aircraft, Route route){
        return Flight.builder()
                .id(id)
                .flightNumber(flightNumber)
                .aircraft(aircraft)
                .route(route)
                .build();
    }
}