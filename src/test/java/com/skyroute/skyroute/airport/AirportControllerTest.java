package com.skyroute.skyroute.airport;

import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.service.AirportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class AirportControllerTest {
    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AirportService airportService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    class CreateAirportTest {
        private MockMultipartFile image;

        @BeforeEach
        void setUpImage(){
            image = new MockMultipartFile(
                    "image",
                    "airport.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "image content".getBytes()
            );
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createAirport_shouldReturnAirportResponse_whenValidRequest() throws Exception {
            AirportResponse response = createAirportResponse();
            when(airportService.createAirport(any())).thenReturn(response);

            mockMvc.perform(multipart("/api/airports")
                    .file(image)
                    .param("code", "MAD")
                    .param("city", "Madrid")
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.code").value("MAD"))
                    .andExpect(jsonPath("$.city").value("Madrid"))
                    .andExpect(jsonPath("$.imageUrl").exists());

            verify(airportService).createAirport(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createAirport_shouldReturnBadRequest_whenInvalidCode() throws Exception{

            mockMvc.perform(multipart("/api/airports")
                    .file(image)
                    .param("code", "MA")
                    .param("city", "Madrid")
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest());

            verify(airportService, never()).createAirport(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createAirport_shouldReturnBadRequest_whenInvalidCodePattern() throws Exception{

            mockMvc.perform(multipart("/api/airports")
                    .file(image)
                    .param("code", "ma1")
                    .param("city", "Madrid")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(status().isBadRequest());

            verify(airportService, never()).createAirport(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createAirport_shouldReturnBadRequest_whenInvalidCity() throws Exception{

            mockMvc.perform(multipart("/api/airports")
                            .file(image)
                            .param("code", "MAD")
                            .param("city", "M")
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest());

            verify(airportService, never()).createAirport(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createAirport_shouldReturnConflict_whenAirportCodeAlreadyExists() throws Exception{
            when(airportService.createAirport(any()))
                    .thenThrow(new EntityAlreadyExistsException("Airport code already exist: MAD"));

            mockMvc.perform(multipart("/api/airports")
                    .file(image)
                    .param("code", "MAD")
                    .param("city", "Madrid")
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isConflict());

            verify(airportService).createAirport(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createAirport_shouldReturnBadRequest_whenImageUploadFails() throws Exception{
            when(airportService.createAirport(any()))
                    .thenThrow(new ImageUploadException("Error uploading image"));

            mockMvc.perform(multipart("/api/airports")
                    .file(image)
                    .param("code", "MAD")
                    .param("city", "Madrid")
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest());

            verify(airportService).createAirport(any());
        }

        @Test
        @WithMockUser(roles = "USER")
        void createAirport_shouldReturnForbidden_whenNotAdmin() throws Exception{
            mockMvc.perform(multipart("/api/airports")
                    .file(image)
                    .param("code", "MAD")
                    .param("city", "Madrid")
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isForbidden());

            verify(airportService, never()).createAirport(any());
        }

        @Test
        void createAirport_shouldReturnForbidden_whenNotAuthenticated() throws Exception{
            mockMvc.perform(multipart("/api/airports")
                            .file(image)
                            .param("code", "MAD")
                            .param("city", "Madrid")
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isForbidden());

            verify(airportService, never()).createAirport(any());
        }
    }

    private AirportResponse createAirportResponse(){
        return new AirportResponse(
                1L,
                "MAD",
                "Madrid",
                "http://example.com/madrid.jpg"
        );
    }
}
