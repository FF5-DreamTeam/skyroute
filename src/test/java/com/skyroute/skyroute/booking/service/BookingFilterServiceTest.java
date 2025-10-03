package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingFilterRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class BookingFilterServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingFilterServiceImpl bookingFilterService;

    private User testUser;
    private User testAdmin;
    private Booking testBooking;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        testUser = createUser(1L, Role.USER);
        testAdmin = createUser(2L, Role.ADMIN);
        testBooking = createBooking();
        defaultPageable = PageRequest.of(0, 10);
    }

    @Nested
    class FilterBookingsTests {

        @Test
        void shouldReturnFilteredBookingsForUser() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withBookingStatus(BookingStatus.CONFIRMED).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).bookingNumber()).isEqualTo(testBooking.getBookingNumber());

            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldReturnFilteredBookingsForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withBookingStatus(BookingStatus.CONFIRMED).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);

            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldHandleNullFilterRequestForUser() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            Page<BookingResponse> result = bookingFilterService.filterBookings(null, defaultPageable, testUser);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldHandleNullFilterRequestForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            Page<BookingResponse> result = bookingFilterService.filterBookings(null, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldReturnEmptyPageWhenNoBookingsMatch() {
            Page<Booking> emptyPage = new PageImpl<>(List.of(), defaultPageable, 0);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(emptyPage);

            BookingFilterRequest filter = buildFilter().withBookingStatus(BookingStatus.CANCELLED).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    class AdminFilterTests {

        @Test
        void shouldApplyUserIdFilterForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withUserId(1L).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyUserEmailFilterForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withUserEmail("user@example.com").build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyUserNameFilterForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withUserName("John Doe").build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyFlightIdFilterForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withFlightId(1L).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyFlightNumberFilterForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withFlightNumber("SK123").build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyActiveOnlyFilterForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withActiveOnly(true).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyPendingOnlyFilterForAdmin() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withPendingOnly(true).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testAdmin);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }
    }

    @Nested
    class CommonFilterTests {

        @Test
        void shouldApplyBookingStatusFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withBookingStatus(BookingStatus.CONFIRMED).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyBookingNumberFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withBookingNumber("SR-123").build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyDepartureDateFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withFlightDepartureDate(LocalDate.now()).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyPriceRangeFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withMinPrice(100.0).withMaxPrice(500.0).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyOriginAirportFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withOriginAirport("MAD").build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyDestinationAirportFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withDestinationAirport("VAL").build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyPassengerNameFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withPassengerName("John Smith").build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyFutureFlightsFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withFutureFlightsOnly(true).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldApplyPastFlightsFilter() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withFutureFlightsOnly(false).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void shouldHandleEmptyStringFilters() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter()
                    .withBookingNumber("")
                    .withUserEmail("")
                    .withUserName("")
                    .build();

            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldHandleZeroPriceValues() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withMinPrice(0.0).withMaxPrice(0.0).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldHandleOnlyMinPrice() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withMinPrice(100.0).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldHandleOnlyMaxPrice() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter().withMaxPrice(500.0).build();
            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }

        @Test
        void shouldHandleMultipleFiltersAtOnce() {
            Page<Booking> bookingPage = createPageWithBookings(testBooking);
            when(bookingRepository.findAll(any(Specification.class), eq(defaultPageable)))
                    .thenReturn(bookingPage);

            BookingFilterRequest filter = buildFilter()
                    .withBookingStatus(BookingStatus.CONFIRMED)
                    .withOriginAirport("MAD")
                    .withDestinationAirport("VAL")
                    .withMinPrice(100.0)
                    .withMaxPrice(500.0)
                    .withFutureFlightsOnly(true)
                    .build();

            Page<BookingResponse> result = bookingFilterService.filterBookings(filter, defaultPageable, testUser);

            assertThat(result).isNotNull();
            verify(bookingRepository).findAll(any(Specification.class), eq(defaultPageable));
        }
    }

    private User createUser(Long id, Role role) {
        return User.builder()
                .id(id)
                .firstName("Test")
                .lastName("User" + id)
                .email("test" + id + "@email.com")
                .password("encodedPassword")
                .phoneNumber("+123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(role)
                .build();
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .bookingNumber("SR-ABC123")
                .bookedSeats(2)
                .totalPrice(399.98)
                .bookingStatus(BookingStatus.CREATED)
                .passengerNames(List.of("John", "Jane"))
                .passengerBirthDates(List.of(
                        LocalDate.of(1990, 1, 1),
                        LocalDate.of(1992, 2, 2)))
                .user(testUser)
                .flight(createFlight())
                .build();
    }

    private com.skyroute.skyroute.flight.entity.Flight createFlight() {
        return com.skyroute.skyroute.flight.entity.Flight.builder()
                .id(1L)
                .flightNumber("SK123")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .availableSeats(100)
                .price(199.99)
                .available(true)
                .route(createRoute())
                .build();
    }

    private com.skyroute.skyroute.route.entity.Route createRoute() {
        return com.skyroute.skyroute.route.entity.Route.builder()
                .id(1L)
                .origin(createAirport(1L, "MAD", "Madrid"))
                .destination(createAirport(2L, "VAL", "Valencia"))
                .build();
    }

    private com.skyroute.skyroute.airport.entity.Airport createAirport(Long id, String code, String city) {
        return com.skyroute.skyroute.airport.entity.Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl("https://example.com/" + code.toLowerCase() + ".jpg")
                .build();
    }

    private Page<Booking> createPageWithBookings(Booking... bookings) {
        return new PageImpl<>(List.of(bookings), defaultPageable, bookings.length);
    }

    private FilterBuilder buildFilter() {
        return new FilterBuilder();
    }

    private static class FilterBuilder {
        private BookingStatus bookingStatus;
        private String bookingNumber;
        private LocalDate flightDepartureDate;
        private Double minPrice;
        private Double maxPrice;
        private Long userId;
        private String userEmail;
        private String userName;
        private Long flightId;
        private String flightNumber;
        private String originAirport;
        private String destinationAirport;
        private String passengerName;
        private Boolean futureFlightsOnly;
        private Boolean activeOnly;
        private Boolean pendingOnly;

        FilterBuilder withBookingStatus(BookingStatus bookingStatus) {
            this.bookingStatus = bookingStatus;
            return this;
        }

        FilterBuilder withBookingNumber(String bookingNumber) {
            this.bookingNumber = bookingNumber;
            return this;
        }

        FilterBuilder withFlightDepartureDate(LocalDate flightDepartureDate) {
            this.flightDepartureDate = flightDepartureDate;
            return this;
        }

        FilterBuilder withMinPrice(Double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        FilterBuilder withMaxPrice(Double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        FilterBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        FilterBuilder withUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        FilterBuilder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        FilterBuilder withFlightId(Long flightId) {
            this.flightId = flightId;
            return this;
        }

        FilterBuilder withFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
            return this;
        }

        FilterBuilder withOriginAirport(String originAirport) {
            this.originAirport = originAirport;
            return this;
        }

        FilterBuilder withDestinationAirport(String destinationAirport) {
            this.destinationAirport = destinationAirport;
            return this;
        }

        FilterBuilder withPassengerName(String passengerName) {
            this.passengerName = passengerName;
            return this;
        }

        FilterBuilder withFutureFlightsOnly(Boolean futureFlightsOnly) {
            this.futureFlightsOnly = futureFlightsOnly;
            return this;
        }

        FilterBuilder withActiveOnly(Boolean activeOnly) {
            this.activeOnly = activeOnly;
            return this;
        }

        FilterBuilder withPendingOnly(Boolean pendingOnly) {
            this.pendingOnly = pendingOnly;
            return this;
        }

        BookingFilterRequest build() {
            return new BookingFilterRequest(
                    bookingStatus,
                    bookingNumber,
                    flightDepartureDate,
                    minPrice,
                    maxPrice,
                    userId,
                    userEmail,
                    userName,
                    flightId,
                    flightNumber,
                    originAirport,
                    destinationAirport,
                    passengerName,
                    futureFlightsOnly,
                    activeOnly,
                    pendingOnly);
        }
    }
}
