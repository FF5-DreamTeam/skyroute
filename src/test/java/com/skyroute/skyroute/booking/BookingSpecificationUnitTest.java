package com.skyroute.skyroute.booking;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.specification.BookingSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BookingSpecificationUnitTest {

    @Mock
    private Root<Booking> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Nested
    class HasStatusTest {

        @Test
        void hasStatus_shouldReturnPredicate_whenStatusProvided() {
            BookingStatus status = BookingStatus.CREATED;
            when(criteriaBuilder.equal(any(), eq(status))).thenReturn(predicate);
            when(root.get("bookingStatus")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.hasStatus(status);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);

            verify(criteriaBuilder).equal(any(), eq(status));
        }

        @Test
        void hasStatus_shouldReturnConjunction_whenStatusIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasStatus(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);

            verify(criteriaBuilder).conjunction();
            verify(criteriaBuilder, never()).equal(any(), any());
        }
    }

    @Nested
    class HasBookingNumberTest {

        @Test
        void hasBookingNumber_shouldReturnPredicate_whenBookingNumberProvided() {
            String bookingNumber = "SR-ABC123";
            when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
            when(criteriaBuilder.upper(any())).thenReturn(null);
            when(root.get("bookingNumber")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.hasBookingNumber(bookingNumber);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);

            verify(criteriaBuilder).like(any(), contains(bookingNumber.toUpperCase()));
        }

        @Test
        void hasBookingNumber_shouldReturnConjunction_whenBookingNumberIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasBookingNumber(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);

            verify(criteriaBuilder).conjunction();
        }

        @Test
        void hasBookingNumber_shouldReturnConjunction_whenBookingNumberIsEmpty() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasBookingNumber("");
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);

            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasFlightDepartureDateTest {

        @Test
        void hasFlightDepartureDate_shouldReturnPredicate_whenDateProvided() {
            LocalDate departureDate = LocalDate.of(2025, 12, 1);
            when(criteriaBuilder.between(any(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(predicate);
            when(root.join("flight", any())).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.hasFlightDepartureDate(departureDate);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).between(any(), any(LocalDateTime.class), any(LocalDateTime.class));
        }

        @Test
        void hasFlightDepartureDate_shouldReturnConjunction_whenDateIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasFlightDepartureDate(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);

            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasPriceBetweenTest {
        @Test
        void hasPriceBetween_shouldReturnPredicate_whenBothPricesProvided() {
            Double minPrice = 100.0;
            Double maxPrice = 500.0;
            when(criteriaBuilder.between(any(), eq(minPrice), eq(maxPrice))).thenReturn(predicate);
            when(root.get("totalPrice")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.hasPriceBetween(minPrice, maxPrice);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).between(any(), eq(minPrice), eq(maxPrice));
        }


    }
}
