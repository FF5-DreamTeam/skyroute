package com.skyroute.skyroute.booking;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.specification.BookingSpecification;
import jakarta.persistence.criteria.*;
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

    @Mock
    Join<Object, Object> flightJoin;

    @Mock
    Join<Object, Object> userJoin;

    @Mock
    Join<Object, Object> routeJoin;

    @Mock
    Join<Object, Object> originJoin;

    @Mock
    Join<Object, Object> destinationJoin;

    @Mock
    Join<Object, Object> passengerNames;

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
            when(criteriaBuilder.between(any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(predicate);
            when(root.join(eq("flight"), any())).thenReturn(flightJoin);
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

        @Test
        void hasPriceBetween_shouldReturnPredicate_whenOnlyMinPriceProvided() {
            Double minPrice = 100.0;
            when(criteriaBuilder.greaterThanOrEqualTo(any(), eq(minPrice))).thenReturn(predicate);
            when(root.get("totalPrice")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.hasPriceBetween(minPrice, null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).greaterThanOrEqualTo(any(), eq(minPrice));
        }

        @Test
        void hasPriceBetween_shouldReturnPredicate_whenOnlyMaxPriceProvided() {
            Double maxPrice = 500.0;
            when(criteriaBuilder.lessThanOrEqualTo(any(), eq(maxPrice))).thenReturn(predicate);
            when(root.get("totalPrice")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.hasPriceBetween(null, maxPrice);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).lessThanOrEqualTo(any(), eq(maxPrice));
        }

        @Test
        void hasPriceBetween_shouldReturnConjunction_whenBothPricesAreNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasPriceBetween(null, null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasUserIdTest {

        @Test
        void hasUserId_shouldReturnPredicate_whenUserIdProvided() {
            Long userId = 1L;
            when(criteriaBuilder.equal(any(), eq(userId))).thenReturn(predicate);
            when(root.join(eq("user"), any())).thenReturn(userJoin);
            Specification<Booking> specification = BookingSpecification.hasUserId(userId);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).equal(any(), eq(userId));
        }

        @Test
        void hasUserId_shouldReturnConjunction_whenUserIdIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasUserId(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasUserEmailTest {

        @Test
        void hasUserEmail_shouldReturnPredicate_whenEmailProvided() {
            String email = "test@email.com";
            when(criteriaBuilder.equal(any(), eq(email.toLowerCase()))).thenReturn(predicate);
            when(criteriaBuilder.lower(any())).thenReturn(null);
            when(root.join(eq("user"), any())).thenReturn(userJoin);
            Specification<Booking> specification = BookingSpecification.hasUserEmail(email);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).equal(any(), eq(email.toLowerCase()));
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenEmailIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasUserEmail(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenEmailIsEmpty() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasUserEmail("");
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasUserNameTest {

        @Test
        void hasUserName_shouldReturnPredicate_whenNameProvided() {
            String name = "Pepa";
            when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
            when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
            when(criteriaBuilder.lower(any())).thenReturn(null);
            when(root.join(eq("user"), any())).thenReturn(userJoin);
            Specification<Booking> specification = BookingSpecification.hasUserName(name);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).or(any(Predicate.class), any(Predicate.class));
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenNameIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasUserName(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenNameIsEmpty() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasUserName("");
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasFlightIdTest {

        @Test
        void hasFlightId_shouldReturnPredicate_whenFlightIdProvided() {
            Long flightId = 1L;
            when(criteriaBuilder.equal(any(), eq(flightId))).thenReturn(predicate);
            when(root.join(eq("flight"), any())).thenReturn(flightJoin);
            Specification<Booking> specification = BookingSpecification.hasFlightId(flightId);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).equal(any(), eq(flightId));
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenFlightIdIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasFlightId(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasFlightNumberTest {

        @Test
        void hasFlightNumber_shouldReturnPredicate_whenFlightNumberProvided() {
            String flightNumber = "SK123";
            when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
            when(criteriaBuilder.upper(any())).thenReturn(null);
            when(root.join(eq("flight"), any())).thenReturn(flightJoin);
            Specification<Booking> specification = BookingSpecification.hasFlightNumber(flightNumber);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).like(any(), contains(flightNumber.toUpperCase()));
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenFlightNumberIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasFlightNumber(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenFlightNumberIsEmpty() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasFlightNumber("");
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasOriginAirportTest {

        @Test
        void hasOriginAirportOrCode_shouldReturnPredicate_whenAirportProvided() {
            String airport = "Madrid";
            Path cityPath = mock(Path.class);
            Path codePath = mock(Path.class);
            Expression lowerExpression = mock(Expression.class);
            Expression upperExpression = mock(Expression.class);
            when(root.join(eq("flight"), any(JoinType.class))).thenReturn(flightJoin);
            when(flightJoin.join(eq("route"), any(JoinType.class))).thenReturn(routeJoin);
            when(routeJoin.join(eq("origin"), any(JoinType.class))).thenReturn(originJoin);
            when(originJoin.get("city")).thenReturn(cityPath);
            when(originJoin.get("code")).thenReturn(codePath);
            when(criteriaBuilder.lower(cityPath)).thenReturn(lowerExpression);
            when(criteriaBuilder.upper(codePath)).thenReturn(upperExpression);
            when(criteriaBuilder.like(eq(lowerExpression), anyString())).thenReturn(predicate);
            when(criteriaBuilder.equal(eq(upperExpression), anyString())).thenReturn(predicate);
            when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasOriginAirportOrCode(airport);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(root).join(eq("flight"), any(JoinType.class));
            verify(flightJoin).join(eq("route"), any(JoinType.class));
            verify(routeJoin).join(eq("origin"), any(JoinType.class));
            verify(originJoin).get("city");
            verify(originJoin).get("code");
            verify(criteriaBuilder).lower(cityPath);
            verify(criteriaBuilder).upper(codePath);
            verify(criteriaBuilder).or(any(Predicate.class), any(Predicate.class));
        }

        @Test
        void hasOriginAirportOrCode_shouldReturnConjunction_whenAirportIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasOriginAirportOrCode(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }

        @Test
        void hasOriginAirportOrCode_shouldReturnConjunction_whenAirportIsEmpty() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasOriginAirportOrCode("");
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasDepartureAirportTest {

        @Test
        void hasDepartureAirportOrCode_shouldReturnPredicate_whenAirportProvided() {
            String airport = "Madrid";
            Path cityPath = mock(Path.class);
            Path codePath = mock(Path.class);
            Expression lowerExpression = mock(Expression.class);
            Expression upperExpression = mock(Expression.class);
            when(root.join(eq("flight"), any(JoinType.class))).thenReturn(flightJoin);
            when(flightJoin.join(eq("route"), any(JoinType.class))).thenReturn(routeJoin);
            when(routeJoin.join(eq("destination"), any(JoinType.class))).thenReturn(destinationJoin);
            when(destinationJoin.get("city")).thenReturn(cityPath);
            when(destinationJoin.get("code")).thenReturn(codePath);
            when(criteriaBuilder.lower(cityPath)).thenReturn(lowerExpression);
            when(criteriaBuilder.upper(codePath)).thenReturn(upperExpression);
            when(criteriaBuilder.like(eq(lowerExpression), anyString())).thenReturn(predicate);
            when(criteriaBuilder.equal(eq(upperExpression), anyString())).thenReturn(predicate);
            when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasDestinationAirportOrCode(airport);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(root).join(eq("flight"), any(JoinType.class));
            verify(flightJoin).join(eq("route"), any(JoinType.class));
            verify(routeJoin).join(eq("destination"), any(JoinType.class));
            verify(destinationJoin).get("city");
            verify(destinationJoin).get("code");
            verify(criteriaBuilder).lower(cityPath);
            verify(criteriaBuilder).upper(codePath);
            verify(criteriaBuilder).or(any(Predicate.class), any(Predicate.class));
        }

        @Test
        void hasDestinationAirportOrCode_shouldReturnConjunction_whenAirportIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasDestinationAirportOrCode(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }

        @Test
        void hasDestinationAirportOrCode_shouldReturnConjunction_whenAirportIsEmpty() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasDestinationAirportOrCode("");
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasPassengerNameTest {

        @Test
        void hasPassengerName_shouldReturnPredicate_whenNameProvided() {
            String passengerName = "Pepe";
            when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
            when(criteriaBuilder.lower(any())).thenReturn(null);
            when(root.join(eq("passengerNames"), any())).thenReturn(passengerNames);
            Specification<Booking> specification = BookingSpecification.hasPassengerName(passengerName);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).like(any(), contains(passengerName.toLowerCase()));
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenPassengerNameIsNull() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasPassengerName(null);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }

        @Test
        void hasUserEmail_shouldReturnConjunction_whenPassengerNameIsEmpty() {
            when(criteriaBuilder.conjunction()).thenReturn(predicate);
            Specification<Booking> specification = BookingSpecification.hasPassengerName("");
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
        }
    }

    @Nested
    class HasFutureFlightsTest {

        @Test
        void hasFutureFlights_shouldReturnPredicate_whenCalled() {
            when(criteriaBuilder.greaterThan(any(), any(LocalDateTime.class))).thenReturn(predicate);
            when(root.join(eq("flight"), any())).thenReturn(flightJoin);
            Specification<Booking> specification = BookingSpecification.hasFutureFlights();
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).greaterThan(any(), any(LocalDateTime.class));
        }
    }

    @Nested
    class HasPastFlightsTest {

        @Test
        void hasPastFlights_shouldReturnPredicate_whenCalled() {
            when(criteriaBuilder.lessThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);
            when(root.join(eq("flight"), any())).thenReturn(flightJoin);
            Specification<Booking> specification = BookingSpecification.hasPastFlights();
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).lessThanOrEqualTo(any(), any(LocalDateTime.class));
        }
    }

    @Nested
    class IsActiveTest {
        @Test
        void isActive_shouldReturnPredicate_whenCalled() {
            when(criteriaBuilder.notEqual(any(), eq(BookingStatus.CANCELLED))).thenReturn(predicate);
            when(root.get("bookingStatus")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.isActive();
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).notEqual(any(), eq(BookingStatus.CANCELLED));
        }
    }

    @Nested
    class IsPendingTest {

        @Test
        void isPending_shouldReturnPredicate_whenCalled() {
            when(criteriaBuilder.equal(any(), eq(BookingStatus.CREATED))).thenReturn(predicate);
            when(root.get("bookingStatus")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.isPending();
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);
            verify(criteriaBuilder).equal(any(), eq(BookingStatus.CREATED));
        }
    }

}
