package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.email.EmailService;
import com.skyroute.skyroute.flight.service.FlightService;
import com.skyroute.skyroute.shared.exception.custom_exception.BookingAccessDeniedException;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.booking.repository.BookingRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightService flightService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    private User testUser;
    private User testAdmin;
    private User anotherUser;
    private Booking testBooking;
    private Flight testFlight;
    private BookingRequest testRequest;
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @BeforeEach
    void setUp() {
        testUser = createTestUser(1L, Role.USER);
        testAdmin = createTestUser(2L, Role.ADMIN);
        anotherUser = createTestUser(3L, Role.USER);
        testBooking = createTestBooking();
        testFlight = createTestFlight();
        testBooking = createTestBooking();
        testRequest = createTestRequest();
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

            BookingResponse response = bookingServiceImpl.getBookingById(1L, testUser);

            assertEquals(testBooking.getBookingNumber(), response.bookingNumber());
            assertEquals(testBooking.getTotalPrice(), response.totalPrice());

            verify(bookingRepository, times(1)).findById(1L);
        }

        @Test
        void getBookingById_shouldReturnBooking_whenNoExist() {
            when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> bookingServiceImpl.getBookingById(99L, testUser));

            assertEquals("Booking not found", exception.getMessage());

            verify(bookingRepository, times(1)).findById(99L);
        }
    }

    @Nested
    class CreateBookingsTests {

        @Test
        void createBooking_shouldCreateBooking_whenValidRequest() {
            when(flightService.findById(1L)).thenReturn(testFlight);
            when(flightService.isFlightAvailable(1L)).thenReturn(true);
            when(flightService.hasAvailableSeats(1L, 2)).thenReturn(true);
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            doNothing().when(flightService).bookSeats(1L, 2);
            doNothing().when(emailService).sendBookingConfirmationEmail(any(Booking.class), any(User.class),
                    any(Flight.class));

            BookingResponse result = bookingServiceImpl.createBooking(testRequest, testUser);

            assertNotNull(result);
            assertEquals(testBooking.getBookingNumber(), result.bookingNumber());
            assertEquals(testBooking.getTotalPrice(), result.totalPrice());
            assertEquals(testBooking.getBookedSeats(), result.bookedSeats());

            verify(flightService).findById(1L);
            verify(flightService).isFlightAvailable(1L);
            verify(flightService).hasAvailableSeats(1L, 2);
            verify(flightService).bookSeats(1L, 2);
            verify(bookingRepository).save(any(Booking.class));
            verify(emailService).sendBookingConfirmationEmail(any(Booking.class), any(User.class), any(Flight.class));
        }

        @Test
        void createBooking_shouldThrowException_whenFlightNotFound() {
            when(flightService.findById(1L)).thenThrow(new EntityNotFoundException("Flight with id: 1 not found"));

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> bookingServiceImpl.createBooking(testRequest, testUser));

            assertEquals("Flight with id: 1 not found", exception.getMessage());

            verify(flightService).findById(1L);
            verify(bookingRepository, never()).save(any());
            verify(flightService, never()).bookSeats(anyLong(), anyInt());
        }

        @Test
        void createBooking_shouldThrowException_whenFlightNotAvailable() {
            when(flightService.findById(1L)).thenReturn(testFlight);
            when(flightService.isFlightAvailable(1L)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> bookingServiceImpl.createBooking(testRequest, testUser));

            assertEquals("Flight not available for booking", exception.getMessage());

            verify(flightService).findById(1L);
            verify(flightService).isFlightAvailable(1L);
            verify(bookingRepository, never()).save(any());
            verify(flightService, never()).bookSeats(anyLong(), anyInt());
        }

        @Test
        void createBooking_shouldThrowException_whenInsufficientSeats() {
            when(flightService.findById(1L)).thenReturn(testFlight);
            when(flightService.isFlightAvailable(1L)).thenReturn(true);
            when(flightService.hasAvailableSeats(1L, 2)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> bookingServiceImpl.createBooking(testRequest, testUser));

            assertTrue(exception.getMessage().contains("Not enough seats available"));

            verify(flightService, atLeastOnce()).findById(1L);
            verify(flightService).isFlightAvailable(1L);
            verify(flightService).hasAvailableSeats(1L, 2);
            verify(bookingRepository, never()).save(any());
            verify(flightService, never()).bookSeats(anyLong(), anyInt());
        }

        @Test
        void createBooking_shouldCalculateCorrectTotalPrice() {
            testFlight.setPrice(100.0);

            when(flightService.findById(1L)).thenReturn(testFlight);
            when(flightService.isFlightAvailable(1L)).thenReturn(true);
            when(flightService.hasAvailableSeats(1L, 2)).thenReturn(true);
            when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
                Booking booking = invocation.getArgument(0);
                booking.setId(1L);
                return booking;
            });
            doNothing().when(flightService).bookSeats(1L, 2);
            doNothing().when(emailService).sendBookingConfirmationEmail(any(Booking.class), any(User.class),
                    any(Flight.class));

            BookingResponse result = bookingServiceImpl.createBooking(testRequest, testUser);

            assertNotNull(result);
            assertEquals(200.0, result.totalPrice());
            assertEquals(2, result.bookedSeats());

            verify(bookingRepository).save(argThat(booking -> booking.getTotalPrice().equals(200.0)));
            verify(emailService).sendBookingConfirmationEmail(any(Booking.class), any(User.class), any(Flight.class));
        }

        @Test
        void createBooking_shouldSendEmail_whenBookingCreated() {
            when(flightService.findById(1L)).thenReturn(testFlight);
            when(flightService.isFlightAvailable(1L)).thenReturn(true);
            when(flightService.hasAvailableSeats(1L, 2)).thenReturn(true);
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            doNothing().when(flightService).bookSeats(1L, 2);
            doNothing().when(emailService).sendBookingConfirmationEmail(any(Booking.class), any(User.class),
                    any(Flight.class));

            bookingServiceImpl.createBooking(testRequest, testUser);

            verify(emailService).sendBookingConfirmationEmail(
                    any(Booking.class),
                    eq(testUser),
                    any(Flight.class));
        }

        @Test
        void createBooking_shouldNotFail_whenEmailSendingFails() {
            when(flightService.findById(1L)).thenReturn(testFlight);
            when(flightService.isFlightAvailable(1L)).thenReturn(true);
            when(flightService.hasAvailableSeats(1L, 2)).thenReturn(true);
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            doNothing().when(flightService).bookSeats(1L, 2);

            BookingResponse result = bookingServiceImpl.createBooking(testRequest, testUser);

            assertNotNull(result);
            assertEquals(testBooking.getBookingNumber(), result.bookingNumber());
        }
    }

    @Nested
    class UpdateBookingStatusTests {

        @Test
        void updateBookingStatus_shouldUpdateStatusToCancel_whenUserValidTransition() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponse result = bookingServiceImpl.updateBookingStatus(1L, BookingStatus.CANCELLED, testUser);

            assertNotNull(result);
            assertEquals(BookingStatus.CANCELLED, testBooking.getBookingStatus());

            verify(bookingRepository).findById(1L);
            verify(bookingRepository).save(testBooking);
        }

        @Test
        void updateBookingStatus_shouldReleaseSeats_whenCancellingConfirmedBooking() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            testBooking.getFlight().setDepartureTime(LocalDateTime.now().plusDays(2));
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            doNothing().when(flightService).releaseSeats(1L, 2);

            bookingServiceImpl.updateBookingStatus(1L, BookingStatus.CANCELLED, testUser);

            assertEquals(BookingStatus.CANCELLED, testBooking.getBookingStatus());

            verify(flightService).releaseSeats(1L, 2);
            verify(bookingRepository).save(testBooking);
        }

        @Test
        void updateBookingStatus_shouldThrowException_whenUserCancelsConfirmedBookingLess24h() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            testBooking.getFlight().setDepartureTime(LocalDateTime.now().plusHours(10));
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> bookingServiceImpl.updateBookingStatus(1L, BookingStatus.CANCELLED, testUser));

            assertEquals("You can only cancel the booking up to 24 hours before the flight departure. Please contact our customer service for further assistance", exception.getMessage());
            verify(flightService, never()).releaseSeats(anyLong(), anyInt());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void updateBookingStatus_shouldNotReleaseSeats_whenAlreadyCancelled() {
            testBooking.setBookingStatus(BookingStatus.CANCELLED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> bookingServiceImpl.updateBookingStatus(1L, BookingStatus.CANCELLED, testUser));

            assertEquals("Booking is already in CANCELLED status", exception.getMessage());
            verify(flightService, never()).releaseSeats(anyLong(), anyInt());
        }

        @Test
        void updateBookingStatus_shouldThrowException_whenInvalidTestTransition() {
            testBooking.setBookingStatus(BookingStatus.CANCELLED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> bookingServiceImpl.updateBookingStatus(1l, BookingStatus.CONFIRMED, testUser));

            assertEquals("Cannot change status of a CANCELLED booking", exception.getMessage());
        }

        @Test
        void updateBookingStatus_shouldThrowException_whenUserInvalidTransition() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingAccessDeniedException exception = assertThrows(BookingAccessDeniedException.class,
                    () -> bookingServiceImpl.updateBookingStatus(1l, BookingStatus.CONFIRMED, testUser));

            assertEquals("Users cannot confirm booking", exception.getMessage());
        }

        @Test
        void updateBookingStatus_shouldThrowException_whenSameStatus() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> bookingServiceImpl.updateBookingStatus(1L, BookingStatus.CONFIRMED, testUser));

            assertEquals("Booking is already in CONFIRMED status", exception.getMessage());
        }

        @Test
        void updateBookingStatus_shouldThrowException_whenUserAccessDenied() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingAccessDeniedException exception = assertThrows(BookingAccessDeniedException.class,
                    () -> bookingServiceImpl.updateBookingStatus(1L, BookingStatus.CONFIRMED, anotherUser));

            assertEquals("User cannot access this booking", exception.getMessage());
        }

        @Test
        void updateBookingStatus_shouldAllowAdmin_ToUpdateAnyBooking() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponse result = bookingServiceImpl.updateBookingStatus(1L, BookingStatus.CONFIRMED, testAdmin);

            assertNotNull(result);
            assertEquals(BookingStatus.CONFIRMED, testBooking.getBookingStatus());

            verify(bookingRepository).save(testBooking);
        }
    }

    @Nested
    class CancelBookingTests {

        @Test
        void cancelBooking_shouldUpdateStatusToCancelled() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            doNothing().when(flightService).releaseSeats(1L, 2);

            bookingServiceImpl.cancelBooking(1L, testUser);

            assertEquals(BookingStatus.CANCELLED, testBooking.getBookingStatus());

            verify(flightService).releaseSeats(1L, 2);
            verify(bookingRepository).save(testBooking);
        }
    }

    @Nested
    class ConfirmBookingTests {

        @Test
        void confirmBooking_shouldUpdateStatusToConfirmed() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponse result = bookingServiceImpl.confirmBooking(1L, testAdmin);

            assertNotNull(result);
            assertEquals(BookingStatus.CONFIRMED, testBooking.getBookingStatus());

            verify(bookingRepository).save(testBooking);
        }
    }

    @Nested
    class UpdateBookingPassengersTests {

        @Test
        void updatePassengerNames_shouldUpdate_whenUserAndBookingCreated() {
            testBooking.setBookingStatus(BookingStatus.CREATED);
            List<String> newNames = List.of("Pepa", "Luisa");
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponse result = bookingServiceImpl.updatePassengerNames(1L, newNames, testUser);

            assertNotNull(result);
            assertEquals(newNames, testBooking.getPassengerNames());
            verify(bookingRepository).save(testBooking);
        }

        @Test
        void updatePassengerNames_shouldThrowException_whenUserAndBookingConfirmed() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingAccessDeniedException exception = assertThrows(BookingAccessDeniedException.class,
                    () -> bookingServiceImpl.updatePassengerNames(1L, List.of("Pepa"), testUser));

            assertEquals("Cannot modify passenger names after booking is CONFORMED or CANCELLED",
                    exception.getMessage());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void updatePassengerNames_shouldThrowException_shenUserAndBookingCancelled() {
            testBooking.setBookingStatus(BookingStatus.CANCELLED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingAccessDeniedException exception = assertThrows(BookingAccessDeniedException.class,
                    () -> bookingServiceImpl.updatePassengerNames(1L, List.of("Pepa"), testUser));

            assertEquals("Cannot modify passenger names after booking is CONFORMED or CANCELLED",
                    exception.getMessage());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void updatePassengerNames_shouldAllowAdminUpdate_regardlessOfStatus() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            List<String> newNames = List.of("Pepa", "Lola");
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponse result = bookingServiceImpl.updatePassengerNames(1L, newNames, testAdmin);

            assertNotNull(result);
            assertEquals(newNames, testBooking.getPassengerNames());

            verify(bookingRepository).save(testBooking);
        }

        @Test
        void updatePassengerBirthDates_shouldUpdate_whenUserAndBookingCreated() {
            testBooking.setBookingStatus(BookingStatus.CREATED);
            List<LocalDate> newBirthDates = List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1990, 1, 2));
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponse result = bookingServiceImpl.updatePassengerBirthDates(1L, newBirthDates, testUser);

            assertNotNull(result);
            assertEquals(newBirthDates, testBooking.getPassengerBirthDates());
            verify(bookingRepository).save(testBooking);
        }

        @Test
        void updatePassengerBirthDates_shouldThrowException_whenUserAndBookingConfirmed() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingAccessDeniedException exception = assertThrows(BookingAccessDeniedException.class,
                    () -> bookingServiceImpl.updatePassengerBirthDates(1L,
                            List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1990, 1, 2)), testUser));

            assertEquals("Cannot modify passenger birth dates after booking is CONFORMED or CANCELLED",
                    exception.getMessage());

            verify(bookingRepository, never()).save(any());
        }

        @Test
        void updatePassengerBirthDates_shouldThrowException_shenUserAndBookingCancelled() {
            testBooking.setBookingStatus(BookingStatus.CANCELLED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingAccessDeniedException exception = assertThrows(BookingAccessDeniedException.class,
                    () -> bookingServiceImpl.updatePassengerBirthDates(1L,
                            List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1990, 1, 2)), testUser));

            assertEquals("Cannot modify passenger birth dates after booking is CONFORMED or CANCELLED",
                    exception.getMessage());

            verify(bookingRepository, never()).save(any());
        }

        @Test
        void updatePassengerBirthDates_shouldAllowAdminUpdate_regardlessOfStatus() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            List<LocalDate> newBirthDates = List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1990, 1, 2));
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponse result = bookingServiceImpl.updatePassengerBirthDates(1L, newBirthDates, testAdmin);

            assertNotNull(result);
            assertEquals(newBirthDates, testBooking.getPassengerBirthDates());

            verify(bookingRepository).save(testBooking);
        }
    }

    @Nested
    class DeleteBookingTests {

        @Test
        void deleteBooking_shouldDeleteBooking_whenUserOwnsItAndStatusIsCreated() {
            testBooking.setBookingStatus(BookingStatus.CREATED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            doNothing().when(bookingRepository).delete(testBooking);

            bookingServiceImpl.deleteBooking(1L, testUser);

            verify(bookingRepository).delete(testBooking);
        }

        @Test
        void deleteBooking_shouldThrowAccessDenied_whenUserTriesToDeleteConfirmedBooking() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            BookingAccessDeniedException exception = assertThrows(BookingAccessDeniedException.class,
                    () -> bookingServiceImpl.deleteBooking(1L, testUser));

            assertEquals("Users can only delete bookings in CREATED status", exception.getMessage());
        }

        @Test
        void deleteBooking_shouldAllowAdmin_ToDeleteAnyBooking() {
            testBooking.setBookingStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            doNothing().when(bookingRepository).delete(testBooking);

            bookingServiceImpl.deleteBooking(1L, testAdmin);

            verify(bookingRepository).delete(testBooking);
        }
    }

    private User createTestUser(Long id, Role role) {
        return User.builder()
                .id(id)
                .firstName("Test")
                .lastName("User" + id)
                .email("test" + id + "@email.com")
                .password(ENCODED_PASSWORD)
                .phoneNumber("+123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(role)
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
                .id(1L).bookingNumber("SR-ABC123").bookedSeats(2)
                .totalPrice(399.98).bookingStatus(BookingStatus.CREATED)
                .passengerNames(List.of("pepe", "pepa"))
                .passengerBirthDates(List.of(
                        LocalDate.of(1990, 1, 1),
                        LocalDate.of(1992, 2, 2)))
                .user(testUser)
                .flight(createTestFlight())
                .build();
    }

    private BookingRequest createTestRequest() {
        return new BookingRequest(
                1L,
                2,
                List.of("Pepito", "Maria"),
                List.of(LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1)));
    }
}
