package com.skyroute.skyroute.booking.controller;

import com.skyroute.skyroute.booking.dto.BookingFilterRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.service.BookingFilterService;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import com.skyroute.skyroute.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.skyroute.skyroute.booking.enums.BookingStatus.CREATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class BookingFilterControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private BookingFilterService bookingFilterService;

    @MockitoBean
    private UserService userService;

    private MockMvc mockMvc;
    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        testUser = createTestUser(1L, Role.USER);
        testAdmin = createTestUser(2L, Role.ADMIN);
    }

    @Nested
    class FilterMyBookingsTests {

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldReturnFilteredBookings_whenNoFiltersProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].bookingNumber").value("SR-ABC123"));

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterByStatus_whenStatusProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("bookingStatus", "CREATED"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].bookingStatus").value("CREATED"));

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterByBookingNumber_whenBookingNumberProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("bookingNumber", "SR-ABC123"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].bookingNumber").value("SR-ABC123"));

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterByFlightDepartureDate_whenDateProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("flightDepartureDate", "2020-12-31"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterByMinPrice_whenMinPriceProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("minPrice", "100.0"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].totalPrice").value(399.99));

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterByOriginAirport_whenOriginProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("originAirport", "Madrid"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].originAirport").value("Madrid"));

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterByDestinationAirport_whenDestinationProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("destinationAirport", "Valencia"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].destinationAirport").value("Valencia"));

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterByPassengerName_whenPassengerNameProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("passengerName", "Pepa"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].passengerNames[0]").value("Pepa"));

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldFilterFutureFlights_whenFutureFlightsOnlyIsTrue() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("futureFlightsOnly", "true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldApplyMultipleFilters_whenMultipleFiltersProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("bookingStatus", "CREATED")
                    .param("originAirport", "Madrid")
                    .param("destinationAirport", "Valencia")
                    .param("minPrice", "100.0")
                    .param("futureFlightsOnly", "true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldApplyPagination_whenPaginationParamsProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("page", "1")
                    .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldApplySorting_whenSortParamsProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("sortBy", "totalPrice")
                    .param("sortDirection", "ASC"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterMyBookings_shouldReturnEmptyPage_whenNoBookingsMatchFilters() throws Exception {
            Page<BookingResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testUser)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/bookings/filter/my-bookings")
                    .param("bookingStatus", "CANCELLED"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testUser));
        }

        @Test
        void filterMyBookings_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/bookings/filter/my-bookings"))
                    .andExpect(status().isForbidden());

            verify(bookingFilterService, never()).filterBookings(any(), any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterMyBookings_shouldReturnForbidden_whenAdminTriesToAccessUserEndpoint() throws Exception {
            mockMvc.perform(get("/api/bookings/filter/my-bookings"))
                    .andExpect(status().isForbidden());

            verify(bookingFilterService, never()).filterBookings(any(), any(), any());
        }
    }

    @Nested
    class FilterAdminBookingsTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldReturnFilteredBookings_whenNoFiltersProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].bookingNumber").value("SR-ABC123"));

            verify(userService).getCurrentUser();
            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterByUserId_whenUserIdProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("userId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterByUserEmail_whenUserEmailProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("userEmail", "test@email.com"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterByUserName_whenUserNameProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("userName", "Test User"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterByFlightId_whenFlightIdProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("flightId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].flightId").value(1));

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterByFlightNumber_whenFlightNumberProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("flightNumber", "SK123"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].flightNumber").value("SK123"));

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterByMaxPrice_whenMaxPriceProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("maxPrice", "500.0"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].totalPrice").value(399.99));

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterByPriceRange_whenMinAndMaxPriceProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("minPrice", "100.0")
                    .param("maxPrice", "500.0"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].totalPrice").value(399.99));

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterActiveBookings_whenActiveOnlyIsTrue() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("activeOnly", "true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldFilterPendingBookings_whenPendingOnlyIsTrue() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("pendingOnly", "true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldApplyAllFilters_whenAllFiltersProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("bookingStatus", "CREATED")
                    .param("userId", "1")
                    .param("userEmail", "test@email.com")
                    .param("flightId", "1")
                    .param("flightNumber", "SK123")
                    .param("minPrice", "100.0")
                    .param("maxPrice", "500.0")
                    .param("originAirport", "Madrid")
                    .param("destinationAirport", "Valencia")
                    .param("activeOnly", "true")
                    .param("futureFlightsOnly", "true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].bookingNumber").value("SR-ABC123"));

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "USER")
        void filterAdminBookings_shouldReturnForbidden_whenUserRole() throws Exception {
            mockMvc.perform(get("/api/bookings/filter/admin"))
                    .andExpect(status().isForbidden());

            verify(bookingFilterService, never()).filterBookings(any(), any(), any());
        }

        @Test
        void filterAdminBookings_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/bookings/filter/admin"))
                    .andExpect(status().isForbidden());

            verify(bookingFilterService, never()).filterBookings(any(), any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldReturnEmptyPage_whenNoBookingsMatchFilters() throws Exception {
            Page<BookingResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("userId", "999"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void filterAdminBookings_shouldApplyPaginationAndSorting_whenParamsProvided() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(List.of(createBookingResponse()), PageRequest.of(2, 10),
                    100);
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            when(bookingFilterService.filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin)))
                    .thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/filter/admin")
                    .param("page", "2")
                    .param("size", "10")
                    .param("sortBy", "totalPrice")
                    .param("sortDirection", "DESC"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            verify(bookingFilterService).filterBookings(any(BookingFilterRequest.class), any(), eq(testAdmin));
        }
    }

    private User createTestUser(Long id, Role role) {
        return User.builder()
                .id(id)
                .firstName("Test")
                .lastName("User")
                .email("test@email.com")
                .password("encodedPassword")
                .phoneNumber("+123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(role)
                .build();
    }

    private BookingResponse createBookingResponse() {
        return new BookingResponse(
                1L, "SR-ABC123", CREATED, 1L, "SK123", "Madrid", "Valencia", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2), List.of("Pepa", "Maria"),
                List.of("1990, 1, 1", "1991, 1, 1"), 2, 399.99, LocalDateTime.now(), LocalDateTime.now());
    }
}
