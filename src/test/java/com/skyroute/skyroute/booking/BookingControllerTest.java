package com.skyroute.skyroute.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.service.BookingService;
import com.skyroute.skyroute.shared.exception.custom_exception.BookingAccessDeniedException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidBookingOperationException;
import com.skyroute.skyroute.shared.exception.custom_exception.NotEnoughSeatsException;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class BookingControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        testUser = createTestUser(1L, Role.USER);
        testAdmin = createTestUser(2L, Role.ADMIN);
    }

    @Nested
    class GetAllBookingTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllBookingsAdmin_shouldReturnPaginatedBookings_whenValidRequest() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(
                    List.of(createBookingResponse()),
                    PageRequest.of(0, 10),
                    1);
            when(bookingService.getAllBookingsAdmin(0, 10, "createdAt", "DESC")).thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "createdAt")
                            .param("sortDirection", "DESC"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].bookingNumber").value("SR-ABC123"));

            verify(bookingService).getAllBookingsAdmin(0, 10, "createdAt", "DESC");
        }

        @Test
        @WithMockUser(roles = "USER")
        void getAllBookingsAdmin_shouldReturnForbidden_whenNotAdmin() throws Exception {
            mockMvc.perform(get("/api/bookings"))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).getAllBookingsAdmin(anyInt(), anyInt(), anyString(), anyString());
        }

        @Test
        void getAllBookingsAdmin_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/bookings"))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).getAllBookingsAdmin(anyInt(), anyInt(), anyString(), anyString());
        }
    }

    @Nested
    class GetUserBookingsTests {

        @Test
        @WithMockUser(roles = "USER")
        void getAllBookingsUser_shouldReturnPaginatedBookings_whenAuthenticatedUser() throws Exception {
            Page<BookingResponse> bookingPage = new PageImpl<>(
                    List.of(createBookingResponse()),
                    PageRequest.of(0, 10),
                    1);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.getAllBookingsUser(testUser, 0, 10, "createdAt", "DESC")).thenReturn(bookingPage);

            mockMvc.perform(get("/api/bookings/user")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "createdAt")
                            .param("sortDirection", "DESC"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].bookingNumber").value("SR-ABC123"));

            verify(userService).getCurrentUser();
            verify(bookingService).getAllBookingsUser(testUser, 0, 10, "createdAt", "DESC");
        }

        @Test
        void getAllBookingsUser_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/bookings"))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).getAllBookingsAdmin(anyInt(), anyInt(), anyString(), anyString());
        }
    }

    @Nested
    class GetBookingByIdTests {

        @Test
        @WithMockUser(roles = "USER")
        void getBookingById_shouldReturnBooking_whenBookingExists() throws Exception {
            BookingResponse response = createBookingResponse();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.getBookingById(1L, testUser)).thenReturn(response);

            mockMvc.perform(get("/api/bookings/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bookingId").value(1L))
                    .andExpect(jsonPath("$.bookingNumber").value("SR-ABC123"));

            verify(bookingService).getBookingById(1L, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void getBookingById_shouldReturnNotFound_whenBookingDoesNotExist() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.getBookingById(99L, testUser)).thenThrow(new EntityNotFoundException("Booking not found"));

            mockMvc.perform(get("/api/bookings/99"))
                    .andExpect(status().isNotFound());
            
            verify(bookingService).getBookingById(99L, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void getBookingById_shouldReturnForbidden_whenUserDoesNotOwnBooking() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.getBookingById(1L, testUser)).thenThrow(new BookingAccessDeniedException("User cannot access this booking"));

            mockMvc.perform(get("/api/bookings/1"))
                    .andExpect(status().isForbidden());

            verify(bookingService).getBookingById(1L, testUser);
        }
    }

    @Nested
    class CreateBookingTest {

        @Test
        @WithMockUser(roles = "USER")
        void createBooking_shouldReturnCreatedBooking_whenValidRequest() throws Exception{
            BookingRequest request = createBookingRequest();
            BookingResponse response = createBookingResponse();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.createBooking(any(BookingRequest.class), eq(testUser))).thenReturn(response);

            mockMvc.perform(post("/api/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bookingNumber").value("SR-ABC123"))
                    .andExpect(jsonPath("$.bookedSeats").value(2));

            verify(bookingService).createBooking(any(BookingRequest.class), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void createBooking_shouldReturnBadRequest_whenInvalidFlightId() throws Exception {
            BookingRequest request = new BookingRequest(
                    -1L,
                    2,
                    List.of("Pepe", "Maria"),
                    List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1))
            );

            mockMvc.perform(post("/api/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(bookingService, never()).createBooking(any(), any());
        }

        @Test
        @WithMockUser(roles = "USER")
        void createBooking_shouldReturnBadRequest_whenPassengerNamesTooShort() throws Exception {
            BookingRequest request = new BookingRequest(
                    1L,
                    2,
                    List.of("P", "M"),
                    List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1))
            );

            mockMvc.perform(post("/api/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(bookingService, never()).createBooking(any(), any());
        }

        @Test
        @WithMockUser(roles = "USER")
        void createBooking_shouldReturnBadRequest_whenBirthDateInFuture() throws Exception {
            BookingRequest request = new BookingRequest(
                    1L,
                    2,
                    List.of("Pepe", "Maria"),
                    List.of(LocalDate.now().plusDays(1), LocalDate.of(1991, 1, 1))
            );

            mockMvc.perform(post("/api/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(bookingService, never()).createBooking(any(), any());
        }

        @Test
        @WithMockUser(roles = "USER")
        void createBooking_shouldReturnNotFound_whenFlightNotFound() throws Exception {
            BookingRequest request = createBookingRequest();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.createBooking(any(BookingRequest.class), eq(testUser))).thenThrow(new EntityNotFoundException("Flight not found"));

            mockMvc.perform(post("/api/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(bookingService).createBooking(any(BookingRequest.class), eq(testUser));
        }

        @Test
        @WithMockUser(roles = "USER")
        void createBooking_shouldReturnConflict_whenNotEnoughSeats() throws Exception {
            BookingRequest request = createBookingRequest();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.createBooking(any(BookingRequest.class), eq(testUser))).thenThrow(new NotEnoughSeatsException("Not enough seats available. Requested: 2. Available: 1"));

            mockMvc.perform(post("/api/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(bookingService).createBooking(any(BookingRequest.class), eq(testUser));
        }

        @Test
        void createBooking_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            BookingRequest request = createBookingRequest();

            mockMvc.perform(post("/api/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).createBooking(any(), any());
        }
    }

    @Nested
    class UpdateBookingStatusTests {

        @Test
        @WithMockUser(roles = "USER")
        void updateBookingStatus_shouldUpdateStatus_whenValidTransition() throws Exception {
            BookingResponse response = createBookingResponse();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.updateBookingStatus(1L, BookingStatus.CANCELLED, testUser)).thenReturn(response);

            mockMvc.perform(put("/api/bookings/1/status")
                    .param("status", "CANCELLED"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bookingNumber").value("SR-ABC123"));

            verify(bookingService).updateBookingStatus(1L, BookingStatus.CANCELLED, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void updateBookingStatus_shouldReturnBadRequest_whenInvalidTransition() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED, testUser)).thenThrow(new InvalidBookingOperationException("A CONFIRMED booking can only be CANCELLED"));

            mockMvc.perform(put("/api/bookings/1/status")
                            .param("status", "CONFIRMED"))
                    .andExpect(status().isBadRequest());

            verify(bookingService).updateBookingStatus(1L, BookingStatus.CONFIRMED, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void updateBookingStatus_shouldReturnNotFound_whenBookingDoesNotExist() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.updateBookingStatus(99L, BookingStatus.CANCELLED, testUser)).thenThrow(new EntityNotFoundException("Booking not found"));

            mockMvc.perform(put("/api/bookings/99/status")
                            .param("status", "CANCELLED"))
                    .andExpect(status().isNotFound());

            verify(bookingService).updateBookingStatus(99L, BookingStatus.CANCELLED, testUser);
        }

        @Test
        void updateBookingStatus_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(put("/api/bookings/1/status")
                            .param("status", "CANCELLED"))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).updateBookingStatus(anyLong(), any(), any());
        }
    }

    @Nested
    class ConfirmBookingTests {

        @Test
        @WithMockUser(roles = "USER")
        void confirmBooking_shouldConfirmBooking_whenValidRequest() throws Exception {
            BookingResponse response = createBookingResponse();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.confirmBooking(1L, testUser)).thenReturn(response);

            mockMvc.perform(post("/api/bookings/1/confirm"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bookingNumber").value("SR-ABC123"));

            verify(bookingService).confirmBooking(1L, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void confirmBooking_shouldReturnNotFound_whenBookingDoesNotExist() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.confirmBooking(99L, testUser))
                    .thenThrow(new EntityNotFoundException("Booking not found"));

            mockMvc.perform(post("/api/bookings/99/confirm"))
                    .andExpect(status().isNotFound());

            verify(bookingService).confirmBooking(99L, testUser);
        }

        @Test
        void confirmBooking_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/api/bookings/1/confirm"))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).confirmBooking(anyLong(), any());
        }
    }

    @Nested
    class CancelBookingTests {

        @Test
        @WithMockUser(roles = "USER")
        void cancelBooking_shouldCancelBooking_whenValidRequest() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            doNothing().when(bookingService).cancelBooking(1L, testUser);

            mockMvc.perform(post("/api/bookings/1/cancel"))
                    .andExpect(status().isNoContent());

            verify(bookingService).cancelBooking(1L, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void cancelBooking_shouldReturnNotFound_whenBookingDoesNotExist() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            doThrow(new EntityNotFoundException("Booking not found")).when(bookingService).cancelBooking(99L, testUser);

            mockMvc.perform(post("/api/bookings/99/cancel"))
                    .andExpect(status().isNotFound());

            verify(bookingService).cancelBooking(99L, testUser);
        }

        @Test
        void cancelBooking_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/api/bookings/1/cancel"))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).cancelBooking(anyLong(), any());
        }
    }

    @Nested
    class UpdatePassengerNamesTests {

        @Test
        @WithMockUser(roles = "USER")
        void updatePassengerNames_shouldUpdateNames_whenValidRequest() throws Exception {
            List<String> newNames = List.of("Lola", "Juan");
            BookingResponse response = createBookingResponse();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.updatePassengerNames(1L, newNames, testUser)).thenReturn(response);

            mockMvc.perform(put("/api/bookings/1/passenger-names")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newNames)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bookingNumber").value("SR-ABC123"));

            verify(bookingService).updatePassengerNames(1L, newNames, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void updatePassengerNames_shouldReturnForbidden_whenBookingConfirmed() throws Exception {
            List<String> newNames = List.of("Lola", "Juan");
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.updatePassengerNames(1L, newNames, testUser)).thenThrow(new BookingAccessDeniedException("Cannot modify passenger birth dates after booking is CONFORMED or CANCELLED"));

            mockMvc.perform(put("/api/bookings/1/passenger-names")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newNames)))
                    .andExpect(status().isForbidden());

            verify(bookingService).updatePassengerNames(1L, newNames, testUser);
        }

        @Test
        void updatePassengerNames_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            List<String> newNames = List.of("Lola", "Juan");

            mockMvc.perform(put("/api/bookings/1/passenger-names")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newNames)))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).updatePassengerNames(anyLong(), anyList(), any());
        }
    }

    @Nested
    class UpdatePassengerBirthDatesTests {
        @Test
        @WithMockUser(roles = "USER")
        void updatePassengerBirthDates_shouldUpdateBirthDates_whenValidRequest() throws Exception {
            List<LocalDate> newDates = List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1992, 1, 1));
            BookingResponse response = createBookingResponse();
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.updatePassengerBirthDates(1L, newDates, testUser)).thenReturn(response);

            mockMvc.perform(put("/api/bookings/1/passenger-birth-dates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newDates)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bookingNumber").value("SR-ABC123"));

            verify(bookingService).updatePassengerBirthDates(1L, newDates, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void updatePassengerBirthDates_shouldReturnForbidden_whenBookingConfirmed() throws Exception {
            List<LocalDate> newDates = List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1992, 1, 1));
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingService.updatePassengerBirthDates(1L, newDates, testUser)).thenThrow(new BookingAccessDeniedException("Cannot modify passenger birth dates after booking is CONFORMED or CANCELLED"));

            mockMvc.perform(put("/api/bookings/1/passenger-birth-dates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newDates)))
                    .andExpect(status().isForbidden());

            verify(bookingService).updatePassengerBirthDates(1L, newDates, testUser);
        }

        @Test
        void updatePassengerBirthDates_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            List<LocalDate> newDates = List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1992, 1, 1));

            mockMvc.perform(put("/api/bookings/1/passenger-birth-dates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newDates)))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).updatePassengerBirthDates(anyLong(), anyList(), any());
        }
    }

    @Nested
    class DeleteBookingTests {

        @Test
        @WithMockUser(roles = "USER")
        void deleteBooking_shouldDeleteBooking_whenValidRequest() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            doNothing().when(bookingService).deleteBooking(1L, testUser);

            mockMvc.perform(delete("/api/bookings/1"))
                    .andExpect(status().isNoContent());

            verify(bookingService).deleteBooking(1L, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void deleteBooking_shouldReturnNotFound_whenBookingDoesNotExist() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            doThrow(new EntityNotFoundException("Booking not found"))
                    .when(bookingService).deleteBooking(99L, testUser);

            mockMvc.perform(delete("/api/bookings/99"))
                    .andExpect(status().isNotFound());

            verify(bookingService).deleteBooking(99L, testUser);
        }

        @Test
        @WithMockUser(roles = "USER")
        void deleteBooking_shouldReturnForbidden_whenUserTriesToDeleteConfirmedBooking() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testUser);
            doThrow(new BookingAccessDeniedException("Users can only delete bookings in CREATED status"))
                    .when(bookingService).deleteBooking(1L, testUser);

            mockMvc.perform(delete("/api/bookings/1"))
                    .andExpect(status().isForbidden());

            verify(bookingService).deleteBooking(1L, testUser);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteBooking_shouldAllowAdmin_toDeleteAnyBooking() throws Exception {
            when(userService.getCurrentUser()).thenReturn(testAdmin);
            doNothing().when(bookingService).deleteBooking(1L, testAdmin);

            mockMvc.perform(delete("/api/bookings/1"))
                    .andExpect(status().isNoContent());

            verify(bookingService).deleteBooking(1L, testAdmin);
        }

        @Test
        void deleteBooking_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/api/bookings/1"))
                    .andExpect(status().isForbidden());

            verify(bookingService, never()).deleteBooking(anyLong(), any());
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
                .birthDate(LocalDate.of(1990,1, 1))
                .role(role)
                .build();
    }

    private BookingRequest createBookingRequest() {
        return new BookingRequest(
                1L, 2, List.of("Pepe", "Maria"), List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1))
        );
    }

    private BookingResponse createBookingResponse() {
       return new BookingResponse(
            1L, "SR-ABC123", BookingStatus.CREATED, 1L, "SK123", "Madrid", "Valencia", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), List.of("Pepe", "Maria"), List.of("1990, 1, 1", "1991, 1, 1"), 2, 399.99, LocalDateTime.now(), LocalDateTime.now()
       );
   }
}