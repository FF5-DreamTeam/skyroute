package com.skyroute.skyroute.aircraft;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyroute.skyroute.aircraft.controller.AircraftController;
import com.skyroute.skyroute.aircraft.dto.AircraftRequest;
import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftDeletionException;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AircraftController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {AircraftController.class, AircraftControllerTest.TestConfig.class})
@ActiveProfiles("test")
@DisplayName("Aircraft Controller Tests")
class AircraftControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        public AircraftService aircraftService() {
            return mock(AircraftService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AircraftService aircraftService;

    private AircraftRequest validAircraftRequest;
    private AircraftResponse aircraftResponse;
    private AircraftResponse aircraftResponse2;

    @BeforeEach
    void setUp() {
        reset(aircraftService);

        validAircraftRequest = new AircraftRequest("Boeing 737", "Boeing", 180);

        aircraftResponse = new AircraftResponse(1L, "Boeing 737", "Boeing", 180);
        aircraftResponse2 = new AircraftResponse(2L, "Airbus A320", "Airbus", 150);
    }

    @Test
    @DisplayName("Should create aircraft successfully with valid request")
    void createAircraft_ShouldReturnCreated_WhenValidRequest() throws Exception {
        when(aircraftService.createAircraft(any(AircraftRequest.class))).thenReturn(aircraftResponse);

        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAircraftRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.model").value("Boeing 737"))
                .andExpect(jsonPath("$.manufacturer").value("Boeing"))
                .andExpect(jsonPath("$.capacity").value(180));

        verify(aircraftService, times(1)).createAircraft(any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when model is blank")
    void createAircraft_ShouldReturnBadRequest_WhenModelIsBlank() throws Exception {
        AircraftRequest invalidRequest = new AircraftRequest("", "Boeing", 180);

        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).createAircraft(any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when manufacturer is blank")
    void createAircraft_ShouldReturnBadRequest_WhenManufacturerIsBlank() throws Exception {
        AircraftRequest invalidRequest = new AircraftRequest("Boeing 737", "", 180);

        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).createAircraft(any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when capacity is null")
    void createAircraft_ShouldReturnBadRequest_WhenCapacityIsNull() throws Exception {
        AircraftRequest invalidRequest = new AircraftRequest("Boeing 737", "Boeing", null);

        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).createAircraft(any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when capacity is less than 1")
    void createAircraft_ShouldReturnBadRequest_WhenCapacityIsLessThanOne() throws Exception {
        AircraftRequest invalidRequest = new AircraftRequest("Boeing 737", "Boeing", 0);

        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).createAircraft(any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when request body is missing")
    void createAircraft_ShouldReturnBadRequest_WhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).createAircraft(any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should return aircraft when aircraft exists")
    void getAircraft_ShouldReturnAircraft_WhenAircraftExists() throws Exception {
        when(aircraftService.getAircraftById(1L)).thenReturn(aircraftResponse);

        mockMvc.perform(get("/api/aircrafts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.model").value("Boeing 737"))
                .andExpect(jsonPath("$.manufacturer").value("Boeing"))
                .andExpect(jsonPath("$.capacity").value(180));

        verify(aircraftService, times(1)).getAircraftById(1L);
    }


    @Test
    @DisplayName("Should return 400 when id is invalid")
    void getAircraft_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/aircrafts/invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).getAircraftById(anyLong());
    }


    @Test
    @DisplayName("Should return list of aircrafts when aircrafts exist")
    void getAllAircrafts_ShouldReturnListOfAircrafts_WhenAircraftsExist() throws Exception {
        List<AircraftResponse> aircrafts = Arrays.asList(aircraftResponse, aircraftResponse2);
        when(aircraftService.getAllAircrafts()).thenReturn(aircrafts);

        mockMvc.perform(get("/api/aircrafts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].model").value("Boeing 737"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].model").value("Airbus A320"));

        verify(aircraftService, times(1)).getAllAircrafts();
    }

    @Test
    @DisplayName("Should return empty list when no aircrafts exist")
    void getAllAircrafts_ShouldReturnEmptyList_WhenNoAircraftsExist() throws Exception {
        when(aircraftService.getAllAircrafts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/aircrafts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(aircraftService, times(1)).getAllAircrafts();
    }

    @Test
    @DisplayName("Should update aircraft successfully with valid request")
    void updateAircraft_ShouldReturnUpdatedAircraft_WhenValidRequest() throws Exception {
        AircraftRequest updateRequest = new AircraftRequest("Boeing 737-800", "Boeing", 189);
        AircraftResponse updatedResponse = new AircraftResponse(1L, "Boeing 737-800", "Boeing", 189);

        when(aircraftService.updateAircraft(eq(1L), any(AircraftRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/aircrafts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.model").value("Boeing 737-800"))
                .andExpect(jsonPath("$.capacity").value(189));

        verify(aircraftService, times(1)).updateAircraft(eq(1L), any(AircraftRequest.class));
    }



    @Test
    @DisplayName("Should return 400 when update request has invalid data")
    void updateAircraft_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        AircraftRequest invalidRequest = new AircraftRequest("", "", -1);

        mockMvc.perform(put("/api/aircrafts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).updateAircraft(anyLong(), any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when update request body is missing")
    void updateAircraft_ShouldReturnBadRequest_WhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(put("/api/aircrafts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).updateAircraft(anyLong(), any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should delete aircraft successfully when aircraft exists and has no flights")
    void deleteAircraft_ShouldReturnNoContent_WhenSuccessful() throws Exception {
        doNothing().when(aircraftService).deleteAircraft(1L);

        mockMvc.perform(delete("/api/aircrafts/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(aircraftService, times(1)).deleteAircraft(1L);
    }


    @Test
    @DisplayName("Should return 400 when delete id is invalid")
    void deleteAircraft_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/aircrafts/invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).deleteAircraft(anyLong());
    }


    @Test
    @DisplayName("Should handle very large capacity value")
    void createAircraft_ShouldHandleLargeCapacity_WhenValid() throws Exception {
        AircraftRequest largeCapacityRequest = new AircraftRequest("Airbus A380", "Airbus", 853);
        AircraftResponse largeCapacityResponse = new AircraftResponse(3L, "Airbus A380", "Airbus", 853);

        when(aircraftService.createAircraft(any(AircraftRequest.class))).thenReturn(largeCapacityResponse);

        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(largeCapacityRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(853));

        verify(aircraftService, times(1)).createAircraft(any(AircraftRequest.class));
    }

    @Test
    @DisplayName("Should handle special characters in model name")
    void createAircraft_ShouldHandleSpecialCharacters_InModelName() throws Exception {
        AircraftRequest specialCharRequest = new AircraftRequest("Boeing 737-800 MAX", "Boeing", 180);
        AircraftResponse specialCharResponse = new AircraftResponse(4L, "Boeing 737-800 MAX", "Boeing", 180);

        when(aircraftService.createAircraft(any(AircraftRequest.class))).thenReturn(specialCharResponse);

        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specialCharRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Boeing 737-800 MAX"));

        verify(aircraftService, times(1)).createAircraft(any(AircraftRequest.class));
    }
}