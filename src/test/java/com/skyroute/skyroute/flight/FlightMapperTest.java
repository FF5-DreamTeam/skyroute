package com.skyroute.skyroute.flight;

import org.springframework.test.context.ActiveProfiles;
import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.flight.dto.FlightMapper;
import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.dto.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.entity.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class FlightMapperTest {
    private FlightMapper flightMapper;
    private Aircraft testAircraft;
    private Route testRoute;
    private Airport originAirport;
    private Airport destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @BeforeEach
    void setUp() {
        flightMapper = new FlightMapper();
        originAirport = createAirport(1L, "MAD", "Madrid");
        destinationAirport = createAirport(2L, "BCN", "Barcelona");
        testRoute = createRoute(1L, originAirport, destinationAirport);
        testAircraft = createAircraft(1L, "Boeing 737", 180);
        departureTime = LocalDateTime.now().plusDays(1);
        arrivalTime = departureTime.plusHours(2);
    }

    @Nested
    class ToEntityTests {
        @Test
        void toEntity_shouldMapAllFields_whenValidRequest() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    150,
                    departureTime,
                    arrivalTime,
                    299.99,
                    1L,
                    1L,
                    true);

            Flight result = flightMapper.toEntity(request);

            assertNotNull(result);
            assertEquals("SR001", result.getFlightNumber());
            assertEquals(150, result.getAvailableSeats());
            assertEquals(departureTime, result.getDepartureTime());
            assertEquals(arrivalTime, result.getArrivalTime());
            assertEquals(299.99, result.getPrice());
            assertTrue(result.isAvailable());
        }

        @Test
        void toEntity_shouldSetAvailableToTrue_whenAvailableIsNull() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    150,
                    departureTime,
                    arrivalTime,
                    299.99,
                    1L,
                    1L,
                    null);

            Flight result = flightMapper.toEntity(request);

            assertNotNull(result);
            assertTrue(result.isAvailable());
        }

        @Test
        void toEntity_shouldSetAvailableToFalse_whenExplicitlySetToFalse() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    150,
                    departureTime,
                    arrivalTime,
                    299.99,
                    1L,
                    1L,
                    false);

            Flight result = flightMapper.toEntity(request);

            assertNotNull(result);
            assertFalse(result.isAvailable());
        }

        @Test
        void toEntity_shouldReturnNull_whenRequestIsNull() {
            Flight result = flightMapper.toEntity(null);

            assertNull(result);
        }

        @Test
        void toEntity_shouldNotSetAircraftAndRoute() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    150,
                    departureTime,
                    arrivalTime,
                    299.99,
                    1L,
                    1L,
                    true);

            Flight result = flightMapper.toEntity(request);

            assertNotNull(result);
            assertNull(result.getAircraft());
            assertNull(result.getRoute());
        }

        @Test
        void toEntity_shouldMapWithZeroSeats() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    0,
                    departureTime,
                    arrivalTime,
                    299.99,
                    1L,
                    1L,
                    true);

            Flight result = flightMapper.toEntity(request);

            assertNotNull(result);
            assertEquals(0, result.getAvailableSeats());
        }

        @Test
        void toEntity_shouldMapWithZeroPrice() {
            FlightRequest request = new FlightRequest(
                    "SR001",
                    150,
                    departureTime,
                    arrivalTime,
                    0.0,
                    1L,
                    1L,
                    true);

            Flight result = flightMapper.toEntity(request);

            assertNotNull(result);
            assertEquals(0.0, result.getPrice());
        }
    }

    @Nested
    class ToSimpleResponseTests {
        @Test
        void toSimpleResponse_shouldMapAllFields_whenFlightHasAllData() {
            Flight flight = createFlight(1L, "SR001", testAircraft, testRoute);

            FlightSimpleResponse result = FlightMapper.toSimpleResponse(flight);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("SR001", result.flightNumber());
            assertEquals("Madrid", result.origin());
            assertEquals("Barcelona", result.destination());
            assertEquals(departureTime, result.departureDate());
            assertEquals(arrivalTime, result.arrivalDate());
            assertEquals(299.99, result.price());
            assertEquals(150, result.availableSeats());
        }

        @Test
        void toSimpleResponse_shouldReturnNullOrigin_whenRouteIsNull() {
            Flight flight = createFlight(1L, "SR001", testAircraft, null);

            FlightSimpleResponse result = FlightMapper.toSimpleResponse(flight);

            assertNotNull(result);
            assertNull(result.origin());
            assertNull(result.destination());
        }

        @Test
        void toSimpleResponse_shouldReturnNullOrigin_whenRouteOriginIsNull() {
            Route routeWithoutOrigin = Route.builder()
                    .id(1L)
                    .origin(null)
                    .destination(destinationAirport)
                    .build();
            Flight flight = createFlight(1L, "SR001", testAircraft, routeWithoutOrigin);

            FlightSimpleResponse result = FlightMapper.toSimpleResponse(flight);

            assertNotNull(result);
            assertNull(result.origin());
            assertEquals("Barcelona", result.destination());
        }

        @Test
        void toSimpleResponse_shouldReturnNullDestination_whenRouteDestinationIsNull() {
            Route routeWithoutDestination = Route.builder()
                    .id(1L)
                    .origin(originAirport)
                    .destination(null)
                    .build();
            Flight flight = createFlight(1L, "SR001", testAircraft, routeWithoutDestination);

            FlightSimpleResponse result = FlightMapper.toSimpleResponse(flight);

            assertNotNull(result);
            assertEquals("Madrid", result.origin());
            assertNull(result.destination());
        }

        @Test
        void toSimpleResponse_shouldMapCorrectly_withDifferentPrices() {
            Flight expensiveFlight = createFlightWithPrice(1L, "SR001", 999.99);
            Flight cheapFlight = createFlightWithPrice(2L, "SR002", 49.99);

            FlightSimpleResponse expensiveResult = FlightMapper.toSimpleResponse(expensiveFlight);
            FlightSimpleResponse cheapResult = FlightMapper.toSimpleResponse(cheapFlight);

            assertEquals(999.99, expensiveResult.price());
            assertEquals(49.99, cheapResult.price());
        }

        @Test
        void toSimpleResponse_shouldMapCorrectly_withDifferentSeats() {
            Flight fullFlight = createFlightWithSeats(1L, "SR001", 300);
            Flight emptyFlight = createFlightWithSeats(2L, "SR002", 0);

            FlightSimpleResponse fullResult = FlightMapper.toSimpleResponse(fullFlight);
            FlightSimpleResponse emptyResult = FlightMapper.toSimpleResponse(emptyFlight);

            assertEquals(300, fullResult.availableSeats());
            assertEquals(0, emptyResult.availableSeats());
        }
    }

    @Nested
    class ToResponseTests {
        @Test
        void toResponse_shouldMapAllFields_whenFlightHasAllData() {
            Flight flight = createFullFlight(1L, "SR001");

            FlightResponse result = FlightMapper.toResponse(flight);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("SR001", result.flightNumber());
            assertEquals(150, result.availableSeats());
            assertEquals(departureTime, result.departureTime());
            assertEquals(arrivalTime, result.arrivalTime());
            assertEquals(299.99, result.price());
            assertTrue(result.available());
            assertNotNull(result.aircraft());
            assertNotNull(result.route());
            assertNotNull(result.createdAt());
            assertNotNull(result.updatedAt());
        }

        @Test
        void toResponse_shouldMapAircraftDetails_whenAircraftExists() {
            Flight flight = createFullFlight(1L, "SR001");

            FlightResponse result = FlightMapper.toResponse(flight);

            assertNotNull(result.aircraft());
            assertEquals(1L, result.aircraft().id());
            assertEquals("Boeing 737", result.aircraft().model());
            assertEquals(180, result.aircraft().capacity());
        }

        @Test
        void toResponse_shouldReturnNullAircraft_whenAircraftIsNull() {
            Flight flight = createFlight(1L, "SR001", null, testRoute);

            FlightResponse result = FlightMapper.toResponse(flight);

            assertNotNull(result);
            assertNull(result.aircraft());
        }

        @Test
        void toResponse_shouldMapRouteDetails_whenRouteExists() {
            Flight flight = createFullFlight(1L, "SR001");

            FlightResponse result = FlightMapper.toResponse(flight);

            assertNotNull(result.route());
            assertEquals(1L, result.route().id());
            assertNotNull(result.route().origin());
            assertNotNull(result.route().destination());
            assertEquals("MAD", result.route().origin().code());
            assertEquals("BCN", result.route().destination().code());
        }

        @Test
        void toResponse_shouldReturnNullRoute_whenRouteIsNull() {
            Flight flight = createFlight(1L, "SR001", testAircraft, null);

            FlightResponse result = FlightMapper.toResponse(flight);

            assertNotNull(result);
            assertNull(result.route());
        }

        @Test
        void toResponse_shouldMapAvailability_whenFlightIsAvailable() {
            Flight flight = createFullFlight(1L, "SR001");
            flight.setAvailable(true);

            FlightResponse result = FlightMapper.toResponse(flight);

            assertTrue(result.available());
        }

        @Test
        void toResponse_shouldMapAvailability_whenFlightIsNotAvailable() {
            Flight flight = createFullFlight(1L, "SR001");
            flight.setAvailable(false);

            FlightResponse result = FlightMapper.toResponse(flight);

            assertFalse(result.available());
        }

        @Test
        void toResponse_shouldMapTimestamps_whenPresent() {
            Flight flight = createFullFlight(1L, "SR001");
            LocalDateTime createdAt = LocalDateTime.now().minusDays(10);
            LocalDateTime updatedAt = LocalDateTime.now().minusDays(2);
            flight.setCreatedAt(createdAt);
            flight.setUpdatedAt(updatedAt);

            FlightResponse result = FlightMapper.toResponse(flight);

            assertEquals(createdAt, result.createdAt());
            assertEquals(updatedAt, result.updatedAt());
        }

        @Test
        void toResponse_shouldMapCorrectly_withZeroSeats() {
            Flight flight = createFullFlight(1L, "SR001");
            flight.setAvailableSeats(0);

            FlightResponse result = FlightMapper.toResponse(flight);

            assertEquals(0, result.availableSeats());
        }

        @Test
        void toResponse_shouldMapCorrectly_withHighPrice() {
            Flight flight = createFullFlight(1L, "SR001");
            flight.setPrice(9999.99);

            FlightResponse result = FlightMapper.toResponse(flight);

            assertEquals(9999.99, result.price());
        }

        @Test
        void toResponse_shouldMapMultipleFlights_correctly() {
            Flight flight1 = createFullFlight(1L, "SR001");
            Flight flight2 = createFullFlight(2L, "SR002");
            flight2.setAvailableSeats(200);
            flight2.setPrice(499.99);

            FlightResponse result1 = FlightMapper.toResponse(flight1);
            FlightResponse result2 = FlightMapper.toResponse(flight2);

            assertEquals(1L, result1.id());
            assertEquals("SR001", result1.flightNumber());
            assertEquals(150, result1.availableSeats());
            assertEquals(299.99, result1.price());

            assertEquals(2L, result2.id());
            assertEquals("SR002", result2.flightNumber());
            assertEquals(200, result2.availableSeats());
            assertEquals(499.99, result2.price());
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

    private Flight createFlightWithPrice(Long id, String flightNumber, double price) {
        Flight flight = createFlight(id, flightNumber, testAircraft, testRoute);
        flight.setPrice(price);
        return flight;
    }

    private Flight createFlightWithSeats(Long id, String flightNumber, int seats) {
        Flight flight = createFlight(id, flightNumber, testAircraft, testRoute);
        flight.setAvailableSeats(seats);
        return flight;
    }

    private Flight createFullFlight(Long id, String flightNumber) {
        Flight flight = createFlight(id, flightNumber, testAircraft, testRoute);
        flight.setCreatedAt(LocalDateTime.now().minusDays(1));
        flight.setUpdatedAt(LocalDateTime.now());
        return flight;
    }
}