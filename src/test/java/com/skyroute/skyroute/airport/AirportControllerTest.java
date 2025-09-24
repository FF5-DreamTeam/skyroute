package com.skyroute.skyroute.airport;

import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidUpdateRequestException;
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

import java.util.List;

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

    @Nested
    class GetAllAirportsTests {
        @Test
        void getAllAirports_shouldReturnAirportsList_whenAirportsExist() throws Exception{
            List<AirportResponse> airports = List.of(
                    createAirportResponse(),
                    new AirportResponse(2L, "BCN", "Barcelona", "http://example.com/bcn.jpg")
            );

            when(airportService.getAllAirports()).thenReturn(airports);

            mockMvc.perform(get("/api/airports"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].code").value("MAD"))
                    .andExpect(jsonPath("$[1].code").value("BCN"));

            verify(airportService).getAllAirports();
        }

        @Test
        void getAllAirports_shouldReturnEmptyList_whenNoAirportsExist() throws Exception{
            when(airportService.getAllAirports()).thenReturn(List.of());

            mockMvc.perform(get("/api/airports"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(airportService).getAllAirports();
        }

        @Test
        void getAllAirports_shouldBeAccessible_withoutAuthentication() throws Exception{
            when(airportService.getAllAirports()).thenReturn(List.of());

            mockMvc.perform(get("/api/airports"))
                    .andExpect(status().isOk());

            verify(airportService).getAllAirports();
        }
    }

    @Nested
    class GetAirportByIdTests {
        @Test
        void getAirportById_shouldReturnAirport_whenAirportExists() throws Exception{
            AirportResponse response = createAirportResponse();
            when(airportService.getAirportById(1L)).thenReturn(response);

            mockMvc.perform(get("/api/airports/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.code").value("MAD"))
                    .andExpect(jsonPath("$.city").value("Madrid"));

            verify(airportService).getAirportById(1L);
        }

        @Test
        void getAirportById_shouldReturnNotFound_whenAirportDoesNotExist() throws Exception{
            when(airportService.getAirportById(99L))
                    .thenThrow(new EntityNotFoundException("Airport not found with ID: 99"));

            mockMvc.perform(get("/api/airports/99"))
                    .andExpect(status().isNotFound());

            verify(airportService).getAirportById(99L);
        }

        @Test
        void getAirportById_shouldBeAccesible_withoutAuthentication() throws Exception{
            AirportResponse response = createAirportResponse();
            when(airportService.getAirportById(1L)).thenReturn(response);

            mockMvc.perform(get("/api/airports/1"))
                    .andExpect(status().isOk());

            verify(airportService).getAirportById(1L);
        }
    }

    @Nested
    class UpdateAirportTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void updateAirport_shouldReturnUpdatedAirport_whenValidRequest() throws Exception{
            MockMultipartFile image = new MockMultipartFile(
                    "image",
                    "updated.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "updated image".getBytes()
            );

            AirportResponse response = new AirportResponse(
                    1L,
                    "MAD",
                    "Madrid Updated",
                    "http://example/updated.jpg"
            );
            when(airportService.updateAirport(eq(1L), any())).thenReturn(response);

            mockMvc.perform(multipart("/api/airports/1")
                    .file(image)
                    .param("code", "MAD")
                    .param("city", "Madrid Updated")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.city").value("Madrid Updated"));

            verify(airportService).updateAirport(eq(1L), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateAirport_shouldReturnUpdatedAirport_whenPartialUpdate() throws Exception{
            AirportResponse response = new AirportResponse(
                    1L,
                    "MAD",
                    "Madrid Updated",
                    "http://example/old.jpg"
            );
            when(airportService.updateAirport(eq(1L), any())).thenReturn(response);

            mockMvc.perform(multipart("/api/airports/1")
                    .param("city", "Madrid Updated")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.city").value("Madrid Updated"));

            verify(airportService).updateAirport(eq(1L), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateAirport_shouldReturnNotFound_whenAirportDoesNotExist() throws Exception{
            when(airportService.updateAirport(eq(99L), any()))
                    .thenThrow(new EntityNotFoundException("Airport not found with ID: 99"));

            mockMvc.perform(multipart("/api/airports/99")
                    .param("city", "Updated City")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }))
                    .andExpect(status().isNotFound());

            verify(airportService).updateAirport(eq(99L), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateAirport_shouldReturnBadRequest_whenInvalidUpdateRequest() throws  Exception{
            when(airportService.updateAirport(eq(1L), any()))
                    .thenThrow(new InvalidUpdateRequestException("At least one field must be provided for update"));

            mockMvc.perform(multipart("/api/airports/1")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isBadRequest());
            verify(airportService).updateAirport(eq(1L), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateAirport_shouldReturnBadRequest_whenInvalidCodeValidation() throws  Exception{
            MockMultipartFile emptyImage = new MockMultipartFile(
                    "image",
                    "empty.jpg",
                    "image/jpeg",
                    new byte[0]
            );

            mockMvc.perform(multipart("/api/airports/1")
                            .file(emptyImage)
                            .param("code", "ma")
                            .param("city", "Madrid")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isBadRequest());
            verify(airportService, never()).updateAirport(anyLong(), any());
        }

        @Test
        @WithMockUser(roles = "USER")
        void updateAirport_shouldReturnForbidden_whenNotAdmin() throws  Exception{

            mockMvc.perform(multipart("/api/airports/1")
                            .param("city", "Updated City")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isForbidden());
            verify(airportService, never()).updateAirport(anyLong(), any());
        }
    }

    @Nested
    class DeleteAirportTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteAirport_shouldReturnNoContent_whenAirportExists() throws Exception {
            doNothing().when(airportService).deleteAirport(1L);

            mockMvc.perform(delete("/api/airports/1"))
                    .andExpect(status().isNoContent());

            verify(airportService).deleteAirport(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteAirport_shouldReturnNotFound_whenAirportDoesNotExist() throws Exception {
            doThrow(new EntityNotFoundException("Airport not found with ID: 99"))
                    .when(airportService).deleteAirport(99L);

            mockMvc.perform(delete("/api/airports/99"))
                    .andExpect(status().isNotFound());

            verify(airportService).deleteAirport(99L);
        }

        @Test
        @WithMockUser(roles = "USER")
        void deleteAirport_shouldReturnForbidden_whenNotAdmin() throws Exception {
            mockMvc.perform(delete("/api/airports/1"))
                    .andExpect(status().isForbidden());

            verify(airportService, never()).deleteAirport(anyLong());
        }

        @Test
        void deleteAirport_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/api/airports/1"))
                    .andExpect(status().isForbidden());

            verify(airportService, never()).deleteAirport(anyLong());
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
