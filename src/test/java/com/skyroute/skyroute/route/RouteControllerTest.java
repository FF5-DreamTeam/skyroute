package com.skyroute.skyroute.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;
import com.skyroute.skyroute.route.service.RouteService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidRouteException;
import org.springframework.http.MediaType;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class RouteControllerTest {
    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private RouteService routeService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    class CreateRouteTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void createRoute_shouldReturnCreated_whenValidRequest() throws Exception{
            RouteResponse response = createRouteResponse();
            when(routeService.createRoute(any())).thenReturn(response);

            RouteRequest request = new RouteRequest(1L, 2L);

            mockMvc.perform(post("/api/routes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.origin.code").value("MAD"))
                    .andExpect(jsonPath("$.destination.code").value("BCN"));

            verify(routeService).createRoute(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createRoute_shouldReturnBadRequest_whenOriginIdIsNull() throws Exception{
            RouteRequest request = new RouteRequest(null, 2L);

            mockMvc.perform(post("/api/routes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(routeService, never()).createRoute(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createRoute_shouldReturnBadRequest_whenDestinationIdIsNull() throws Exception{
            RouteRequest request = new RouteRequest(1L, null);

            mockMvc.perform(post("/api/routes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(routeService, never()).createRoute(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createRoute_shouldReturnBadRequest_whenOriginAndDestinationAreTheSame() throws Exception{
            when(routeService.createRoute(any()))
                    .thenThrow(new InvalidRouteException("Origin and destination airports cannot be the same"));
            RouteRequest request = new RouteRequest(1L, 1L);

            mockMvc.perform(post("/api/routes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(routeService).createRoute(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createRoute_shouldReturnConflict_whenRouteAlreadyExists() throws Exception{
            when(routeService.createRoute(any()))
                    .thenThrow(new EntityAlreadyExistsException("Route already exists between these airports"));

            RouteRequest request = new RouteRequest(1L, 2L);

            mockMvc.perform(post("/api/routes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(routeService).createRoute(any());
        }

        @Test
        @WithMockUser(roles = "USER")
        void createRoute_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception{
            RouteRequest request = new RouteRequest(1L, 2L);

            mockMvc.perform(post("/api/routes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(routeService, never()).createRoute(any());
        }

        @Test
        void createRoute_shouldReturnForbidden_whenNoAuthenticated() throws Exception{
            RouteRequest request = new RouteRequest(1L, 2L);

            mockMvc.perform(post("/api/routes")
                            .with(anonymous())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
            verify(routeService, never()).createRoute(any());
        }
    }

    @Nested
    class GetAllRoutesTests {
        @Test
        @WithMockUser
        void getAllRoutes_shouldReturnOk_whenRoutesExist() throws Exception {
            RouteResponse response = createRouteResponse();
            when(routeService.getAllRoutes()).thenReturn(List.of(response));

            mockMvc.perform(get("/api/routes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.[0].id").value(10L))
                    .andExpect(jsonPath("$.[0].origin.code").value("MAD"))
                    .andExpect(jsonPath("$.[0].destination.code").value("BCN"));

            verify(routeService).getAllRoutes();
        }

        @Test
        void getAllRoutes_shouldReturnEmptyList_whenNoRoutesExist() throws Exception {
            when(routeService.getAllRoutes()).thenReturn(List.of());

            mockMvc.perform(get("/api/routes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());

            verify(routeService).getAllRoutes();
        }
    }

    @Nested
    class GetRouteByIdTests {
        @Test
        @WithMockUser
        void getRouteById_shouldReturnOk_whenRouteExist() throws Exception {
            RouteResponse response = createRouteResponse();
            when(routeService.getRouteById(10L)).thenReturn(response);

            mockMvc.perform(get("/api/routes/{id}", 10L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.origin.code").value("MAD"))
                    .andExpect(jsonPath("$.destination.code").value("BCN"));

            verify(routeService).getRouteById(10L);
        }

        @Test
        void getRouteById_shouldReturnNotFound_whenRouteDoesNotExist() throws Exception {
            when(routeService.getRouteById(99L))
                    .thenThrow(new EntityNotFoundException("Airport not found with ID: 99"));

            mockMvc.perform(get("/api/routes/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(routeService).getRouteById(99L);
        }
    }

    @Nested
    class UpdateRouteTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void updateRoute_shouldReturnOk_whenValidRequest() throws Exception {
            RouteResponse response = createRouteResponse();
            when(routeService.updateRoute(eq(10L), any(RouteRequest.class))).thenReturn(response);

            RouteRequest request = new RouteRequest(1L, 2L);

            mockMvc.perform(put("/api/routes/{id}", 10L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.origin.code").value("MAD"))
                    .andExpect(jsonPath("$.destination.code").value("BCN"));
            verify(routeService).updateRoute(eq(10L), any(RouteRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateRoute_shouldReturnBadRequest_whenOriginAndDestinationAreSame() throws Exception {
            RouteRequest request = new RouteRequest(1L, 1L);
            when(routeService.updateRoute(eq(10L), any(RouteRequest.class)))
                    .thenThrow(new InvalidRouteException("Origin and destination airports cannot be the same"));

            mockMvc.perform(put("/api/routes/{id}", 10L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            verify(routeService).updateRoute(eq(10L), any(RouteRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateRoute_shouldReturnNotFound_whenRouteDoesNotExist() throws Exception{
            RouteRequest request = new RouteRequest(1L, 2L);
            when(routeService.updateRoute(eq(99L), any(RouteRequest.class)))
                    .thenThrow(new EntityNotFoundException("Route not found with ID: 99"));

            mockMvc.perform(put("/api/routes/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
            verify(routeService).updateRoute(eq(99L), any(RouteRequest.class));
        }

        @Test
        @WithMockUser(roles = "USER")
        void updateRoute_shouldReturnForbidden_whenNotAdmin() throws Exception {
            RouteRequest request = new RouteRequest(1L, 2L);

            mockMvc.perform(put("/api/routes/{id}", 10L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
            verify(routeService, never()).updateRoute(anyLong(), any(RouteRequest.class));
        }

        @Test
        void updateRoute_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            RouteRequest request = new RouteRequest(1L, 2L);

            mockMvc.perform(put("/api/routes/{id}", 10L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
            verify(routeService, never()).updateRoute(anyLong(), any(RouteRequest.class));
        }
    }

    @Nested
    class DeleteRouteTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteRoute_shouldReturnNoContent_whenRouteExists() throws Exception{
            doNothing().when(routeService).deleteRoute(10L);

            mockMvc.perform(delete("/api/routes/{id}", 10L))
                    .andExpect(status().isNoContent());

            verify(routeService).deleteRoute(10L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteRoute_shouldReturnNotFound_whenRouteDoesNotExist() throws Exception {
            doThrow(new EntityNotFoundException("Route not found with ID: 99"))
                    .when(routeService).deleteRoute(99L);

            mockMvc.perform(delete("/api/routes/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(routeService).deleteRoute(99L);
        }

        @Test
        @WithMockUser(roles = "USER")
        void deleteRoute_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
            mockMvc.perform(delete("/api/routes/{id}", 10L))
                    .andExpect(status().isForbidden());

            verify(routeService, never()).deleteRoute(anyLong());
        }

        @Test
        void deleteRoute_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/api/routes/{id}", 10L))
                    .andExpect(status().isForbidden());

            verify(routeService, never()).deleteRoute(anyLong());
        }
    }

    private RouteResponse createRouteResponse(){
        return new RouteResponse(
                10L,
                new AirportResponse(1L, "MAD", "Madrid", "http://example.com/mad.jpg"),
                new AirportResponse(2L, "BCN", "Barcelona", "http://example.com/bcn.jpg")
        );
    }
}