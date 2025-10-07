package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightUpdate;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.helper.FlightHelper;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FlightHelperTest {
    @Mock
    private AircraftService aircraftService;

    @Mock
    private RouteService routeService;

    @InjectMocks
    private FlightHelper flightHelper;

    private Aircraft testAircraft;
    private Route testRoute;
    private Airport originAirport;
    private Airport destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @BeforeEach
    void setUp(){
        originAirport = createAirport(1L, "MAD", "Madrid");
        destinationAirport = createAirport(2L, "BCN", "Barcelona");
        testRoute = createRoute(1L, originAirport, destinationAirport);
        testAircraft = createAircraft(1L, "BOEING 737", 180);
        departureTime = LocalDateTime.now().plusDays(1);
        arrivalTime = departureTime.plusHours(2);
    }

    @Nested
    class BuildFlightFromRequestTests {
        @Test
        void buildFlightFromRequest_shouldCreateFlight_whenValidRequest() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    150,
                    departureTime,
                    arrivalTime,
                    299.99,
                    1L,
                    1L,
                    true
            );

            Flight result = flightHelper.buildFlightFromRequest(request, testAircraft, testRoute);

            assertNotNull(result);
            assertEquals("SR001", result.getFlightNumber());
            assertEquals(150, result.getAvailableSeats());
            assertEquals(departureTime, result.getDepartureTime());
            assertEquals(arrivalTime, result.getArrivalTime());
            assertEquals(299.99, result.getPrice());
            assertTrue(result.isAvailable());
            assertEquals(testAircraft, result.getAircraft());
            assertEquals(testRoute, result.getRoute());
        }

        @Test
        void buildFlightFromRequest_shouldSetAvailableToTrue_whenAvailableIsNull() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    150,
                    departureTime,
                    arrivalTime,
                    299.99,
                    1L,
                    1L,
                    null
            );

            Flight result = flightHelper.buildFlightFromRequest(request, testAircraft, testRoute);

            assertNotNull(result);
            assertTrue(result.isAvailable());
        }

        @Test
        void buildFlightFromRequest_shouldCreateFlight_withAllFields() {
            FlightRequest request = new FlightRequest(
                    "SR999",
                    200,
                    departureTime,
                    arrivalTime,
                    499.99,
                    1L,
                    1L,
                    true
            );

            Flight result = flightHelper.buildFlightFromRequest(request, testAircraft, testRoute);

            assertNotNull(result);
            assertEquals("SR999", result.getFlightNumber());
            assertEquals(200, result.getAvailableSeats());
            assertEquals(499.99, result.getPrice());
            assertEquals(testAircraft, result.getAircraft());
            assertEquals(testRoute, result.getRoute());
        }
    }

    @Nested
    class ResolveAircraftForUpdateTests{
        @Test
        void resolveAircraftForUpdate_shouldReturnAircraft_whenAircraftIdProvided() {
            when(aircraftService.findById(1L)).thenReturn(testAircraft);

            Aircraft result = flightHelper.resolveAircraftForUpdate(1L);

            assertNotNull(result);
            assertEquals(testAircraft, result);
            verify(aircraftService).findById(1L);
        }

        @Test
        void resolveAircraftForUpdate_shouldReturnNull_whenAircraftIdIsNull() {
            Aircraft result = flightHelper.resolveAircraftForUpdate(null);

            assertNull(result);
            verify(aircraftService, never()).findById(any());
        }

        @Test
        void resolveAircraftForUpdate_shouldCallAircraftService_withCorrectId() {
            Long aircraftId = 99L;
            Aircraft customAircraft = createAircraft(99L, "Airbus A320", 200);
            when(aircraftService.findById(aircraftId)).thenReturn(customAircraft);

            Aircraft result = flightHelper.resolveAircraftForUpdate(aircraftId);

            assertNotNull(result);
            assertEquals(99L, result.getId());
            assertEquals("Airbus A320", result.getModel());
            verify(aircraftService).findById(aircraftId);
        }
    }

    @Nested
    class ApplyFlightUpdatesTests {
        @Test
        void applyFlightUpdates_shouldUpdateAllFields_whenAllProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            Aircraft newAircraft = createAircraft(2L, "Airbus A320", 200);
            Route newRoute = createRoute(2L, destinationAirport, originAirport);

            LocalDateTime newDeparture = LocalDateTime.now().plusDays(2);
            LocalDateTime newArrival = newDeparture.plusHours(3);

            FlightUpdate update = new FlightUpdate(
                    "SR002",
                    180,
                    newDeparture,
                    newArrival,
                    399.99,
                    false,
                    2L,
                    2L
            );

            when(routeService.findRouteById(2L)).thenReturn(newRoute);

            flightHelper.applyFlightUpdates(flight, update, newAircraft);

            assertEquals("SR002", flight.getFlightNumber());
            assertEquals(180, flight.getAvailableSeats());
            assertEquals(newDeparture, flight.getDepartureTime());
            assertEquals(newArrival, flight.getArrivalTime());
            assertEquals(399.99, flight.getPrice());
            assertFalse(flight.isAvailable());
            assertEquals(newAircraft, flight.getAircraft());
            assertEquals(newRoute, flight.getRoute());
            verify(routeService).findRouteById(2L);
        }

        @Test
        void applyFlightUpdates_shouldUpdateOnlyFlightNumber_whenOnlyFlightNumberProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            String originalFlightNumber = flight.getFlightNumber();
            int originalSeats = flight.getAvailableSeats();

            FlightUpdate update = new FlightUpdate("SR999", null,null,
                    null,null,null,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals("SR999", flight.getFlightNumber());
            assertEquals(originalSeats, flight.getAvailableSeats());
            assertEquals(testAircraft, flight.getAircraft());
            assertEquals(testRoute, flight.getRoute());
            verify(routeService, never()).findRouteById(any());
        }

        @Test
        void applyFlightUpdates_shouldUpdateOnlyAvailableSeats_whenOnlySeatsProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);

            FlightUpdate update = new FlightUpdate(null,200,null,null,
                    null,null,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals("SR001", flight.getFlightNumber());
            assertEquals(200, flight.getAvailableSeats());
        }

        @Test
        void applyFlightUpdates_shouldUpdateOnlyDepartureTim_whenOnlyDepartureProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            LocalDateTime newDeparture = LocalDateTime.now().plusDays(5);

            FlightUpdate update = new FlightUpdate(null,null, newDeparture,null,
                    null,null,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals(newDeparture, flight.getDepartureTime());
        }

        @Test
        void applyFlightUpdates_shouldUpdateOnlyArrivalTime_whenOnlyArrivalProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            LocalDateTime newArrival = LocalDateTime.now().plusDays(5).plusHours(3);

            FlightUpdate update = new FlightUpdate(
                    null,
                    null,
                    null,
                    newArrival,
                    null,
                    null,
                    null,
                    null
            );

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals(newArrival, flight.getArrivalTime());
        }

        @Test
        void applyFlightUpdates_shouldUpdateOnlyPrice_whenOnlyPriceProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);

            FlightUpdate update = new FlightUpdate(null,null,null,null,
                    599.99,null,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals(599.99, flight.getPrice());
        }

        @Test
        void applyFlightUpdates_shouldUpdateOnlyAvailable_whenOnlyAvailableProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            flight.setAvailable(true);

            FlightUpdate update = new FlightUpdate(null,null,null,null,
                    null,false,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertFalse(flight.isAvailable());
        }

        @Test
        void applyFlightUpdates_shouldUpdateAircraft_whenAircraftProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            Aircraft newAircraft = createAircraft(2L, "Airbus A320", 200);

            FlightUpdate update = new FlightUpdate(null,null,null,
                    null,null,null,2L,null);

            flightHelper.applyFlightUpdates(flight, update, newAircraft);

            assertEquals(newAircraft, flight.getAircraft());
            assertEquals(2L, flight.getAircraft().getId());
        }

        @Test
        void applyFlightUpdates_shouldNotUpdateAircraft_whenAircraftIsNull() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            Aircraft originalAircraft = flight.getAircraft();

            FlightUpdate update = new FlightUpdate(null,null,null,null,
                    null,null,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals(originalAircraft, flight.getAircraft());
        }

        @Test
        void applyFlightUpdates_shouldUpdateRoute_whenRouteIdProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            Route newRoute = createRoute(2L, destinationAirport, originAirport);

            FlightUpdate update = new FlightUpdate(null,null,null,null,
                    null,null,null,2L);

            when(routeService.findRouteById(2L)).thenReturn(newRoute);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals(newRoute, flight.getRoute());
            assertEquals(2L, flight.getRoute().getId());
            verify(routeService).findRouteById(2L);
        }

        @Test
        void applyFlightUpdates_shouldNotUpdateRoute_whenRouteIdIsNull() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            Route originalRoute = flight.getRoute();

            FlightUpdate update = new FlightUpdate(null,null,null,null,
                    null,null,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals(originalRoute, flight.getRoute());
            verify(routeService, never()).findRouteById(any());
        }

        @Test
        void applyFlightUpdates_shouldNotChangeOriginalValues_whenAllFieldsNull() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);
            flight.setAvailableSeats(150);
            flight.setPrice(299.99);
            flight.setAvailable(true);

            String originalFlightNumber = flight.getFlightNumber();
            int originalSeats = flight.getAvailableSeats();
            LocalDateTime originalDeparture = flight.getDepartureTime();
            LocalDateTime originalArrival = flight.getArrivalTime();
            double originalPrice = flight.getPrice();
            boolean originalAvailable = flight.isAvailable();

            FlightUpdate update = new FlightUpdate(null,null,null,null,
                    null,null,null,null);

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals(originalFlightNumber, flight.getFlightNumber());
            assertEquals(originalSeats, flight.getAvailableSeats());
            assertEquals(originalDeparture, flight.getDepartureTime());
            assertEquals(originalArrival, flight.getArrivalTime());
            assertEquals(originalPrice, flight.getPrice());
            assertEquals(originalAvailable, flight.isAvailable());
        }

        @Test
        void applyFlightUpdates_shouldUpdateMultipleFields_whenMultipleFieldsProvided() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);

            FlightUpdate update = new FlightUpdate(
                    "SR888",
                    175,
                    null,
                    null,
                    450.50,
                    false,
                    null,
                    null
            );

            flightHelper.applyFlightUpdates(flight, update, null);

            assertEquals("SR888", flight.getFlightNumber());
            assertEquals(175, flight.getAvailableSeats());
            assertEquals(450.50, flight.getPrice());
            assertFalse(flight.isAvailable());
        }
    }

    private Aircraft createAircraft(Long id, String model, int capacity) {
        return Aircraft.builder()
                .id(id)
                .model(model)
                .capacity(capacity)
                .build();
    }

    private Route createRoute(Long id, Airport origin, Airport destination) {
        return Route.builder()
                .id(id)
                .origin(origin)
                .destination(destination)
                .build();
    }

    private Airport createAirport(Long id, String code, String city) {
        return Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl("http://example.com/" + code.toLowerCase() + ".jpg")
                .build();
    }

    private Flight createFlight(Long id, String flightNumber, Aircraft aircraft, Route route) {
        return Flight.builder()
                .id(id)
                .flightNumber(flightNumber)
                .availableSeats(150)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .price(299.99)
                .available(true)
                .aircraft(aircraft)
                .route(route)
                .build();
    }
}