package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.flight.dto.*;
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
        flightService = new FlightServiceImpl(flightRepository, aircraftService, routeService, flightValidator,
                flightHelper);
        originAirport = createAirport(1L, "MAD", "Madrid");
        destinationAirport = createAirport(2L, "BCN", "Barcelona");
        testRoute = createRoute(1L, originAirport, destinationAirport);
        testAircraft = createAircraft(1L, "Boeing 737", 180);
        testFlight = createFlight(1L, "SR001", testAircraft, testRoute, testAircraft.getCapacity(), true);
    }

    @Nested
    class SearchFlightsTests {
        @Test
        void searchFlights_shouldReturnPageOfFlights_whenValidParameters() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Flight> flights = List.of(testFlight);
            Page<Flight> flightPage = new PageImpl<>(flights, pageable, flights.size());

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(flightPage);

            Page<FlightSimpleResponse> result = flightService.searchFlights(
                    Optional.of("MAD"),
                    Optional.of("BCN"),
                    Optional.of("01/12/2025"),
                    Optional.of(2),
                    pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("SR001", result.getContent().getFirst().flightNumber());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void searchFlights_shouldReturnEmptyPage_whenNoFlightsFound() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Flight> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

            Page<FlightSimpleResponse> result = flightService.searchFlights(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void searchFlights_shouldHandleOptionalParameters() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Flight> flightPage = new PageImpl<>(List.of(testFlight), pageable, 1);

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(flightPage);

            Page<FlightSimpleResponse> result = flightService.searchFlights(
                    Optional.of("MAD"),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(flightRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    class GetFlightSimpleByIdTests {
        @Test
        void getFlightSimpleById_shouldReturnFlightSimpleResponse_whenFlightExists() {
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
                    () -> flightService.getFlightSimpleById(99L));

            assertEquals("Flight with id: 99 not found", exception.getMessage());
            verify(flightRepository).findById(99L);
        }
    }

    @Nested
    class SearchFlightsByBudgetAndCityTests {
        @Test
        void searchFlightsByBudgetAndCity_shouldReturnFilteredFlights() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Flight> flights = List.of(testFlight);
            Page<Flight> flightPage = new PageImpl<>(flights, pageable, flights.size());

            when(flightRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(flightPage);

            Page<FlightSimpleResponse> result = flightService.searchFlightsByBudgetAndCity(
                    Optional.of("MAD"),
                    Optional.of("BCN"),
                    Optional.of(500.0),
                    pageable);

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
                    pageable);

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

    @Nested
    class  CreateFlightTests{
        @Test
        void createFlight_shouldReturnFlightResponse_whenValidRequest() {
            FlightRequest request = createFlightRequest();

            Flight expectedFlight = createFlight(
                    1L,
                    request.flightNumber(),
                    testAircraft,
                    testRoute,
                    request.availableSeats(),
                    request.available());

            when(aircraftService.findById(1L)).thenReturn(testAircraft);
            when(routeService.findRouteById(1L)).thenReturn(testRoute);
            doNothing().when(flightValidator).validateFlightCreation(
                    any(Aircraft.class), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)
            );
            when(flightHelper.buildFlightFromRequest(request, testAircraft, testRoute))
                    .thenReturn(expectedFlight);
            when(flightRepository.save(expectedFlight)).thenReturn(expectedFlight);

            FlightResponse result = flightService.createFlight(request);

            assertNotNull(result);
            assertEquals("SR001", result.flightNumber());
            assertEquals(150, result.availableSeats());
            assertTrue(result.available());
            verify(aircraftService).findById(1L);
            verify(routeService).findRouteById(1L);
            verify(flightValidator).validateFlightCreation(
                    testAircraft, 150, request.departureTime(), request.arrivalTime());
            verify(flightRepository).save(expectedFlight);
        }

        @Test
        void createFlight_shouldThrowEntityNotFoundException_whenAircraftNotFound() {
            FlightRequest request = createFlightRequest();

            when(aircraftService.findById(1L))
                    .thenThrow(new EntityNotFoundException("Aircraft not found with ID: 1"));
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.createFlight(request)
            );

            assertEquals("Aircraft not found with ID: 1", exception.getMessage());
            verify(aircraftService).findById(1L);
            verify(routeService, never()).findRouteById(anyLong());
            verify(flightRepository, never()).save(any());
        }

        @Test
        void createFlight_shouldThrowEntityNotFoundException_whenRouteNotFound() {
            FlightRequest request = createFlightRequest();

            when(aircraftService.findById(1L)).thenReturn(testAircraft);
            when(routeService.findRouteById(1L))
                    .thenThrow(new EntityNotFoundException("Route not found with ID: 1"));

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.createFlight(request)
            );

            assertEquals("Route not found with ID: 1", exception.getMessage());
            verify(aircraftService).findById(1L);
            verify(routeService).findRouteById(1L);
            verify(flightRepository, never()).save(any());
        }

        @Test
        void createFlight_shouldThrowBusinessException_whenValidationFails() {
            FlightRequest request = createFlightRequest();

            when(aircraftService.findById(1L)).thenReturn(testAircraft);
            when(routeService.findRouteById(1L)).thenReturn(testRoute);
            doThrow(new BusinessException("Available seats exceed aircraft capacity"))
                    .when(flightValidator).validateFlightCreation(
                            any(Aircraft.class), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)
                    );

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> flightService.createFlight(request)
            );

            assertEquals("Available seats exceed aircraft capacity", exception.getMessage());
            verify(flightRepository, never()).save(any());
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

    @Nested
    class UpdateFlightTests{
        @Test
        void updateFlight_shouldReturnUpdatedFlight_whenValidRequest(){
            FlightUpdate update = createFlightUpdate();

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            when(flightHelper.resolveAircraftForUpdate(1L)).thenReturn(testAircraft);
            doNothing().when(flightValidator).validateFlightUpdate(
                    any(Flight.class), any(Aircraft.class), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)
            );
            doNothing().when(flightHelper).applyFlightUpdates(testFlight, update, testAircraft);
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            FlightResponse result = flightService.updateFlight(1L, update);

            assertNotNull(result);
            assertEquals("SR001", result.flightNumber());
            verify(flightRepository).findById(1L);
            verify(flightHelper).resolveAircraftForUpdate(1L);
            verify(flightValidator).validateFlightUpdate(
                    eq(testFlight), eq(testAircraft), eq(150), any(LocalDateTime.class), any(LocalDateTime.class)
            );
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateFlight_shouldHandlePartialUpdate() {
            FlightUpdate partialUpdate = new FlightUpdate(
                    null, 100, null, null, null, null, null,
                    null
            );

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            when(flightHelper.resolveAircraftForUpdate(null)).thenReturn(null);
            doNothing().when(flightValidator).validateFlightUpdate(
                    any(Flight.class), any(), anyInt(), any(), any()
            );
            doNothing().when(flightHelper).applyFlightUpdates(testFlight, partialUpdate, null);
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            FlightResponse result = flightService.updateFlight(1L, partialUpdate);

            assertNotNull(result);
            verify(flightRepository).findById(1L);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateFlight_shouldThrowEntityNotFoundException_whenFlightNotFound() {
            FlightUpdate update = createFlightUpdate();

            when(flightRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.updateFlight(99L, update)
            );

            assertEquals("Flight with id: 99 not found", exception.getMessage());
            verify(flightRepository).findById(99L);
            verify(flightRepository, never()).save(any());
        }

        @Test
        void updateFlight_shouldThrowBusinessException_whenValidationFails() {
            FlightUpdate update = createFlightUpdate();

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            when(flightHelper.resolveAircraftForUpdate(1L)).thenReturn(testAircraft);
            doThrow(new BusinessException("Departure time must be in the future"))
                    .when(flightValidator).validateFlightUpdate(
                            any(Flight.class), any(Aircraft.class), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)
                    );

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> flightService.updateFlight(1L, update)
            );

            assertEquals("Departure time must be in the future", exception.getMessage());
            verify(flightRepository, never()).save(any());
        }
    }

    @Nested
    class DeleteFlightTests {
        @Test
        void deleteFlight_shouldDeleteSuccessfully_whenFlightExists() {
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            doNothing().when(flightRepository).delete(testFlight);

            assertDoesNotThrow(() -> flightService.deleteFlight(1L));

            verify(flightRepository).findById(1L);
            verify(flightRepository).delete(testFlight);
        }

        @Test
        void deleteFlight_shouldThrowEntityNotFoundException_whenFlightDoesNotExist() {
            when(flightRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.deleteFlight(99L)
            );

            assertEquals("Flight with id: 99 not found", exception.getMessage());
            verify(flightRepository).findById(99L);
            verify(flightRepository, never()).delete(any(Flight.class));
        }
    }

    @Nested
    class GetFlightByIdTests {
        @Test
        void getFlightById_shouldReturnFlightResponse_whenFlightExists() {
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            FlightResponse result = flightService.getFlightById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("SR001", result.flightNumber());
            verify(flightRepository).findById(1L);
        }

        @Test
        void getFlightById_shouldThrowEntityNotFoundException_whenFlightDoesNotExist() {
            when(flightRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.getFlightById(99L)
            );

            assertEquals("Flight with id: 99 not found", exception.getMessage());
            verify(flightRepository).findById(99L);
        }
    }

    @Nested
    class IsFlightAvailableTests {
        @Test
        void isFlightAvailable_shouldReturnTrue_whenFlightIsAvailable() {
            testFlight.setAvailable(true);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            boolean result = flightService.isFlightAvailable(1L);

            assertTrue(result);
            verify(flightRepository).findById(1L);
        }

        @Test
        void isFlightAvailable_shouldReturnFalse_whenFlightIsNotAvailable() {
            testFlight.setAvailable(false);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            boolean result = flightService.isFlightAvailable(1L);

            assertFalse(result);
            verify(flightRepository).findById(1L);
        }

        @Test
        void isFlightAvailable_shouldThrowException_whenFlightDoesNotExist() {
            when(flightRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> flightService.isFlightAvailable(99L));

            verify(flightRepository).findById(99L);
        }
    }

    @Nested
    class HasAvailableSeatsTests {
        @Test
        void hasAvailableSeats_shouldReturnTrue_whenEnoughSeatsAvailable() {
            testFlight.setAvailableSeats(150);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            boolean result = flightService.hasAvailableSeats(1L, 50);

            assertTrue(result);
            verify(flightRepository).findById(1L);
        }

        @Test
        void hasAvailableSeats_shouldReturnFalse_whenNotEnoughSeatsAvailable() {
            testFlight.setAvailableSeats(10);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            boolean result = flightService.hasAvailableSeats(1L, 50);

            assertFalse(result);
            verify(flightRepository).findById(1L);
        }

        @Test
        void hasAvailableSeats_shouldReturnTrue_whenExactSeatsAvailable() {
            testFlight.setAvailableSeats(50);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            boolean result = flightService.hasAvailableSeats(1L, 50);

            assertTrue(result);
            verify(flightRepository).findById(1L);
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_shouldReturnFlight_whenFlightExists() {
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            Flight result = flightService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("SR001", result.getFlightNumber());
            verify(flightRepository).findById(1L);
        }

        @Test
        void findById_shouldThrowEntityNotFoundException_whenFlightDoesNotExist() {
            when(flightRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.findById(99L)
            );

            assertEquals("Flight with id: 99 not found", exception.getMessage());
            verify(flightRepository).findById(99L);
        }
    }

    @Nested
    class BookSeatsTests {
        @Test
        void bookSeats_shouldDecreaseAvailableSeats_whenValidRequest() {
            testFlight.setAvailableSeats(150);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            doNothing().when(flightValidator).validateSeatsToBook(testFlight, 10);
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            flightService.bookSeats(1L, 10);

            assertEquals(140, testFlight.getAvailableSeats());
            verify(flightRepository).findById(1L);
            verify(flightValidator).validateSeatsToBook(testFlight, 10);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void bookSeats_shouldThrowBusinessException_whenNotEnoughSeats() {
            testFlight.setAvailableSeats(5);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            doThrow(new BusinessException("Not enough seats available. requested: 10. Available: 5"))
                    .when(flightValidator).validateSeatsToBook(testFlight, 10);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> flightService.bookSeats(1L, 10)
            );

            assertTrue(exception.getMessage().contains("Not enough seats available"));
            verify(flightRepository).findById(1L);
            verify(flightValidator).validateSeatsToBook(testFlight, 10);
            verify(flightRepository, never()).save(any());
        }

        @Test
        void bookSeats_shouldThrowBusinessException_whenZeroSeatsRequested() {
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            doThrow(new BusinessException("Seats requested must be greater than 0"))
                    .when(flightValidator).validateSeatsToBook(testFlight, 0);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> flightService.bookSeats(1L, 0)
            );

            assertEquals("Seats requested must be greater than 0", exception.getMessage());
            verify(flightRepository, never()).save(any());
        }
    }

    @Nested
    class ReleaseSeatsTests {
        @Test
        void releaseSeats_shouldIncreaseAvailableSeats_whenValidRequest() {
            testFlight.setAvailableSeats(140);
            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            doNothing().when(flightValidator).validateSeatsToRelease(10);
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            flightService.releaseSeats(1L, 10);

            assertEquals(150, testFlight.getAvailableSeats());
            verify(flightRepository).findById(1L);
            verify(flightValidator).validateSeatsToRelease(10);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void releaseSeats_shouldThrowBusinessException_whenNegativeSeats() {
            doThrow(new BusinessException("Seats to release must be positive"))
                    .when(flightValidator).validateSeatsToRelease(-5);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> flightService.releaseSeats(1L, -5)
            );

            assertEquals("Seats to release must be positive", exception.getMessage());
            verify(flightRepository, never()).findById(anyLong());
            verify(flightRepository, never()).save(any());
        }

        @Test
        void releaseSeats_shouldThrowBusinessException_whenZeroSeats() {
            doThrow(new BusinessException("Seats to release must be positive"))
                    .when(flightValidator).validateSeatsToRelease(0);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> flightService.releaseSeats(1L, 0)
            );

            assertEquals("Seats to release must be positive", exception.getMessage());
            verify(flightRepository, never()).save(any());
        }
    }

    @Nested
    class MarkFlightsAsUnavailableAndReleaseSeatsTests {
        @Test
        void markFlightsAsUnavailableAndReleaseSeats_shouldUpdateDepartedFlights() {
            LocalDateTime now = LocalDateTime.now();
            List<Flight> departedFlights = List.of(
                    createFlight(1L, "SR001", testAircraft, testRoute),
                    createFlight(2L, "SR002", testAircraft, testRoute)
            );

            when(flightRepository.findAll(any(Specification.class))).thenReturn(departedFlights);
            when(flightRepository.saveAll(departedFlights)).thenReturn(departedFlights);

            int result = flightService.markFlightsAsUnavailableAndReleaseSeats(now);

            assertEquals(2, result);
            departedFlights.forEach(flight -> {
                assertFalse(flight.isAvailable());
                assertEquals(0, flight.getAvailableSeats());
            });
            verify(flightRepository).findAll(any(Specification.class));
            verify(flightRepository).saveAll(departedFlights);
        }

        @Test
        void markFlightsAsUnavailableAndReleaseSeats_shouldReturnZero_whenNoFlightsDeparted() {
            LocalDateTime now = LocalDateTime.now();

            when(flightRepository.findAll(any(Specification.class))).thenReturn(List.of());

            int result = flightService.markFlightsAsUnavailableAndReleaseSeats(now);

            assertEquals(0, result);
            verify(flightRepository).findAll(any(Specification.class));
            verify(flightRepository).saveAll(List.of());
        }

        @Test
        void markFlightsAsUnavailableAndReleaseSeats_shouldHandleSingleFlight() {
            LocalDateTime now = LocalDateTime.now();
            Flight departedFlight = createFlight(1L, "SR001", testAircraft, testRoute);
            departedFlight.setAvailableSeats(100);
            departedFlight.setAvailable(true);

            when(flightRepository.findAll(any(Specification.class))).thenReturn(List.of(departedFlight));
            when(flightRepository.saveAll(List.of(departedFlight))).thenReturn(List.of(departedFlight));

            int result = flightService.markFlightsAsUnavailableAndReleaseSeats(now);

            assertEquals(1, result);
            assertFalse(departedFlight.isAvailable());
            assertEquals(0, departedFlight.getAvailableSeats());
            verify(flightRepository).findAll(any(Specification.class));
            verify(flightRepository).saveAll(List.of(departedFlight));
        }
    }

    @Nested
    class UpdateFlightStatusTests {
        @Test
        void updateFlightStatus_shouldUpdateStatusToAvailable_whenValidRequest() {
            Long flightId = 1L;
            FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(true);
            testFlight.setAvailable(false);

            when(flightRepository.findById(flightId)).thenReturn(Optional.of(testFlight));
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            FlightResponse result = flightService.updateFlightStatus(flightId, request);

            assertNotNull(result);
            assertTrue(result.available());
            verify(flightRepository).findById(flightId);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateFlightStatus_shouldUpdateStatusToUnavailable_whenValidRequest() {
            Long flightId = 1L;
            FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(false);
            testFlight.setAvailable(true);

            when(flightRepository.findById(flightId)).thenReturn(Optional.of(testFlight));
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            FlightResponse result = flightService.updateFlightStatus(flightId, request);

            assertNotNull(result);
            assertFalse(result.available());
            verify(flightRepository).findById(flightId);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateFlightStatus_shouldThrowException_whenFlightNotFound() {
            Long flightId = 999L;
            FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(true);

            when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.updateFlightStatus(flightId, request));

            assertEquals("Flight with id: 999 not found", exception.getMessage());
            verify(flightRepository).findById(flightId);
            verify(flightRepository, never()).save(any(Flight.class));
        }
    }

    @Nested
    class UpdateAvailabilityIfNeededTests {
        @Test
        void updateAvailabilityIfNeeded_shouldSetFlightToUnavailable_whenNoSeatsAvailable() {
            testFlight.setAvailableSeats(0);
            testFlight.setAvailable(true);
            testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            flightService.updateAvailabilityIfNeeded(1L);

            assertFalse(testFlight.isAvailable());
            verify(flightRepository).findById(1L);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateAvailabilityIfNeeded_shouldSetFlightToUnavailable_whenDepartureTimeHasPassed() {
            testFlight.setAvailableSeats(100);
            testFlight.setAvailable(true);
            testFlight.setDepartureTime(LocalDateTime.now().minusHours(1));

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            flightService.updateAvailabilityIfNeeded(1L);

            assertFalse(testFlight.isAvailable());
            verify(flightRepository).findById(1L);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateAvailabilityIfNeeded_shouldSetFlightToUnavailable_whenBothConditionsMet() {
            testFlight.setAvailableSeats(0);
            testFlight.setAvailable(true);
            testFlight.setDepartureTime(LocalDateTime.now().minusHours(1));

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            flightService.updateAvailabilityIfNeeded(1L);

            assertFalse(testFlight.isAvailable());
            verify(flightRepository).findById(1L);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateAvailabilityIfNeeded_shouldNotChangeAvailability_whenSeatsAvailableAndFutureDeparture() {
            testFlight.setAvailableSeats(100);
            testFlight.setAvailable(true);
            testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            flightService.updateAvailabilityIfNeeded(1L);

            assertTrue(testFlight.isAvailable());
            verify(flightRepository).findById(1L);
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        void updateAvailabilityIfNeeded_shouldNotChangeAvailability_whenAlreadyUnavailable() {
            testFlight.setAvailableSeats(100);
            testFlight.setAvailable(false);
            testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

            flightService.updateAvailabilityIfNeeded(1L);

            assertFalse(testFlight.isAvailable());
            verify(flightRepository).findById(1L);
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        void updateAvailabilityIfNeeded_shouldSetFlightToUnavailable_whenNegativeSeats() {
            testFlight.setAvailableSeats(-1);
            testFlight.setAvailable(true);
            testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));

            when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
            when(flightRepository.save(testFlight)).thenReturn(testFlight);

            flightService.updateAvailabilityIfNeeded(1L);

            assertFalse(testFlight.isAvailable());
            verify(flightRepository).findById(1L);
            verify(flightRepository).save(testFlight);
        }

        @Test
        void updateAvailabilityIfNeeded_shouldThrowException_whenFlightNotFound() {
            when(flightRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> flightService.updateAvailabilityIfNeeded(99L)
            );

            assertEquals("Flight with id: 99 not found", exception.getMessage());
            verify(flightRepository).findById(99L);
            verify(flightRepository, never()).save(any(Flight.class));
        }
    }

    private Airport createAirport(Long id, String code, String city) {
        return Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl("http://example.com/" + code.toLowerCase() + ".jpg")
                .build();
    }

    private Route createRoute(Long id, Airport origin, Airport destination) {
        return Route.builder()
                .id(id)
                .origin(origin)
                .destination(destination)
                .build();
    }

    private Aircraft createAircraft(Long id, String model, int capacity) {
        return Aircraft.builder()
                .id(id)
                .model(model)
                .capacity(capacity)
                .build();
    }

    private Flight createFlight(Long id, String flightNumber, Aircraft aircraft, Route route, int availableSeats, boolean available){
        return Flight.builder()
                .id(id)
                .flightNumber(flightNumber)
                .aircraft(aircraft)
                .route(route)
                .availableSeats(availableSeats)
                .available(available)
                .build();
    }

    private FlightRequest createFlightRequest() {
        return new FlightRequest(
                "SR001",
                150,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                299.99,
                1L,
                1L,
                true
        );
    }

    private FlightUpdate createFlightUpdate() {
        return new FlightUpdate(
                "SR001",
                150,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                299.99,
                true,
                1L,
                1L
        );
    }

    private Flight createFlight(Long id, String flightNumber, Aircraft aircraft, Route route){
        return createFlight(id, flightNumber, aircraft, route, 100, true);
    }
}