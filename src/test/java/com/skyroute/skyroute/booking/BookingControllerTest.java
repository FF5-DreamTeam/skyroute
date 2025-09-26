package com.skyroute.skyroute.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.service.BookingService;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.service.UserService;
import lombok.With;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setLastName("user1");

        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Nested
    class CreateBookingTest {

        @Test
        @WithMockUser(roles = "USER")
        void createBooking_shouldReturnBookingResponse_whenValidRequest() {
            BookingRequest request = new BookingRequest(1L, 2, List.of("pepa"), List.of(LocalDate.of(2020, 1, 1)));

        }

        private BookingRequest createBookingRequest() {
            return BookingRequest.builder()
                    .flightId(1L)
                    .bookedSeats(1)
                    .passengerNames(List.of("Pepa"))
                    .passengerBirthDates(List.of(LocalDate.of(2000, 1, 1)))
                    .build();
        }
    }
}
