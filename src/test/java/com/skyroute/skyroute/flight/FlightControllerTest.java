package com.skyroute.skyroute.flight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.flight.dto.*;
import com.skyroute.skyroute.flight.service.FlightService;
import com.skyroute.skyroute.route.dto.RouteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class FlightControllerTest {
        @Autowired
        private WebApplicationContext context;

        @MockitoBean
        private FlightService flightService;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
        }

        @Nested
        class SearchFlightsTests {
                @Test
                void searchFlights_shouldReturnFlights_whenValidParameters() throws Exception {
                        FlightSimpleResponse flight = createFlightSimpleResponse();
                        PageImpl<FlightSimpleResponse> page = new PageImpl<>(List.of(flight));

                        when(flightService.searchFlights(any(), any(), any(), any(), any(Pageable.class)))
                                        .thenReturn(page);

                        mockMvc.perform(get("/api/flights/search")
                                        .param("origin", "MAD")
                                        .param("destination", "BCN")
                                        .param("departureDate", "01/12/2025")
                                        .param("passengers", "2"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.content[0].id").value(1L))
                                        .andExpect(jsonPath("$.content[0].flightNumber").value("SR001"))
                                        .andExpect(jsonPath("$.content[0].origin").value("Madrid"))
                                        .andExpect(jsonPath("$.content[0].destination").value("Barcelona"));

                        verify(flightService).searchFlights(
                                        eq(Optional.of("MAD")),
                                        eq(Optional.of("BCN")),
                                        eq(Optional.of("01/12/2025")),
                                        eq(Optional.of(2)),
                                        any(Pageable.class));
                }

                @Test
                void searchFlights_shouldReturnEmptyPage_whenNoFlightsFound() throws Exception {
                        PageImpl<FlightSimpleResponse> emptyPage = new PageImpl<>(List.of());

                        when(flightService.searchFlights(any(), any(), any(), any(), any(Pageable.class)))
                                        .thenReturn(emptyPage);

                        mockMvc.perform(get("/api/flights/search"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.content").isEmpty());

                        verify(flightService).searchFlights(any(), any(), any(), any(), any(Pageable.class));
                }

                @Test
                void searchFlights_shouldBeAccessible_withoutAuthentication() throws Exception {
                        PageImpl<FlightSimpleResponse> page = new PageImpl<>(List.of());

                        when(flightService.searchFlights(any(), any(), any(), any(), any(Pageable.class)))
                                        .thenReturn(page);

                        mockMvc.perform(get("/api/flights/search"))
                                        .andExpect(status().isOk());

                        verify(flightService).searchFlights(any(), any(), any(), any(), any(Pageable.class));
                }
        }

        @Nested
        class GetFlightByIdTests {
                @Test
                void getFlightById_shouldReturnFlight_whenFlightExists() throws Exception {
                        FlightSimpleResponse flight = createFlightSimpleResponse();
                        when(flightService.getFlightSimpleById(1L)).thenReturn(flight);

                        mockMvc.perform(get("/api/flights/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(1L))
                                        .andExpect(jsonPath("$.flightNumber").value("SR001"))
                                        .andExpect(jsonPath("$.origin").value("Madrid"))
                                        .andExpect(jsonPath("$.destination").value("Barcelona"));

                        verify(flightService).getFlightSimpleById(1L);
                }

                @Test
                void getFlightById_shouldReturnNotFound_whenFlightDoesNotExist() throws Exception {
                        when(flightService.getFlightSimpleById(99L))
                                        .thenThrow(new EntityNotFoundException("Flight with id: 99 not found"));

                        mockMvc.perform(get("/api/flights/99"))
                                        .andExpect(status().isNotFound());

                        verify(flightService).getFlightSimpleById(99L);
                }

                @Test
                void getFlightById_shouldBeAccessible_withoutAuthentication() throws Exception {
                        FlightSimpleResponse flight = createFlightSimpleResponse();
                        when(flightService.getFlightSimpleById(1L)).thenReturn(flight);

                        mockMvc.perform(get("/api/flights/1"))
                                        .andExpect(status().isOk());

                        verify(flightService).getFlightSimpleById(1L);
                }
        }

        @Nested
        class SearchFlightsByBudgetAndCityTests {
                @Test
                void searchFlightsByBudgetAndCity_shouldReturnFlights_whenValidFilters() throws Exception {
                        FlightSimpleResponse flight = createFlightSimpleResponse();
                        PageImpl<FlightSimpleResponse> page = new PageImpl<>(List.of(flight));

                        when(flightService.searchFlightsByBudgetAndCity(any(), any(), any(), any(Pageable.class)))
                                        .thenReturn(page);

                        mockMvc.perform(get("/api/flights/search-filters")
                                        .param("origin", "MAD")
                                        .param("destination", "BCN")
                                        .param("budget", "500.0"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.content[0].id").value(1L))
                                        .andExpect(jsonPath("$.content[0].price").value(299.99));

                        verify(flightService).searchFlightsByBudgetAndCity(
                                        eq(Optional.of("MAD")),
                                        eq(Optional.of("BCN")),
                                        eq(Optional.of(500.0)),
                                        any(Pageable.class));
                }

                @Test
                void searchFlightsByBudgetAndCity_shouldReturnFlights_withoutFilters() throws Exception {
                        PageImpl<FlightSimpleResponse> page = new PageImpl<>(List.of());

                        when(flightService.searchFlightsByBudgetAndCity(any(), any(), any(), any(Pageable.class)))
                                        .thenReturn(page);

                        mockMvc.perform(get("/api/flights/search-filters"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.content").isEmpty());

                        verify(flightService).searchFlightsByBudgetAndCity(any(), any(), any(), any(Pageable.class));
                }

                @Test
                void searchFlightsByBudgetAndCity_shouldBeAccessible_withoutAuthentication() throws Exception {
                        PageImpl<FlightSimpleResponse> page = new PageImpl<>(List.of());

                        when(flightService.searchFlightsByBudgetAndCity(any(), any(), any(), any(Pageable.class)))
                                        .thenReturn(page);

                        mockMvc.perform(get("/api/flights/search-filters"))
                                        .andExpect(status().isOk());

                        verify(flightService).searchFlightsByBudgetAndCity(any(), any(), any(), any(Pageable.class));
                }
        }

        @Nested
        class GetAllFlightsAdminTests {
                @Test
                @WithMockUser(roles = "ADMIN")
                void getAllFlights_shouldReturnFlights_whenAdmin() throws Exception {
                        FlightResponse flight = createFlightResponse();
                        PageImpl<FlightResponse> page = new PageImpl<>(List.of(flight));

                        when(flightService.getFlightsPage(any(Pageable.class))).thenReturn(page);

                        mockMvc.perform(get("/api/flights/admin"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.content[0].id").value(1L))
                                        .andExpect(jsonPath("$.content[0].flightNumber").value("SR001"))
                                        .andExpect(jsonPath("$.content[0].available").value(true));

                        verify(flightService).getFlightsPage(any(Pageable.class));
                }

                @Test
                @WithMockUser(roles = "USER")
                void getAllFlights_shouldReturnForbidden_whenNotAdmin() throws Exception {
                        mockMvc.perform(get("/api/flights/admin"))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).getFlightsPage(any(Pageable.class));
                }

                @Test
                void getAllFlights_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
                        mockMvc.perform(get("/api/flights/admin"))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).getFlightsPage(any(Pageable.class));
                }
        }

        @Nested
        class GetFlightDetailsByIdAdminTests {
                @Test
                @WithMockUser(roles = "ADMIN")
                void getFlightDetailsById_shouldReturnFlight_whenAdmin() throws Exception {
                        FlightResponse flight = createFlightResponse();
                        when(flightService.getFlightById(1L)).thenReturn(flight);

                        mockMvc.perform(get("/api/flights/admin/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(1L))
                                        .andExpect(jsonPath("$.flightNumber").value("SR001"))
                                        .andExpect(jsonPath("$.aircraft").exists())
                                        .andExpect(jsonPath("$.route").exists());

                        verify(flightService).getFlightById(1L);
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void getFlightDetailsById_shouldReturnNotFound_whenFlightDoesNotExist() throws Exception {
                        when(flightService.getFlightById(99L))
                                        .thenThrow(new EntityNotFoundException("Flight with id: 99 not found"));

                        mockMvc.perform(get("/api/flights/admin/99"))
                                        .andExpect(status().isNotFound());

                        verify(flightService).getFlightById(99L);
                }

                @Test
                @WithMockUser(roles = "USER")
                void getFlightDetailsById_shouldReturnForbidden_whenNotAdmin() throws Exception {
                        mockMvc.perform(get("/api/flights/admin/1"))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).getFlightById(anyLong());
                }
        }

        @Nested
        class CreateFlightTests {
                @Test
                @WithMockUser(roles = "ADMIN")
                void createFlight_shouldReturnCreated_whenValidRequest() throws Exception {
                        FlightRequest request = createFlightRequest();
                        FlightResponse response = createFlightResponse();

                        when(flightService.createFlight(any(FlightRequest.class))).thenReturn(response);

                        mockMvc.perform(post("/api/flights/admin")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isCreated())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(1L))
                                        .andExpect(jsonPath("$.flightNumber").value("SR001"));

                        verify(flightService).createFlight(any(FlightRequest.class));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void createFlight_shouldReturnBadRequest_whenInvalidFlightNumber() throws Exception {
                        FlightRequest request = new FlightRequest(
                                        "AB",
                                        150,
                                        LocalDateTime.now().plusDays(1),
                                        LocalDateTime.now().plusDays(1).plusHours(2),
                                        299.99,
                                        1L,
                                        1L,
                                        true);

                        mockMvc.perform(post("/api/flights/admin")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest());

                        verify(flightService, never()).createFlight(any());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void createFlight_shouldReturnBadRequest_whenNegativeSeats() throws Exception {
                        FlightRequest request = new FlightRequest(
                                        "SR001",
                                        -1,
                                        LocalDateTime.now().plusDays(1),
                                        LocalDateTime.now().plusDays(1).plusHours(2),
                                        299.99,
                                        1L,
                                        1L,
                                        true);

                        mockMvc.perform(post("/api/flights/admin")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest());

                        verify(flightService, never()).createFlight(any());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void createFlight_shouldReturnBadRequest_whenDepartureTimeInPast() throws Exception {
                        FlightRequest request = new FlightRequest(
                                        "SR001",
                                        150,
                                        LocalDateTime.now().minusDays(1),
                                        LocalDateTime.now().plusDays(1),
                                        299.99,
                                        1L,
                                        1L,
                                        true);

                        mockMvc.perform(post("/api/flights/admin")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest());

                        verify(flightService, never()).createFlight(any());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void createFlight_shouldReturnBadRequest_whenBusinessValidationFails() throws Exception {
                        FlightRequest request = createFlightRequest();

                        when(flightService.createFlight(any(FlightRequest.class)))
                                        .thenThrow(new BusinessException("Available seats exceed aircraft capacity"));

                        mockMvc.perform(post("/api/flights/admin")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest());

                        verify(flightService).createFlight(any(FlightRequest.class));
                }

                @Test
                @WithMockUser(roles = "USER")
                void createFlight_shouldReturnForbidden_whenNotAdmin() throws Exception {
                        FlightRequest request = createFlightRequest();

                        mockMvc.perform(post("/api/flights/admin")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).createFlight(any());
                }

                @Test
                void createFlight_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
                        FlightRequest request = createFlightRequest();

                        mockMvc.perform(post("/api/flights/admin")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).createFlight(any());
                }
        }

        @Nested
        class UpdateFlightTests {
                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlight_shouldReturnUpdatedFlight_whenValidRequest() throws Exception {
                        FlightUpdate update = createFlightUpdate();
                        FlightResponse response = createFlightResponse();

                        when(flightService.updateFlight(eq(1L), any(FlightUpdate.class))).thenReturn(response);

                        mockMvc.perform(put("/api/flights/admin/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(update)))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(1L))
                                        .andExpect(jsonPath("$.flightNumber").value("SR001"));

                        verify(flightService).updateFlight(eq(1L), any(FlightUpdate.class));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlight_shouldReturnUpdatedFlight_whenPartialUpdate() throws Exception {
                        FlightUpdate partialUpdate = new FlightUpdate(null, 100, null, null,
                                        null, null, null, null);

                        FlightResponse response = createFlightResponse();

                        when(flightService.updateFlight(eq(1L), any(FlightUpdate.class))).thenReturn(response);

                        mockMvc.perform(put("/api/flights/admin/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(partialUpdate)))
                                        .andExpect(status().isOk());

                        verify(flightService).updateFlight(eq(1L), any(FlightUpdate.class));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlight_shouldReturnNotFound_whenFlightDoesNotExist() throws Exception {
                        FlightUpdate update = createFlightUpdate();

                        when(flightService.updateFlight(eq(99L), any(FlightUpdate.class)))
                                        .thenThrow(new EntityNotFoundException("Flight with id: 99 not found"));

                        mockMvc.perform(put("/api/flights/admin/99")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(update)))
                                        .andExpect(status().isNotFound());

                        verify(flightService).updateFlight(eq(99L), any(FlightUpdate.class));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlight_shouldReturnBadRequest_whenInvalidFlightNumber() throws Exception {
                        FlightUpdate update = new FlightUpdate("AB", 150, null, null,
                                        null, null, null, null);

                        mockMvc.perform(put("/api/flights/admin/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(update)))
                                        .andExpect(status().isBadRequest());

                        verify(flightService, never()).updateFlight(anyLong(), any());
                }

                @Test
                @WithMockUser(roles = "USER")
                void updateFlight_shouldReturnForbidden_whenNotAdmin() throws Exception {
                        FlightUpdate update = createFlightUpdate();

                        mockMvc.perform(put("/api/flights/admin/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsBytes(update)))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).updateFlight(anyLong(), any());
                }
        }

        @Nested
        class DeleteFlightTests {
                @Test
                @WithMockUser(roles = "ADMIN")
                void deleteFlight_shouldReturnNoContent_whenFlightExists() throws Exception {
                        doNothing().when(flightService).deleteFlight(1L);

                        mockMvc.perform(delete("/api/flights/admin/1"))
                                        .andExpect(status().isNoContent());

                        verify(flightService).deleteFlight(1L);
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void deleteFlight_shouldReturnNotFound_whenFlightDoesNotExist() throws Exception {
                        doThrow(new EntityNotFoundException("Flight with id: 99 not found"))
                                        .when(flightService).deleteFlight(99L);

                        mockMvc.perform(delete("/api/flights/admin/99"))
                                        .andExpect(status().isNotFound());

                        verify(flightService).deleteFlight(99L);
                }

                @Test
                @WithMockUser(roles = "USER")
                void deleteFlight_shouldReturnForbidden_whenNotAdmin() throws Exception {
                        mockMvc.perform(delete("/api/flights/admin/1"))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).deleteFlight(anyLong());
                }

                @Test
                void deleteFlight_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
                        mockMvc.perform(delete("/api/flights/admin/1"))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).deleteFlight(anyLong());
                }
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

        @Nested
        class UpdateFlightStatusTests {
                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlightStatus_shouldReturnUpdatedFlight_whenValidRequest() throws Exception {
                        Long flightId = 1L;
                        FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(false);
                        FlightResponse updatedResponse = new FlightResponse(
                                        1L,
                                        "SR001",
                                        150,
                                        LocalDateTime.of(2025, 12, 1, 10, 0),
                                        LocalDateTime.of(2025, 12, 1, 12, 0),
                                        299.99,
                                        false,
                                        new AircraftResponse(1L, "B737", "Boeing 737", 180),
                                        new RouteResponse(
                                                        1L,
                                                        new AirportResponse(1L, "MAD", "Madrid",
                                                                        "http://example.com/mad.jpg"),
                                                        new AirportResponse(2L, "BCN", "Barcelona",
                                                                        "http://example.com/bcn.jpg")),
                                        LocalDateTime.now(),
                                        LocalDateTime.now());

                        when(flightService.updateFlightStatus(flightId, request)).thenReturn(updatedResponse);

                        mockMvc.perform(patch("/api/flights/admin/{id}/status", flightId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(1L))
                                        .andExpect(jsonPath("$.flightNumber").value("SR001"))
                                        .andExpect(jsonPath("$.available").value(false));

                        verify(flightService).updateFlightStatus(flightId, request);
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlightStatus_shouldReturnUpdatedFlight_whenSettingToAvailable() throws Exception {
                        Long flightId = 1L;
                        FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(true);
                        FlightResponse updatedResponse = new FlightResponse(
                                        1L,
                                        "SR001",
                                        150,
                                        LocalDateTime.of(2025, 12, 1, 10, 0),
                                        LocalDateTime.of(2025, 12, 1, 12, 0),
                                        299.99,
                                        true,
                                        new AircraftResponse(1L, "B737", "Boeing 737", 180),
                                        new RouteResponse(
                                                        1L,
                                                        new AirportResponse(1L, "MAD", "Madrid",
                                                                        "http://example.com/mad.jpg"),
                                                        new AirportResponse(2L, "BCN", "Barcelona",
                                                                        "http://example.com/bcn.jpg")),
                                        LocalDateTime.now(),
                                        LocalDateTime.now());

                        when(flightService.updateFlightStatus(flightId, request)).thenReturn(updatedResponse);

                        mockMvc.perform(patch("/api/flights/admin/{id}/status", flightId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(1L))
                                        .andExpect(jsonPath("$.flightNumber").value("SR001"))
                                        .andExpect(jsonPath("$.available").value(true));

                        verify(flightService).updateFlightStatus(flightId, request);
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlightStatus_shouldReturn404_whenFlightNotFound() throws Exception {
                        Long flightId = 999L;
                        FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(true);

                        when(flightService.updateFlightStatus(flightId, request))
                                        .thenThrow(new EntityNotFoundException("Flight with id: 999 not found"));

                        mockMvc.perform(patch("/api/flights/admin/{id}/status", flightId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isNotFound());

                        verify(flightService).updateFlightStatus(flightId, request);
                }

                @Test
                @WithMockUser(roles = "USER")
                void updateFlightStatus_shouldReturn403_whenUserNotAdmin() throws Exception {
                        Long flightId = 1L;
                        FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(true);

                        mockMvc.perform(patch("/api/flights/admin/{id}/status", flightId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).updateFlightStatus(any(), any());
                }

                @Test
                void updateFlightStatus_shouldReturn403_whenNotAuthenticated() throws Exception {
                        Long flightId = 1L;
                        FlightStatusUpdateRequest request = new FlightStatusUpdateRequest(true);

                        mockMvc.perform(patch("/api/flights/admin/{id}/status", flightId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isForbidden());

                        verify(flightService, never()).updateFlightStatus(any(), any());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void updateFlightStatus_shouldReturn400_whenInvalidRequest() throws Exception {
                        Long flightId = 1L;
                        String invalidJson = "{\"available\": null}";

                        mockMvc.perform(patch("/api/flights/admin/{id}/status", flightId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(invalidJson))
                                        .andExpect(status().isBadRequest());

                        verify(flightService, never()).updateFlightStatus(any(), any());
                }
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
                                true);
        }

        private FlightSimpleResponse createFlightSimpleResponse() {
                return new FlightSimpleResponse(
                                1L,
                                "SR001",
                                "Madrid",
                                "Barcelona",
                                LocalDateTime.of(2025, 12, 1, 10, 0),
                                LocalDateTime.of(2025, 12, 1, 12, 0),
                                299.99,
                                150);
        }

        private FlightResponse createFlightResponse() {
                return new FlightResponse(
                                1L,
                                "SR001",
                                150,
                                LocalDateTime.of(2025, 12, 1, 10, 0),
                                LocalDateTime.of(2025, 12, 1, 12, 0),
                                299.99,
                                true,
                                new AircraftResponse(1L, "B737", "Boeing 737", 180),
                                new RouteResponse(
                                                1L,
                                                new AirportResponse(1L, "MAD", "Madrid", "http://example.com/mad.jpg"),
                                                new AirportResponse(2L, "BCN", "Barcelona",
                                                                "http://example.com/bcn.jpg")),
                                LocalDateTime.now(),
                                LocalDateTime.now());
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
                                1L);
        }
}