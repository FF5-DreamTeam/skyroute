package com.skyroute.skyroute.booking.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingRequest Tests")
class BookingRequestTest {

    private final Validator validator;

    public BookingRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create valid BookingRequest")
        void shouldCreateValidBookingRequest() {
            List<String> passengerNames = List.of("John Doe", "Jane Smith");
            List<LocalDate> birthDates = List.of(
                    LocalDate.of(1990, 1, 1),
                    LocalDate.of(1992, 2, 2));

            BookingRequest request = new BookingRequest(1L, 2, passengerNames, birthDates);

            assertNotNull(request);
            assertEquals(1L, request.flightId());
            assertEquals(2, request.bookedSeats());
            assertEquals(passengerNames, request.passengerNames());
            assertEquals(birthDates, request.passengerBirthDates());
        }

        @Test
        @DisplayName("Should throw exception when passenger names and birth dates count mismatch")
        void shouldThrowExceptionWhenPassengerNamesAndBirthDatesCountMismatch() {
            List<String> passengerNames = List.of("John Doe", "Jane Smith");
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new BookingRequest(1L, 2, passengerNames, birthDates));

            assertEquals("Number of passenger names must match number of passenger birth dates",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when booked seats and passenger names count mismatch")
        void shouldThrowExceptionWhenBookedSeatsAndPassengerNamesCountMismatch() {
            List<String> passengerNames = List.of("John Doe", "Jane Smith");
            List<LocalDate> birthDates = List.of(
                    LocalDate.of(1990, 1, 1),
                    LocalDate.of(1992, 2, 2));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new BookingRequest(1L, 1, passengerNames, birthDates));

            assertEquals("Number of seats must match number of passengers", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle null passenger names and birth dates")
        void shouldHandleNullPassengerNamesAndBirthDates() {
            BookingRequest request = new BookingRequest(1L, 2, null, null);

            assertNotNull(request);
            assertEquals(1L, request.flightId());
            assertEquals(2, request.bookedSeats());
            assertNull(request.passengerNames());
            assertNull(request.passengerBirthDates());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should pass validation with valid data")
        void shouldPassValidationWithValidData() {
            List<String> passengerNames = List.of("John Doe", "Jane Smith");
            List<LocalDate> birthDates = List.of(
                    LocalDate.of(1990, 1, 1),
                    LocalDate.of(1992, 2, 2));

            BookingRequest request = new BookingRequest(1L, 2, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when flightId is null")
        void shouldFailValidationWhenFlightIdIsNull() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            BookingRequest request = new BookingRequest(null, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Flight ID is required")));
        }

        @Test
        @DisplayName("Should fail validation when flightId is negative")
        void shouldFailValidationWhenFlightIdIsNegative() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            BookingRequest request = new BookingRequest(-1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Flight ID must be positive")));
        }

        @Test
        @DisplayName("Should fail validation when flightId is zero")
        void shouldFailValidationWhenFlightIdIsZero() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            BookingRequest request = new BookingRequest(0L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Flight ID must be positive")));
        }

        @Test
        @DisplayName("Should fail validation when bookedSeats is negative")
        void shouldFailValidationWhenBookedSeatsIsNegative() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new BookingRequest(1L, -1, passengerNames, birthDates));

            assertEquals("Number of seats must match number of passengers", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when bookedSeats is zero")
        void shouldFailValidationWhenBookedSeatsIsZero() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new BookingRequest(1L, 0, passengerNames, birthDates));

            assertEquals("Number of seats must match number of passengers", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when bookedSeats exceeds maximum")
        void shouldFailValidationWhenBookedSeatsExceedsMaximum() {
            List<String> passengerNames = List.of("John Doe", "Jane Smith", "Bob Johnson", "Alice Brown",
                    "Charlie Wilson", "Diana Prince", "Eve Adams", "Frank Miller", "Grace Lee", "Henry Davis");
            List<LocalDate> birthDates = List.of(
                    LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1), LocalDate.of(1992, 1, 1),
                    LocalDate.of(1993, 1, 1), LocalDate.of(1994, 1, 1), LocalDate.of(1995, 1, 1),
                    LocalDate.of(1996, 1, 1), LocalDate.of(1997, 1, 1), LocalDate.of(1998, 1, 1),
                    LocalDate.of(1999, 1, 1));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new BookingRequest(1L, 11, passengerNames, birthDates));

            assertEquals("Number of seats must match number of passengers", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when passengerNames is empty")
        void shouldFailValidationWhenPassengerNamesIsEmpty() {
            List<String> passengerNames = List.of();
            List<LocalDate> birthDates = List.of();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new BookingRequest(1L, 1, passengerNames, birthDates));

            assertEquals("Number of seats must match number of passengers", exception.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when passengerNames is null")
        void shouldFailValidationWhenPassengerNamesIsNull() {
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            BookingRequest request = new BookingRequest(1L, 1, null, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("At least one passenger name is required")));
        }

        @Test
        @DisplayName("Should fail validation when passengerNames exceeds maximum")
        void shouldFailValidationWhenPassengerNamesExceedsMaximum() {
            List<String> passengerNames = List.of("John", "Jane", "Bob", "Alice", "Charlie",
                    "Diana", "Eve", "Frank", "Grace", "Henry", "Ivy");
            List<LocalDate> birthDates = List.of(
                    LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1), LocalDate.of(1992, 1, 1),
                    LocalDate.of(1993, 1, 1), LocalDate.of(1994, 1, 1), LocalDate.of(1995, 1, 1),
                    LocalDate.of(1996, 1, 1), LocalDate.of(1997, 1, 1), LocalDate.of(1998, 1, 1),
                    LocalDate.of(1999, 1, 1), LocalDate.of(2000, 1, 1));

            BookingRequest request = new BookingRequest(1L, 11, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(
                    violations.stream().anyMatch(v -> v.getMessage().contains("Between 1 and 10 passenger allowed")));
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "A" })
        @DisplayName("Should fail validation when passenger name is blank or too short")
        void shouldFailValidationWhenPassengerNameIsBlankOrTooShort(String invalidName) {
            List<String> passengerNames = List.of(invalidName);
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Passenger name required") ||
                    v.getMessage().contains("Passenger name must contain between 2 and 100 characters")));
        }

        @Test
        @DisplayName("Should fail validation when passenger name is too long")
        void shouldFailValidationWhenPassengerNameIsTooLong() {
            String longName = "A".repeat(101);
            List<String> passengerNames = List.of(longName);
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(
                    v -> v.getMessage().contains("Passenger name must contain between 2 and 100 characters")));
        }

        @Test
        @DisplayName("Should fail validation when passengerBirthDates is empty")
        void shouldFailValidationWhenPassengerBirthDatesIsEmpty() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new BookingRequest(1L, 1, passengerNames, birthDates));

            assertEquals("Number of passenger names must match number of passenger birth dates",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when passengerBirthDates is null")
        void shouldFailValidationWhenPassengerBirthDatesIsNull() {
            List<String> passengerNames = List.of("John Doe");

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, null);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(
                    violations.stream().anyMatch(v -> v.getMessage().contains("At least one birth date is required")));
        }

        @Test
        @DisplayName("Should fail validation when birth date is null")
        void shouldFailValidationWhenBirthDateIsNull() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = Arrays.asList((LocalDate) null);

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Birth date cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation when birth date is in the future")
        void shouldFailValidationWhenBirthDateIsInTheFuture() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of(LocalDate.now().plusDays(1));

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Birth date must be in the past")));
        }

        @Test
        @DisplayName("Should fail validation when birth date is today")
        void shouldFailValidationWhenBirthDateIsToday() {
            List<String> passengerNames = List.of("John Doe");
            List<LocalDate> birthDates = List.of(LocalDate.now());

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Birth date must be in the past")));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle maximum valid values")
        void shouldHandleMaximumValidValues() {
            List<String> passengerNames = List.of("John", "Jane", "Bob", "Alice", "Charlie",
                    "Diana", "Eve", "Frank", "Grace", "Henry");
            List<LocalDate> birthDates = List.of(
                    LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1), LocalDate.of(1992, 1, 1),
                    LocalDate.of(1993, 1, 1), LocalDate.of(1994, 1, 1), LocalDate.of(1995, 1, 1),
                    LocalDate.of(1996, 1, 1), LocalDate.of(1997, 1, 1), LocalDate.of(1998, 1, 1),
                    LocalDate.of(1999, 1, 1));

            BookingRequest request = new BookingRequest(1L, 10, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should handle minimum valid values")
        void shouldHandleMinimumValidValues() {
            List<String> passengerNames = List.of("John");
            List<LocalDate> birthDates = List.of(LocalDate.of(1990, 1, 1));

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should handle very old birth date")
        void shouldHandleVeryOldBirthDate() {
            List<String> passengerNames = List.of("John");
            List<LocalDate> birthDates = List.of(LocalDate.of(1900, 1, 1));

            BookingRequest request = new BookingRequest(1L, 1, passengerNames, birthDates);
            var violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }
    }
}
