//package com.skyroute.skyroute.flight;
//
//import com.skyroute.skyroute.flight.dto.publicapi.FlightMapper;
//import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
//import com.skyroute.skyroute.flight.entity.Flight;
//import com.skyroute.skyroute.flight.service.publicapi.FlightPublicServiceImpl;
//import com.skyroute.skyroute.route.entity.Route;
//import com.skyroute.skyroute.flight.repository.FlightRepository;
//import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class FlightPublicServiceImplTest {
//
//    @Mock
//    private FlightRepository flightRepository;
//
//    @Mock
//    private FlightMapper flightMapper;
//
//    @InjectMocks
//    private FlightPublicServiceImpl service;
//
//    private Flight flight;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        Route route = new Route();
//        route.setId(1L);
//
//        flight = new Flight();
//        flight.setId(1L);
//        flight.setFlightNumber("SR123");
//        flight.setAvailableSeats(10);
//        flight.setDepartureTime(LocalDateTime.now().plusHours(5));
//        flight.setArrivalTime(LocalDateTime.now().plusHours(7));
//        flight.setPrice(200.0);
//        flight.setAvailable(true);
//        flight.setRoute(route);
//
//        when(flightMapper.toSimpleResponse(flight)).thenReturn(new FlightSimpleResponse(flight));
//    }
//
//    @Test
//    void testGetFlightById_found() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
//
//        FlightSimpleResponse response = service.getFlightById(1L);
//
//        assertNotNull(response);
//        assertEquals("SR123", response.flightNumber());
//    }
//
//    @Test
//    void testGetFlightById_notFound() {
//        when(flightRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> service.getFlightById(1L));
//    }
//}






