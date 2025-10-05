package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.repository.AirportRepository;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.specification.FlightSpecification;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.repository.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class FlightSpecificationTest {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Airport madridAirport;
    private Airport barcelonaAirport;
    private Airport sevillaAirport;
    private Airport valenciaAirport;
    private Route madridBarcelonaRoute;
    private Route madridSevillaRoute;
    private Route barcelonaValenciaRoute;
    private Aircraft testAircraft;
    private Aircraft largeAircraft;

    @BeforeEach
    void setUp() {
        flightRepository.deleteAll();
        routeRepository.deleteAll();
        airportRepository.deleteAll();
        aircraftRepository.deleteAll();

        entityManager.flush();
        entityManager.clear();

        madridAirport = entityManager.persistAndFlush(createAirport("MAD", "Madrid"));
        barcelonaAirport = entityManager.persistAndFlush(createAirport("BCN", "Barcelona"));
        sevillaAirport = entityManager.persistAndFlush(createAirport("SVQ", "Sevilla"));
        valenciaAirport = entityManager.persistAndFlush(createAirport("VLC", "Valencia"));

        testAircraft = entityManager.persistAndFlush(createAircraft("Boeing", "737", 180));
        largeAircraft = entityManager.persistAndFlush(createAircraft("Airbus", "A380", 853));

        madridBarcelonaRoute = entityManager.persistAndFlush(createRoute(madridAirport, barcelonaAirport));
        madridSevillaRoute = entityManager.persistAndFlush(createRoute(madridAirport, sevillaAirport));
        barcelonaValenciaRoute = entityManager.persistAndFlush(createRoute(barcelonaAirport, valenciaAirport));
    }

    @Nested
    class HasOriginEqualsTests{
        @Test
        void hasOriginEquals_shouldReturnFlights_whenMatchingAirportCode() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MAD");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(f ->
                    f.getRoute().getOrigin().getCode().equals("MAD")));
        }

        @Test
        void hasOriginEquals_shouldReturnFlights_whenMatchingCityName() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", barcelonaValenciaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("Madrid");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("Madrid", results.getFirst().getRoute().getOrigin().getCity());
        }

        @Test
        void hasOriginEquals_shouldBeCaseInsensitive_whenMatchingCityLowercase() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("madrid");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
        }

        @Test
        void hasOriginEquals_shouldBeCaseInsensitive_whenMatchingCityMixedCase() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MaDrId");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
        }

        @Test
        void hasOriginEquals_shouldReturnEmpty_whenNoMatch() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("BCN");
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasOriginEquals_shouldReturnEmpty_whenNonExistentCode() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("XXX");
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasOriginEquals_shouldReturnAll_whenNullOrigin() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals(null);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasOriginEquals_shouldReturnAll_whenBlankOrigin() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("   ");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasOriginEquals_shouldReturnAll_whenEmptyString() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasOriginEquals_shouldMatchCodeOverCity_whenBothMatch() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MAD");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }
    }

    @Nested
    class HasDestinationEqualsTests {
        @Test
        void hasDestinationEquals_shouldReturnFlights_whenMatchingAirportCode() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", barcelonaValenciaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("BCN");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("BCN", results.get(0).getRoute().getDestination().getCode());
        }

        @Test
        void hasDestinationEquals_shouldReturnFlights_whenMatchingCityName() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("Barcelona");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("Barcelona", results.get(0).getRoute().getDestination().getCity());
        }

        @Test
        void hasDestinationEquals_shouldBeCaseInsensitive_whenMatchingCity() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("barcelona");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
        }

        @Test
        void hasDestinationEquals_shouldBeCaseInsensitive_whenMatchingCityUppercase() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("BARCELONA");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
        }

        @Test
        void hasDestinationEquals_shouldBeCaseInsensitive_whenMatchingCityMixedCase() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("BaRcElOnA");
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
        }

        @Test
        void hasDestinationEquals_shouldReturnEmpty_whenNoMatch() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("SVQ");
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasDestinationEquals_shouldReturnEmpty_whenNonExistentCity() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("Paris");
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasDestinationEquals_shouldReturnAll_whenNullDestination() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals(null);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasDestinationEquals_shouldReturnAll_whenBlankDestination() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasDestinationEquals_shouldReturnAll_whenWhitespaceString() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("  \t  ");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }
    }

    @Nested
    class HasDepartureDateEqualsTests {
        @Test
        void hasDepartureDateEquals_shouldReturnFlights_whenMatchingDate() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));

            String dateStr = tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals(dateStr);
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("SR001", results.getFirst().getFlightNumber());
        }

        @Test
        void hasDepartureDateEquals_shouldReturnFlights_whenMultipleFlightsSameDay() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, tomorrow.plusHours(5), tomorrow.plusHours(7));

            String dateStr = tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals(dateStr);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasDepartureDateEquals_shouldReturnEmpty_whenDateDoesNotMatch() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));

            String futureDate = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals(futureDate);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasDepartureDateEquals_shouldReturnAll_whenNullDate() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, tomorrow.plusDays(1), tomorrow.plusDays(1).plusHours(2));

            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals(null);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasDepartureDateEquals_shouldReturnAll_whenBlankDate() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));

            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals("   ");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void hasDepartureDateEquals_shouldReturnAll_whenInvalidDateFormat() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));

            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals("invalid-date");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void hasDepartureDateEquals_shouldReturnAll_whenWrongFormatDate() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));

            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals("2025-12-01");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void hasDepartureDateEquals_shouldFilterByAvailability() {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);

            Flight availableFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, tomorrow, tomorrow.plusHours(2));
            availableFlight.setAvailable(true);
            flightRepository.save(availableFlight);

            Flight unavailableFlight = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, tomorrow, tomorrow.plusHours(2));
            unavailableFlight.setAvailable(false);
            flightRepository.save(unavailableFlight);

            String dateStr = tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals(dateStr);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR001", results.get(0).getFlightNumber());
            assertTrue(results.get(0).isAvailable());
        }

        @Test
        void hasDepartureDateEquals_shouldHandleEndOfMonth() {
            LocalDateTime endOfMonth = LocalDate.of(2025, 12, 31).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, endOfMonth, endOfMonth.plusHours(2));

            String dateStr = "31/12/2025";
            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals(dateStr);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void hasDepartureDateEquals_shouldHandleLeapYear() {
            LocalDateTime leapDay = LocalDate.of(2024, 2, 29).atTime(10, 0);
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, leapDay, leapDay.plusHours(2));

            String dateStr = "29/02/2024";
            Specification<Flight> specification = FlightSpecification.hasDepartureDateEquals(dateStr);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }
    }

    @Nested
    class HasPassengersAvailableTests{
        @Test
        void hasPassengersAvailable_shouldReturnFlights_whenEnoughSeats() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 50, 199.99);

            Specification<Flight> specification = FlightSpecification.hasPassengersAvailable(100);
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
            assertTrue(results.getFirst().getAvailableSeats() >= 100);
        }

        @Test
        void hasPassengersAvailable_shouldReturnFlights_whenExactSeats() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasPassengersAvailable(150);
            List<Flight> results = flightRepository.findAll(specification);

            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals(150, results.getFirst().getAvailableSeats());
        }

        @Test
        void hasPassengersAvailable_shouldReturnEmpty_whenNotEnoughSeats() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 100, 299.99);

            Specification<Flight> specification = FlightSpecification.hasPassengersAvailable(150);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasPassengersAvailable_shouldReturnAll_whenNullPassengers() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 50, 199.99);

            Specification<Flight> specification = FlightSpecification.hasPassengersAvailable(null);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasPassengersAvailable_shouldHandleZeroSeats() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 0, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 50, 199.99);

            Specification<Flight> specification = FlightSpecification.hasPassengersAvailable(0);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasPassengersAvailable_shouldHandleLargeNumbers() {
            Flight largeFlight = createFlight("SR001", barcelonaValenciaRoute, 853, 999.99);
            largeFlight.setAircraft(largeAircraft);
            flightRepository.save(largeFlight);

            Specification<Flight> specification = FlightSpecification.hasPassengersAvailable(800);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void hasPassengersAvailable_shouldReturnMultipleFlights() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 200, 199.99);
            createAndSaveFlight("SR003", barcelonaValenciaRoute, 180, 249.99);

            Specification<Flight> specification = FlightSpecification.hasPassengersAvailable(150);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(3, results.size());
        }
    }

    @Nested
    class IsOnlyAvailableTests{
        @Test
        void isOnlyAvailable_shouldReturnOnlyAvailableFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime future = now.plusDays(1);

            Flight availableFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, future, future.plusHours(2));
            availableFlight.setAvailable(true);
            flightRepository.save(availableFlight);

            Flight unavailableFlight = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, future, future.plusHours(2));
            unavailableFlight.setAvailable(false);
            flightRepository.save(unavailableFlight);

            Specification<Flight> specification = FlightSpecification.isOnlyAvailable(now, true);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR001", results.getFirst().getFlightNumber());
            assertTrue(results.getFirst().isAvailable());
        }

        @Test
        void isOnlyAvailable_shouldExcludePastFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past = now.minusDays(1);
            LocalDateTime future = now.plusDays(1);

            Flight pastFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, past, past.plusHours(2));
            pastFlight.setAvailable(true);
            flightRepository.save(pastFlight);

            Flight futureFlight = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, future, future.plusHours(2));
            futureFlight.setAvailable(true);
            flightRepository.save(futureFlight);

            Specification<Flight> specification = FlightSpecification.isOnlyAvailable(now, true);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR002", results.getFirst().getFlightNumber());
        }

        @Test
        void isOnlyAvailable_shouldReturnEmpty_whenNoAvailableFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime future = now.plusDays(1);

            Flight unavailableFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, future, future.plusHours(2));
            unavailableFlight.setAvailable(false);
            flightRepository.save(unavailableFlight);

            Specification<Flight> specification = FlightSpecification.isOnlyAvailable(now, true);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void isOnlyAvailable_shouldReturnEmpty_whenAllFlightsInPast() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past = now.minusDays(1);

            Flight pastFlight1 = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, past, past.plusHours(2));
            pastFlight1.setAvailable(true);
            flightRepository.save(pastFlight1);

            Flight pastFlight2 = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, past.minusDays(1), past.minusDays(1).plusHours(2));
            pastFlight2.setAvailable(true);
            flightRepository.save(pastFlight2);

            Specification<Flight> specification = FlightSpecification.isOnlyAvailable(now, true);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void isOnlyAvailable_shouldHandleDepartureAtExactTime() {
            LocalDateTime now = LocalDateTime.now().withNano(0);

            Flight exactTimeFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, now, now.plusHours(2));
            exactTimeFlight.setAvailable(true);
            flightRepository.save(exactTimeFlight);

            Specification<Flight> specification = FlightSpecification.isOnlyAvailable(now, true);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void isOnlyAvailable_shouldReturnMultipleFutureAvailableFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime future1 = now.plusDays(1);
            LocalDateTime future2 = now.plusDays(2);

            Flight flight1 = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, future1, future1.plusHours(2));
            flight1.setAvailable(true);
            flightRepository.save(flight1);

            Flight flight2 = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, future2, future2.plusHours(2));
            flight2.setAvailable(true);
            flightRepository.save(flight2);

            Specification<Flight> specification = FlightSpecification.isOnlyAvailable(now, true);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }
    }

    @Nested
    class HasPriceLessThanOrEqualTests{
        @Test
        void hasPriceLessThanOrEqual_shouldReturnFlights_whenPriceWithinBudget() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);
            createAndSaveFlight("SR003", barcelonaValenciaRoute, 120, 399.99);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(300.0);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(f -> f.getPrice() <= 300.0));
        }

        @Test
        void hasPriceLessThanOrEqual_shouldReturnFlights_whenPriceExactlyAtBudget() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(299.99);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals(299.99, results.get(0).getPrice());
        }

        @Test
        void hasPriceLessThanOrEqual_shouldReturnEmpty_whenAllFlightsAboveBudget() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 399.99);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(150.0);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasPriceLessThanOrEqual_shouldReturnAll_whenNullPrice() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 399.99);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(null);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasPriceLessThanOrEqual_shouldHandleZeroPrice() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 0.0);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(0.0);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals(0.0, results.get(0).getPrice());
        }

        @Test
        void hasPriceLessThanOrEqual_shouldHandleVeryHighPrices() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 9999.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 8888.88);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(10000.0);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasPriceLessThanOrEqual_shouldHandleDecimalPrecision() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.999);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 300.001);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(300.0);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertTrue(results.get(0).getPrice() < 300.0);
        }

        @Test
        void hasPriceLessThanOrEqual_shouldReturnMultipleFlights() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 100.0);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 200.0);
            createAndSaveFlight("SR003", barcelonaValenciaRoute, 120, 300.0);

            Specification<Flight> specification = FlightSpecification.hasPricelessThanOrEqual(250.0);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }
    }

    @Nested
    class HasDepartureBeforeTests {
        @Test
        void hasDepartedBefore_shouldReturnDepartedFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past = now.minusDays(1);

            Flight departedFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, past, past.plusHours(2));
            departedFlight.setAvailable(true);
            flightRepository.save(departedFlight);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR001", results.get(0).getFlightNumber());
        }

        @Test
        void hasDepartedBefore_shouldReturnMultipleDepartedFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past1 = now.minusDays(1);
            LocalDateTime past2 = now.minusDays(2);

            Flight flight1 = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, past1, past1.plusHours(2));
            flight1.setAvailable(true);
            flightRepository.save(flight1);

            Flight flight2 = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, past2, past2.plusHours(2));
            flight2.setAvailable(true);
            flightRepository.save(flight2);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(2, results.size());
        }

        @Test
        void hasDepartedBefore_shouldExcludeFutureFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime future = now.plusDays(1);

            Flight futureFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, future, future.plusHours(2));
            futureFlight.setAvailable(true);
            flightRepository.save(futureFlight);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasDepartedBefore_shouldOnlyReturnAvailableFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past = now.minusDays(1);

            Flight availableDepartedFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, past, past.plusHours(2));
            availableDepartedFlight.setAvailable(true);
            flightRepository.save(availableDepartedFlight);

            Flight unavailableDepartedFlight = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, past, past.plusHours(2));
            unavailableDepartedFlight.setAvailable(false);
            flightRepository.save(unavailableDepartedFlight);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR001", results.get(0).getFlightNumber());
            assertTrue(results.get(0).isAvailable());
        }

        @Test
        void hasDepartedBefore_shouldReturnEmpty_whenNoDepartedAvailableFlights() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime future = now.plusDays(1);

            Flight futureFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, future, future.plusHours(2));
            futureFlight.setAvailable(true);
            flightRepository.save(futureFlight);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasDepartedBefore_shouldExcludeFlightsAtExactTime() {
            LocalDateTime now = LocalDateTime.now().withNano(0);

            Flight exactTimeFlight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, now, now.plusHours(2));
            exactTimeFlight.setAvailable(true);
            flightRepository.save(exactTimeFlight);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void hasDepartedBefore_shouldHandleOneMinuteBeforeNow() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneMinuteBefore = now.minusMinutes(1);

            Flight flight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, oneMinuteBefore, oneMinuteBefore.plusHours(2));
            flight.setAvailable(true);
            flightRepository.save(flight);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void hasDepartedBefore_shouldHandleOneSecondBeforeNow() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneSecondBefore = now.minusSeconds(1);

            Flight flight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, oneSecondBefore, oneSecondBefore.plusHours(2));
            flight.setAvailable(true);
            flightRepository.save(flight);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void hasDepartedBefore_shouldHandleMixedScenario() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past = now.minusDays(1);
            LocalDateTime future = now.plusDays(1);

            Flight flightMadridBarcelona = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, past, past.plusHours(2));
            flightMadridBarcelona.setAvailable(true);
            flightRepository.save(flightMadridBarcelona);

            Flight flightMadridSevilla = createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99, past, past.plusHours(2));
            flightMadridSevilla.setAvailable(false);
            flightRepository.save(flightMadridSevilla);

            Flight flightBarcelonaValencia = createAndSaveFlight("SR003", barcelonaValenciaRoute, 120, 249.99, future, future.plusHours(2));
            flightBarcelonaValencia.setAvailable(true);
            flightRepository.save(flightBarcelonaValencia);

            Specification<Flight> specification = FlightSpecification.hasDepartedBefore(now);
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR001", results.get(0).getFlightNumber());
        }
    }

    @Nested
    class CombinedSpecificationsTests {
        @Test
        void shouldCombineOriginAndDestination() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridSevillaRoute, 100, 199.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MAD")
                    .and(FlightSpecification.hasDestinationEquals("BCN"));

            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR001", results.get(0).getFlightNumber());
        }

        @Test
        void shouldCombineMultipleSpecifications() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime future = now.plusDays(1);

            Flight flight = createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99, future, future.plusHours(2));
            flight.setAvailable(true);
            flightRepository.save(flight);

            createAndSaveFlight("SR002", madridSevillaRoute, 50, 399.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MAD")
                    .and(FlightSpecification.hasPassengersAvailable(100))
                    .and(FlightSpecification.hasPricelessThanOrEqual(350.0))
                    .and(FlightSpecification.isOnlyAvailable(now, true));

            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
            assertEquals("SR001", results.getFirst().getFlightNumber());
        }

        @Test
        void shouldReturnEmpty_whenCombinedSpecificationsNoMatch() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MAD")
                    .and(FlightSpecification.hasDestinationEquals("VLC"));

            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }
    }

    @Nested
    class EdgeCasesAndBoundaryTests {
        @Test
        void shouldHandleEmptyDatabase() {
            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MAD");
            List<Flight> results = flightRepository.findAll(specification);

            assertTrue(results.isEmpty());
        }

        @Test
        void shouldHandleSpecialCharactersInCityNames() {
            Airport specialAirport = entityManager.persistAndFlush(
                    createAirport("TST", "São Paulo")
            );
            Route specialRoute = entityManager.persistAndFlush(
                    createRoute(madridAirport, specialAirport)
            );
            createAndSaveFlight("SR001", specialRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("São Paulo");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void shouldHandleVeryLongCityNames() {
            Airport longNameAirport = entityManager.persistAndFlush(
                    createAirport("LNG", "A Very Long City Name With Many Words")
            );
            Route longNameRoute = entityManager.persistAndFlush(
                    createRoute(madridAirport, longNameAirport)
            );
            createAndSaveFlight("SR001", longNameRoute, 150, 299.99);

            Specification<Flight> specification = FlightSpecification.hasDestinationEquals("A Very Long City Name With Many Words");
            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(1, results.size());
        }

        @Test
        void shouldHandleMultipleFlightsWithSameRoute() {
            createAndSaveFlight("SR001", madridBarcelonaRoute, 150, 299.99);
            createAndSaveFlight("SR002", madridBarcelonaRoute, 100, 199.99);
            createAndSaveFlight("SR003", madridBarcelonaRoute, 200, 399.99);

            Specification<Flight> specification = FlightSpecification.hasOriginEquals("MAD")
                    .and(FlightSpecification.hasDestinationEquals("BCN"));

            List<Flight> results = flightRepository.findAll(specification);

            assertEquals(3, results.size());
        }
    }

    private Airport createAirport(String code, String city) {
        return Airport.builder()
                .code(code)
                .city(city)
                .imageUrl("http://example.com/" + code.toLowerCase() + ".jpg")
                .build();
    }

    private Aircraft createAircraft(String manufacturer, String model, int capacity) {
        return Aircraft.builder()
                .manufacturer(manufacturer)
                .model(model)
                .capacity(capacity)
                .build();
    }

    private Route createRoute(Airport origin, Airport destination) {
        return Route.builder()
                .origin(origin)
                .destination(destination)
                .build();
    }

    private Flight createFlight(String flightNumber, Route route, int availableSeats, double price) {
        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        LocalDateTime arrival = departure.plusHours(2);
        return Flight.builder()
                .flightNumber(flightNumber)
                .route(route)
                .aircraft(testAircraft)
                .availableSeats(availableSeats)
                .price(price)
                .departureTime(departure)
                .arrivalTime(arrival)
                .available(true)
                .build();
    }

    private Flight createAndSaveFlight(String flightNumber, Route route, int availableSeats, double price) {
        Flight flight = createFlight(flightNumber, route, availableSeats, price);
        return flightRepository.save(flight);
    }

    private Flight createAndSaveFlight(String flightNumber, Route route, int availableSeats, double price,
                                       LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Flight flight = Flight.builder()
                .flightNumber(flightNumber)
                .route(route)
                .aircraft(testAircraft)
                .availableSeats(availableSeats)
                .price(price)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .available(true)
                .build();
        return flightRepository.save(flight);
    }
}
