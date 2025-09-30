package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.flight.controller.FlightController;
import com.skyroute.skyroute.flight.dto.MinPriceResponse;
import com.skyroute.skyroute.flight.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FlightController(flightService)).build();
    }

    @Test
    void getMinPrices_shouldReturnMinPrices_whenValidDestinations() throws Exception {
        List<MinPriceResponse> expectedResponse = List.of(
                new MinPriceResponse("BCN", "Barcelona", 299.0),
                new MinPriceResponse("MAD", "Madrid", 249.0));

        when(flightService.getMinPricesByDestinations(anyList())).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/flights/min-prices")
                .param("destinations", "BCN,MAD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].destinationCode").value("BCN"))
                .andExpect(jsonPath("$[0].destinationCity").value("Barcelona"))
                .andExpect(jsonPath("$[0].minPrice").value(299.0))
                .andExpect(jsonPath("$[1].destinationCode").value("MAD"))
                .andExpect(jsonPath("$[1].destinationCity").value("Madrid"))
                .andExpect(jsonPath("$[1].minPrice").value(249.0));
    }

    @Test
    void getMinPrices_shouldReturnEmptyList_whenNoDestinations() throws Exception {
        when(flightService.getMinPricesByDestinations(anyList())).thenReturn(List.of());

        mockMvc.perform(get("/api/flights/min-prices")
                .param("destinations", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getMinPrices_shouldHandleSingleDestination() throws Exception {
        List<MinPriceResponse> expectedResponse = List.of(
                new MinPriceResponse("BCN", "Barcelona", 299.0));

        when(flightService.getMinPricesByDestinations(anyList())).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/flights/min-prices")
                .param("destinations", "BCN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].destinationCode").value("BCN"))
                .andExpect(jsonPath("$[0].destinationCity").value("Barcelona"))
                .andExpect(jsonPath("$[0].minPrice").value(299.0));
    }
}
