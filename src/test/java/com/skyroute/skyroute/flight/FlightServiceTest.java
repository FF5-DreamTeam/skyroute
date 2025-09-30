package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.flight.dto.MinPriceResponse;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.service.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    private FlightServiceImpl flightService;

    @BeforeEach
    void setUp() {
        flightService = new FlightServiceImpl(flightRepository, null, null, null);
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
}
