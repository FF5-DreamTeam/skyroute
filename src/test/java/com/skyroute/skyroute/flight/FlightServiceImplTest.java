//package com.skyroute.skyroute.flight;
//
//import com.skyroute.skyroute.aircraft.entity.Aircraft;
//import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
//import com.skyroute.skyroute.airport.entity.Airport;
//import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
//import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
//import com.skyroute.skyroute.flight.entity.Flight;
//import com.skyroute.skyroute.flight.repository.FlightRepository;
//import com.skyroute.skyroute.flight.service.admin.FlightServiceImpl;
//import com.skyroute.skyroute.flight.validation.FlightValidator;
//import com.skyroute.skyroute.route.entity.Route;
//import com.skyroute.skyroute.route.repository.RouteRepository;
//import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//public class FlightServiceImplTest {
//    @Mock
//    private FlightRepository flightRepository;
//    @Mock
//    private AircraftRepository aircraftRepository;
//    @Mock
//    private RouteRepository routeRepository;
//    @Mock
//    private FlightValidator flightAdminValidator;
//
//    @InjectMocks
//    private FlightServiceImpl flightService;
//    private Aircraft aircraft;
//    private Route route;
//    private Flight flight;
//    private FlightRequest createRequest;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        Airport origin = Airport.builder().id(1L).code("MAD").city("Madrid").build();
//        Airport destination = Airport.builder().id(2L).code("BCN").city("Barcelona").build();
//        route = Route.builder().id(1L).origin(origin).destination(destination).build();
//        aircraft = Aircraft.builder().id(1L).model("Boeing 737").manufacturer("Boeing").capacity(180).build();
//        flight = Flight.builder().id(1L).flightNumber("AB123").availableSeats(100).departureTime(LocalDateTime.now().plusDays(1)).arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2)).price(150.0).available(true).aircraft(aircraft).route(route).build();
//        createRequest = new FlightRequest("AB123", 100, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 150.0, 1L, 1L, true);
//        mockDependencies();
//        when(flightRepository.save(any(Flight.class))).thenAnswer(i -> i.getArgument(0));
//    }
//
//    private void mockDependencies() {
//        when(flightRepository.findById(anyLong())).thenReturn(Optional.of(flight));
//        when(aircraftRepository.findById(anyLong())).thenReturn(Optional.of(aircraft));
//        when(routeRepository.findById(anyLong())).thenReturn(Optional.of(route));
//    }
//
//    @Test
//    void testCreateFlight_Success() {
//        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
//        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
//        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
//        FlightResponse response = flightService.createFlight(createRequest);
//        assertNotNull(response);
//        assertEquals(flight.getId(), response.id());
//        assertEquals(flight.getFlightNumber(), response.flightNumber());
//        assertEquals(flight.getAvailableSeats(), response.availableSeats());
//        assertEquals(flight.getPrice(), response.price());
//        assertTrue(response.available());
//        assertNotNull(response.aircraft());
//        assertNotNull(response.route());
//    }
//
//    @Test
//    void testCreateFlight_AircraftNotFound() {
//        when(aircraftRepository.findById(1L)).thenReturn(Optional.empty());
//        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
//        doNothing().when(flightAdminValidator)
//                .validateFlightCreation(anyLong(), anyInt(), any(), any());
//        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> flightService.createFlight(createRequest));
//        assertEquals("Aircraft not found with id: 1", thrown.getMessage());
//    }
//
//    @Test
//    void testCreateFlight_RouteNotFound() {
//        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
//        when(routeRepository.findById(1L)).thenReturn(Optional.empty());
//        doNothing().when(flightAdminValidator)
//                .validateFlightCreation(anyLong(), anyInt(), any(), any());
//        assertThrows(EntityNotFoundException.class, () -> flightService.createFlight(createRequest));
//    }
//
//    @Test
//    void testUpdateFlight_Success() {
//        FlightRequest updateRequest = new FlightRequest("CD456", 120, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(3), 200.0, 1L, 1L, false);
//        mockDependencies();
//        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
//        FlightResponse response = flightService.updateFlight(1L, updateRequest);
//        assertNotNull(response);
//        assertEquals("CD456", response.flightNumber());
//        assertEquals(120, response.availableSeats());
//        assertEquals(200.0, response.price());
//        assertFalse(response.available());
//    }
//
//    @Test
//    void testUpdateFlight_PartialUpdate() {
//        FlightRequest partialUpdateRequest = new FlightRequest("XY789", 80, null, null, 180.0, 1L, 1L, true);
//        mockDependencies();
//        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
//        FlightResponse response = flightService.updateFlight(1L, partialUpdateRequest);
//        assertNotNull(response);
//        assertEquals("XY789", response.flightNumber());
//        assertEquals(80, response.availableSeats());
//        assertEquals(180.0, response.price());
//        assertTrue(response.available());
//        assertNotNull(response.aircraft());
//        assertNotNull(response.route());
//    }
//
//    @Test
//    void testUpdateFlight_AircraftNotFound() {
//        FlightRequest updateRequest = new FlightRequest("XY789", 80, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(1), 180.0, 2L, 1L, true);
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//        when(aircraftRepository.findById(2L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> flightService.updateFlight(1L, updateRequest));
//    }
//
//    @Test
//    void testUpdateFlight_RouteNotFound() {
//        FlightRequest updateRequest = new FlightRequest("XY789", 80, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(1), 180.0, 1L, 2L, true);
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
//        when(routeRepository.findById(2L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> flightService.updateFlight(1L, updateRequest));
//    }
//
//    @Test
//    void testDeleteFlight() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//        doNothing().when(flightRepository).delete(flight);
//        assertDoesNotThrow(() -> flightService.deleteFlight(1L));
//        verify(flightRepository, times(1)).delete(flight);
//    }
//
//    @Test
//    void testGetFlightById_Success() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//        FlightResponse response = flightService.getFlightById(1L);
//        assertNotNull(response);
//        assertEquals(flight.getId(), response.id());
//    }
//
//    @Test
//    void testGetFlightById_NotFound() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> flightService.getFlightById(1L));
//    }
//
//    @Test
//    void testGetAllFlights() {
//        when(flightRepository.findAll()).thenReturn(Arrays.asList(flight));
//        List<FlightResponse> flights = flightService.getAllFlights();
//        assertEquals(1, flights.size());
//    }
//
//    @Test
//    void testIsFlightAvailable() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//        assertTrue(flightService.isFlightAvailable(1L));
//    }
//
//    @Test
//    void testHasAvailableSeats() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//        assertTrue(flightService.hasAvailableSeats(1L, 50));
//        assertFalse(flightService.hasAvailableSeats(1L, 150));
//    }
//
//    @Test
//    void testFindEntityById_Success() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//        Flight found = flightService.findById(1L);
//        assertNotNull(found);
//        assertEquals(flight.getId(), found.getId());
//    }
//
//    @Test
//    void testFindEntityById_NotFound() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> flightService.findById(1L));
//    }
//}
