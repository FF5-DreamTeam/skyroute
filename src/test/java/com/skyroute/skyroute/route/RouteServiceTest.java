package com.skyroute.skyroute.route;

import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.service.AirportService;
import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.repository.RouteRepository;
import com.skyroute.skyroute.route.service.RouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidRouteException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RouteServiceTest {
    @Mock
    private RouteRepository routeRepository;

    @Mock
    private AirportService airportService;

    @InjectMocks
    private RouteServiceImpl routeService;

    private RouteRequest testRouteRequest;
    private Airport originAirport;
    private Airport destinationAirport;
    private Route testRoute;

    @BeforeEach
    void setUp(){
        testRouteRequest = new RouteRequest(1L, 2L);
        originAirport = createTestAirport(1L, "MAD", "Madrid");
        destinationAirport = createTestAirport(2L, "BCN", "Barcelona");
        testRoute = createTestRoute();
    }

    @Nested
    class CreateRouteTests{
        @Test
        void createRoute_shouldReturnRouteResponse_whenValidRequest(){
            when(airportService.findAirportById(1L)).thenReturn(originAirport);
            when(airportService.findAirportById(2L)).thenReturn(destinationAirport);
            when(routeRepository.existsByOriginIdAndDestinationId(1L, 2L)).thenReturn(false);
            when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

            RouteResponse result = routeService.createRoute(testRouteRequest);

            assertNotNull(result);
            assertEquals(testRoute.getId(), result.id());
            assertEquals(originAirport.getId(), result.origin().id());
            assertEquals(originAirport.getCode(), result.origin().code());
            assertEquals(originAirport.getCity(), result.origin().city());
            assertEquals(destinationAirport.getId(), result.destination().id());
            assertEquals(destinationAirport.getCode(), result.destination().code());
            assertEquals(destinationAirport.getCity(), result.destination().city());

            verify(airportService).findAirportById(1L);
            verify(airportService).findAirportById(2L);
            verify(routeRepository).existsByOriginIdAndDestinationId(1L, 2L);
            verify(routeRepository).save(any(Route.class));
        }

        @Test
        void createRoute_shouldThrowInvalidRouteException_whenOriginAndDestinationAreSame(){
            RouteRequest sameAirportRequest = new RouteRequest(1L, 1L);

            InvalidRouteException exception = assertThrows(
                    InvalidRouteException.class,
                    () -> routeService.createRoute(sameAirportRequest)
            );

            assertEquals("Origin and destination airports cannot be the same", exception.getMessage());

            verify(airportService, never()).getAirportById(anyLong());
            verify(routeRepository, never()).existsByOriginIdAndDestinationId(anyLong(), anyLong());
            verify(routeRepository, never()).save(any());
        }

        @Test
        void createRoute_shouldThrowEntityNotFoundException_WhenOriginAirportNotFound(){
            when(airportService.findAirportById(1L))
                    .thenThrow(new EntityNotFoundException("Airport not found with ID: 1"));

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> routeService.createRoute(testRouteRequest)
            );

            assertEquals("Airport not found with ID: 1", exception.getMessage());
            verify(airportService).findAirportById(1L);
            verify(airportService, never()).findAirportById(2L);
            verify(routeRepository, never()).existsByOriginIdAndDestinationId(anyLong(), anyLong());
            verify(routeRepository, never()).save(any());
        }

        @Test
        void createRoute_shouldThrowEntityNotFoundException_whenDestinationAirportNotFound(){
            when(airportService.findAirportById(1L)).thenReturn(originAirport);
            when(airportService.findAirportById(2L))
                    .thenThrow(new EntityNotFoundException("Airport not found with ID: 2"));

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> routeService.createRoute(testRouteRequest)
            );

            assertEquals("Airport not found with ID: 2", exception.getMessage());
            verify(airportService).findAirportById(1L);
            verify(airportService).findAirportById(2L);
            verify(routeRepository, never()).existsByOriginIdAndDestinationId(anyLong(), anyLong());
            verify(routeRepository, never()).save(any());
        }

        @Test
        void createRoute_shouldThrownEntityAlreadyExistsException_whenRouteAlreadyExists(){
            when(airportService.findAirportById(1L)).thenReturn(originAirport);
            when(airportService.findAirportById(2L)).thenReturn(destinationAirport);
            when(routeRepository.existsByOriginIdAndDestinationId(1L, 2L)).thenReturn(true);

            EntityAlreadyExistsException exception = assertThrows(
                    EntityAlreadyExistsException.class,
                    () -> routeService.createRoute(testRouteRequest)
            );

            assertEquals("Route already exists between these airports: 1 -> 2", exception.getMessage());
            verify(airportService).findAirportById(1L);
            verify(airportService).findAirportById(2L);
            verify(routeRepository).existsByOriginIdAndDestinationId(1L, 2L);
            verify(routeRepository, never()).save(any());
        }
    }

    @Nested
    class GetAllRoutesTests {
        @Test
        void getAllRoutes_shouldReturnList_whenRoutesExist(){
            when(routeRepository.findAll()).thenReturn(List.of(testRoute));

            List<RouteResponse> result = routeService.getAllRoutes();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testRoute.getId(), result.getFirst().id());
            assertEquals(originAirport.getCode(), result.getFirst().origin().code());
            assertEquals(destinationAirport.getCode(), result.getFirst().destination().code());
            verify(routeRepository).findAll();
        }

        @Test
        void getAllRoutes_shouldReturnEmptyList_whenNoRoutesExist(){
            when(routeRepository.findAll()).thenReturn(List.of());

            List<RouteResponse> result = routeService.getAllRoutes();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(routeRepository).findAll();
        }
    }

    @Nested
    class GetRouteByIdTests{
        @Test
        void getRouteById_shouldReturnRouteResponse_whenRouteExists(){
            when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));

            RouteResponse result = routeService.getRouteById(1L);

            assertNotNull(result);
            assertEquals(testRoute.getId(), result.id());
            assertEquals(originAirport.getCode(), result.origin().code());
            assertEquals(destinationAirport.getCode(), result.destination().code());
            verify(routeRepository).findById(1L);
        }

        @Test
        void getRouteById_shouldThrowEntityNotFoundException_whenRouteDoesNotExist(){
            when(routeRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> routeService.getRouteById(99L)
            );

            assertEquals("Airport not found with ID: 99", exception.getMessage());
            verify(routeRepository).findById(99L);
        }
    }

    private Airport createTestAirport(Long id, String code, String city){
        return Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl("http://example.com/" + code.toLowerCase() + ".jpg")
                .build();
    }

    private Route createTestRoute(){
        return Route.builder()
                .id(1L)
                .origin(originAirport)
                .destination(destinationAirport)
                .build();
    }
}
