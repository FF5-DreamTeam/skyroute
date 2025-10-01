package com.skyroute.skyroute.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.service.BookingService;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
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
import java.time.LocalDateTime;
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

    //    @Nested
//    class CreateBookingTest {
//
//        @Test
//        @WithMockUser(roles = "USER")
//        void createBooking_shouldReturnBookingResponse_whenValidRequest() {
//            BookingRequest request = new BookingRequest(1L, 2, List.of("Pepa"), List.of(LocalDate.of(2020, 1, 1)));
//
//        }
//    }

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
