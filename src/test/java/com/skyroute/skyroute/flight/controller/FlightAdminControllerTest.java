package com.skyroute.skyroute.flight.controller;

import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.admin.FlightResponse;
import com.skyroute.skyroute.flight.service.admin.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlightAdminControllerTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightAdminController flightAdminController;

    private FlightRequest request;
    private FlightResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new FlightRequest(
                "FL123",
                150,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                100.0,
                1L,
                1L,
                true
        );

        response = new FlightResponse(
                1L,
                "FL123",
                150,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                100.0,
                true,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createFlight_ShouldReturnFlightResponse() {
        when(flightService.createFlight(request)).thenReturn(response);

        ResponseEntity<FlightResponse> result = flightAdminController.createFlight(request);

        assertNotNull(result);
        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(flightService, times(1)).createFlight(request);
    }

    @Test
    void updateFlight_ShouldReturnUpdatedFlightResponse() {
        Long flightId = 1L;
        when(flightService.updateFlight(flightId, request)).thenReturn(response);

        ResponseEntity<FlightResponse> result = flightAdminController.updateFlight(flightId, request);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());

        verify(flightService, times(1)).updateFlight(flightId, request);
    }

    @Test
    void getFlightById_ShouldReturnFlightResponse() {
        Long flightId = 1L;
        when(flightService.getFlightById(flightId)).thenReturn(response);

        ResponseEntity<FlightResponse> result = flightAdminController.getFlightById(flightId);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());

        verify(flightService, times(1)).getFlightById(flightId);
    }

    @Test
    void getFlightsPage_ShouldReturnPagedFlights() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FlightResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        when(flightService.getFlightsPage(pageable)).thenReturn(page);

        ResponseEntity<Page<FlightResponse>> result = flightAdminController.getFlightsPage(pageable);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().getTotalElements());
        assertEquals(response, result.getBody().getContent().get(0));

        verify(flightService, times(1)).getFlightsPage(pageable);
    }

    @Test
    void deleteFlight_ShouldReturnNoContent() {
        Long flightId = 1L;
        doNothing().when(flightService).deleteFlight(flightId);

        ResponseEntity<Void> result = flightAdminController.deleteFlight(flightId);

        assertEquals(204, result.getStatusCodeValue());
        verify(flightService, times(1)).deleteFlight(flightId);
    }
}
