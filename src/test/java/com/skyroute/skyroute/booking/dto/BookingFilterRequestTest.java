package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.enums.BookingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingFilterRequest Tests")
class BookingFilterRequestTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create BookingFilterRequest with all fields")
        void shouldCreateBookingFilterRequestWithAllFields() {
            LocalDate departureDate = LocalDate.now().plusDays(1);

            BookingFilterRequest request = new BookingFilterRequest(
                    BookingStatus.CONFIRMED,
                    "SR-123456",
                    departureDate,
                    100.0,
                    500.0,
                    1L,
                    "user@example.com",
                    "John Doe",
                    1L,
                    "SK123",
                    "MAD",
                    "VAL",
                    "John Smith",
                    true,
                    true,
                    false);

            assertNotNull(request);
            assertEquals(BookingStatus.CONFIRMED, request.bookingStatus());
            assertEquals("SR-123456", request.bookingNumber());
            assertEquals(departureDate, request.flightDepartureDate());
            assertEquals(100.0, request.minPrice());
            assertEquals(500.0, request.maxPrice());
            assertEquals(1L, request.userId());
            assertEquals("user@example.com", request.userEmail());
            assertEquals("John Doe", request.userName());
            assertEquals(1L, request.flightId());
            assertEquals("SK123", request.flightNumber());
            assertEquals("MAD", request.originAirport());
            assertEquals("VAL", request.destinationAirport());
            assertEquals("John Smith", request.passengerName());
            assertTrue(request.futureFlightsOnly());
            assertTrue(request.activeOnly());
            assertFalse(request.pendingOnly());
        }

        @Test
        @DisplayName("Should create BookingFilterRequest with null values")
        void shouldCreateBookingFilterRequestWithNullValues() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

            assertNotNull(request);
            assertNull(request.bookingStatus());
            assertNull(request.bookingNumber());
            assertNull(request.flightDepartureDate());
            assertNull(request.minPrice());
            assertNull(request.maxPrice());
            assertNull(request.userId());
            assertNull(request.userEmail());
            assertNull(request.userName());
            assertNull(request.flightId());
            assertNull(request.flightNumber());
            assertNull(request.originAirport());
            assertNull(request.destinationAirport());
            assertNull(request.passengerName());
            assertNull(request.futureFlightsOnly());
            assertNull(request.activeOnly());
            assertNull(request.pendingOnly());
        }
    }

    @Nested
    @DisplayName("hasAnyFilter Tests")
    class HasAnyFilterTests {

        @Test
        @DisplayName("Should return true when bookingStatus is not null")
        void shouldReturnTrueWhenBookingStatusIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    BookingStatus.CONFIRMED, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when bookingNumber is not null")
        void shouldReturnTrueWhenBookingNumberIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, "SR-123", null, null, null, null, null, null, null, null, null, null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when flightDepartureDate is not null")
        void shouldReturnTrueWhenFlightDepartureDateIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, LocalDate.now(), null, null, null, null, null, null, null, null, null, null, null, null,
                    null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when minPrice is not null")
        void shouldReturnTrueWhenMinPriceIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, 100.0, null, null, null, null, null, null, null, null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when maxPrice is not null")
        void shouldReturnTrueWhenMaxPriceIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, 500.0, null, null, null, null, null, null, null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when userId is not null")
        void shouldReturnTrueWhenUserIdIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, 1L, null, null, null, null, null, null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when userEmail is not null")
        void shouldReturnTrueWhenUserEmailIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, "user@example.com", null, null, null, null, null, null, null,
                    null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when userName is not null")
        void shouldReturnTrueWhenUserNameIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, "John Doe", null, null, null, null, null, null, null,
                    null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when flightId is not null")
        void shouldReturnTrueWhenFlightIdIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, 1L, null, null, null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when flightNumber is not null")
        void shouldReturnTrueWhenFlightNumberIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, "SK123", null, null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when originAirport is not null")
        void shouldReturnTrueWhenOriginAirportIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, "MAD", null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when destinationAirport is not null")
        void shouldReturnTrueWhenDestinationAirportIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, "VAL", null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when passengerName is not null")
        void shouldReturnTrueWhenPassengerNameIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, "John Smith", null, null,
                    null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when futureFlightsOnly is not null")
        void shouldReturnTrueWhenFutureFlightsOnlyIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, null, true, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when activeOnly is not null")
        void shouldReturnTrueWhenActiveOnlyIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, true, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when pendingOnly is not null")
        void shouldReturnTrueWhenPendingOnlyIsNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, true);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return false when all fields are null")
        void shouldReturnFalseWhenAllFieldsAreNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

            assertFalse(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should return true when multiple fields are not null")
        void shouldReturnTrueWhenMultipleFieldsAreNotNull() {
            BookingFilterRequest request = new BookingFilterRequest(
                    BookingStatus.CONFIRMED,
                    "SR-123",
                    LocalDate.now(),
                    100.0,
                    500.0,
                    1L,
                    "user@example.com",
                    "John Doe",
                    1L,
                    "SK123",
                    "MAD",
                    "VAL",
                    "John Smith",
                    true,
                    true,
                    false);

            assertTrue(request.hasAnyFilter());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle zero values for numeric fields")
        void shouldHandleZeroValuesForNumericFields() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, 0.0, 0.0, 0L, null, null, 0L, null, null, null, null, null, null, null);

            assertTrue(request.hasAnyFilter());
        }

        @Test
        @DisplayName("Should handle false boolean values")
        void shouldHandleFalseBooleanValues() {
            BookingFilterRequest request = new BookingFilterRequest(
                    null, null, null, null, null, null, null, null, null, null, null, null, null, false, false, false);

            assertTrue(request.hasAnyFilter());
        }
    }
}
