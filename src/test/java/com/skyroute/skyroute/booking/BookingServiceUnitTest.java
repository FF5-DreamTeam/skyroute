package com.skyroute.skyroute.booking;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.booking.service.BookingServiceImpl;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    private User testUser;
    private Booking testBooking;
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testBooking= createTestBooking();
    }

    @Nested
    class GetBookingsTests {

        @Test
        void gelAllBookingsAdmin_shouldReturnPaginatedBookings_whenValidRequest() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));
            Page<Booking> bookingPage = new PageImpl<>(List.of(testBooking), pageable, 1);

            when(bookingRepository.findAll(any(Pageable.class))).thenReturn(bookingPage);

            Page<BookingResponse> result = bookingServiceImpl.getAllBookingsAdmin(0, 10, "createdAt", "ASC");

            assertEquals(1, result.getTotalElements());
            assertEquals(testBooking.getBookingNumber(), result.getContent().getFirst().bookingNumber());

            verify(bookingRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        void gelAllBookingsUser_shouldReturnPaginatedBookings_whenUserIsAuth() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
            Page<Booking> bookingPage = new PageImpl<>(List.of(testBooking), pageable, 1);

            when(bookingRepository.findAllByUser(any(Pageable.class), eq(testUser))).thenReturn(bookingPage);

            Page<BookingResponse> result = bookingServiceImpl.getAllBookingsUser(testUser, 0, 10, "id", "ASC");

            assertEquals(1, result.getTotalElements());
            assertEquals(testBooking.getBookingNumber(), result.getContent().getFirst().bookingNumber());

            verify(bookingRepository, times(1)).findAllByUser(any(Pageable.class), eq(testUser));
        }

        @Test
        void getBookingById_shouldReturnBooking_whenExist() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingResponse response = bookingServiceImpl.getBookingById(1L);

            assertEquals(testBooking.getBookingNumber(), response.bookingNumber());
            assertEquals(testBooking.getTotalPrice(), response.totalPrice());

            verify(bookingRepository, times(1)).findById(1L);
        }

        @Test
        void getBookingById_shouldReturnBooking_whenNoExist() {
            when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookingServiceImpl.getBookingById(99L));

            assertEquals("Booking not found", exception.getMessage());

            verify(bookingRepository, times(1)).findById(99L);
        }
    }

    private  User createTestUser() {
        return User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@email.com")
                .password(ENCODED_PASSWORD)
                .phoneNumber("+123456789")
                .birthDate(LocalDate.of(1990,1,1))
                .role(Role.USER)
                .build();
    }

    private Airport createTestAirport(Long id, String code, String city) {
        return Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl("img.png")
                .build();
    }

    private Route createTestRoute() {
        return Route.builder()
                .id(1L)
                .origin(createTestAirport(1L, "MAD", "Madrid"))
                .destination(createTestAirport(2L, "VAL", "Valencia"))
                .build();
    }

    private Aircraft createTestAircraft() {
        return Aircraft.builder()
                .id(1L)
                .capacity(150)
                .model("AB123")
                .manufacturer("Airbus")
                .flights(List.of())
                .build();
    }

    private Flight createTestFlight() {
        return Flight.builder()
                .id(1L)
                .flightNumber("SK123")
                .availableSeats(150)
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .price(199.99)
                .available(true)
                .aircraft(createTestAircraft())
                .route(createTestRoute())
                .build();
    }

    private Booking createTestBooking() {
        return Booking.builder()
                .id(1L).bookingNumber("SR-ABC123").seatsBooked(2)
                .totalPrice(399.98).bookingStatus(BookingStatus.CREATED)
                .passengerNames(List.of("pepe", "pepa"))
                .passengerBirthDates(List.of(
                        LocalDate.of(1990, 1, 1),
                        LocalDate.of(1992, 2, 2)))
                .user(testUser)
                .flight(createTestFlight())
                .build();
    }

}
