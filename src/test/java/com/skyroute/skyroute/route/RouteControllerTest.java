package com.skyroute.skyroute.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;
import com.skyroute.skyroute.route.service.RouteService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

    private RouteResponse createRouteResponse(){
        return new RouteResponse(
                10L,
                new AirportResponse(1L, "MAD", "Madrid", "http://example.com/mad.jpg"),
                new AirportResponse(2L, "BCN", "Barcelona", "http://example.com/bcn.jpg")
        );
    }
}
