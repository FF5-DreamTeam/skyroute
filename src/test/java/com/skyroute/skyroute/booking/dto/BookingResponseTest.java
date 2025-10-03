package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.enums.BookingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingResponse Tests")
class BookingResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create BookingResponse with all fields")
        void shouldCreateBookingResponseWithAllFields() {
            LocalDateTime now = LocalDateTime.now();
            List<String> passengerNames = List.of("John Doe", "Jane Smith");
            List<String> birthDates = List.of("1990-01-01", "1992-02-02");

            BookingResponse response = new BookingResponse(
                    1L,
                    "SR-ABC123",
                    BookingStatus.CONFIRMED,
                    1L,
                    "SK123",
                    "MAD",
                    "VAL",
                    now,
                    now.plusHours(2),
                    passengerNames,
                    birthDates,
                    2,
                    399.98,
                    now.minusDays(1),
                    now);

            assertNotNull(response);
            assertEquals(1L, response.bookingId());
            assertEquals("SR-ABC123", response.bookingNumber());
            assertEquals(BookingStatus.CONFIRMED, response.bookingStatus());
            assertEquals(1L, response.flightId());
            assertEquals("SK123", response.flightNumber());
            assertEquals("MAD", response.originAirport());
            assertEquals("VAL", response.destinationAirport());
            assertEquals(now, response.departureTime());
            assertEquals(now.plusHours(2), response.arrivalTime());
            assertEquals(passengerNames, response.passengerNames());
            assertEquals(birthDates, response.passengerBirthDates());
            assertEquals(2, response.bookedSeats());
            assertEquals(399.98, response.totalPrice());
            assertEquals(now.minusDays(1), response.createdAt());
            assertEquals(now, response.updatedAt());
        }

        @Test
        @DisplayName("Should create BookingResponse with null values")
        void shouldCreateBookingResponseWithNullValues() {
            BookingResponse response = new BookingResponse(
                    null, null, null, null, null, null, null, null, null, null, null, 0, null, null, null);

            assertNotNull(response);
            assertNull(response.bookingId());
            assertNull(response.bookingNumber());
            assertNull(response.bookingStatus());
            assertNull(response.flightId());
            assertNull(response.flightNumber());
            assertNull(response.originAirport());
            assertNull(response.destinationAirport());
            assertNull(response.departureTime());
            assertNull(response.arrivalTime());
            assertNull(response.passengerNames());
            assertNull(response.passengerBirthDates());
            assertEquals(0, response.bookedSeats());
            assertNull(response.totalPrice());
            assertNull(response.createdAt());
            assertNull(response.updatedAt());
        }

        @Test
        @DisplayName("Should create BookingResponse with different booking statuses")
        void shouldCreateBookingResponseWithDifferentBookingStatuses() {
            LocalDateTime now = LocalDateTime.now();
            List<String> passengerNames = List.of("John Doe");
            List<String> birthDates = List.of("1990-01-01");

            BookingResponse createdResponse = new BookingResponse(
                    1L, "SR-ABC123", BookingStatus.CREATED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            BookingResponse confirmedResponse = new BookingResponse(
                    2L, "SR-DEF456", BookingStatus.CONFIRMED, 2L, "SK456", "VAL", "MAD",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            BookingResponse cancelledResponse = new BookingResponse(
                    3L, "SR-GHI789", BookingStatus.CANCELLED, 3L, "SK789", "MAD", "BCN",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            assertEquals(BookingStatus.CREATED, createdResponse.bookingStatus());
            assertEquals(BookingStatus.CONFIRMED, confirmedResponse.bookingStatus());
            assertEquals(BookingStatus.CANCELLED, cancelledResponse.bookingStatus());
        }

        @Test
        @DisplayName("Should create BookingResponse with empty lists")
        void shouldCreateBookingResponseWithEmptyLists() {
            LocalDateTime now = LocalDateTime.now();
            List<String> emptyNames = List.of();
            List<String> emptyDates = List.of();

            BookingResponse response = new BookingResponse(
                    1L, "SR-ABC123", BookingStatus.CREATED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), emptyNames, emptyDates, 0, 0.0, now, now);

            assertNotNull(response);
            assertTrue(response.passengerNames().isEmpty());
            assertTrue(response.passengerBirthDates().isEmpty());
            assertEquals(0, response.bookedSeats());
            assertEquals(0.0, response.totalPrice());
        }

        @Test
        @DisplayName("Should create BookingResponse with maximum values")
        void shouldCreateBookingResponseWithMaximumValues() {
            LocalDateTime now = LocalDateTime.now();
            List<String> passengerNames = List.of("John", "Jane", "Bob", "Alice", "Charlie",
                    "Diana", "Eve", "Frank", "Grace", "Henry");
            List<String> birthDates = List.of("1990-01-01", "1991-01-01", "1992-01-01", "1993-01-01", "1994-01-01",
                    "1995-01-01", "1996-01-01", "1997-01-01", "1998-01-01", "1999-01-01");

            BookingResponse response = new BookingResponse(
                    Long.MAX_VALUE, "SR-MAX123", BookingStatus.CONFIRMED, Long.MAX_VALUE, "SK999",
                    "XXX", "YYY", now, now.plusHours(24), passengerNames, birthDates, 10, Double.MAX_VALUE, now, now);

            assertNotNull(response);
            assertEquals(Long.MAX_VALUE, response.bookingId());
            assertEquals("SR-MAX123", response.bookingNumber());
            assertEquals(Long.MAX_VALUE, response.flightId());
            assertEquals("SK999", response.flightNumber());
            assertEquals("XXX", response.originAirport());
            assertEquals("YYY", response.destinationAirport());
            assertEquals(10, response.bookedSeats());
            assertEquals(Double.MAX_VALUE, response.totalPrice());
        }

        @Test
        @DisplayName("Should create BookingResponse with zero values")
        void shouldCreateBookingResponseWithZeroValues() {
            LocalDateTime now = LocalDateTime.now();

            BookingResponse response = new BookingResponse(
                    0L, "SR-ZERO", BookingStatus.CREATED, 0L, "SK000", "AAA", "BBB",
                    now, now, List.of(), List.of(), 0, 0.0, now, now);

            assertNotNull(response);
            assertEquals(0L, response.bookingId());
            assertEquals("SR-ZERO", response.bookingNumber());
            assertEquals(0L, response.flightId());
            assertEquals("SK000", response.flightNumber());
            assertEquals("AAA", response.originAirport());
            assertEquals("BBB", response.destinationAirport());
            assertEquals(0, response.bookedSeats());
            assertEquals(0.0, response.totalPrice());
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal when all fields are same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            LocalDateTime now = LocalDateTime.now();
            List<String> passengerNames = List.of("John Doe");
            List<String> birthDates = List.of("1990-01-01");

            BookingResponse response1 = new BookingResponse(
                    1L, "SR-ABC123", BookingStatus.CONFIRMED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            BookingResponse response2 = new BookingResponse(
                    1L, "SR-ABC123", BookingStatus.CONFIRMED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            assertEquals(response1, response2);
            assertEquals(response1.hashCode(), response2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when bookingId is different")
        void shouldNotBeEqualWhenBookingIdIsDifferent() {
            LocalDateTime now = LocalDateTime.now();
            List<String> passengerNames = List.of("John Doe");
            List<String> birthDates = List.of("1990-01-01");

            BookingResponse response1 = new BookingResponse(
                    1L, "SR-ABC123", BookingStatus.CONFIRMED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            BookingResponse response2 = new BookingResponse(
                    2L, "SR-ABC123", BookingStatus.CONFIRMED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            assertNotEquals(response1, response2);
        }

        @Test
        @DisplayName("Should not be equal when bookingNumber is different")
        void shouldNotBeEqualWhenBookingNumberIsDifferent() {
            LocalDateTime now = LocalDateTime.now();
            List<String> passengerNames = List.of("John Doe");
            List<String> birthDates = List.of("1990-01-01");

            BookingResponse response1 = new BookingResponse(
                    1L, "SR-ABC123", BookingStatus.CONFIRMED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            BookingResponse response2 = new BookingResponse(
                    1L, "SR-DEF456", BookingStatus.CONFIRMED, 1L, "SK123", "MAD", "VAL",
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            assertNotEquals(response1, response2);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            LocalDateTime now = LocalDateTime.now();
            String longString = "A".repeat(1000);
            List<String> passengerNames = List.of(longString);
            List<String> birthDates = List.of("1990-01-01");

            BookingResponse response = new BookingResponse(
                    1L, longString, BookingStatus.CONFIRMED, 1L, longString, longString, longString,
                    now, now.plusHours(2), passengerNames, birthDates, 1, 199.99, now, now);

            assertNotNull(response);
            assertEquals(longString, response.bookingNumber());
            assertEquals(longString, response.flightNumber());
            assertEquals(longString, response.originAirport());
            assertEquals(longString, response.destinationAirport());
            assertEquals(longString, response.passengerNames().get(0));
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            LocalDateTime now = LocalDateTime.now();
            String specialString = "SR-123!@#$%^&*()_+-=[]{}|;':\",./<>?";
            List<String> passengerNames = List.of("José María", "François", "Müller");
            List<String> birthDates = List.of("1990-01-01", "1991-02-02", "1992-03-03");

            BookingResponse response = new BookingResponse(
                    1L, specialString, BookingStatus.CONFIRMED, 1L, specialString, specialString, specialString,
                    now, now.plusHours(2), passengerNames, birthDates, 3, 599.97, now, now);

            assertNotNull(response);
            assertEquals(specialString, response.bookingNumber());
            assertEquals(specialString, response.flightNumber());
            assertEquals(specialString, response.originAirport());
            assertEquals(specialString, response.destinationAirport());
            assertEquals(passengerNames, response.passengerNames());
        }

        @Test
        @DisplayName("Should handle negative values")
        void shouldHandleNegativeValues() {
            LocalDateTime now = LocalDateTime.now();

            BookingResponse response = new BookingResponse(
                    -1L, "SR-NEG", BookingStatus.CREATED, -1L, "SK-NEG", "NEG", "NEG",
                    now, now, List.of(), List.of(), -1, -199.99, now, now);

            assertNotNull(response);
            assertEquals(-1L, response.bookingId());
            assertEquals(-1L, response.flightId());
            assertEquals(-1, response.bookedSeats());
            assertEquals(-199.99, response.totalPrice());
        }
    }
}
