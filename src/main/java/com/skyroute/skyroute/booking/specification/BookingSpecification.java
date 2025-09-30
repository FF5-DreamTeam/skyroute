package com.skyroute.skyroute.booking.specification;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingSpecification {

    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("bookingStatus"), status);
        };
    }

    public static Specification<Booking> hasBookingNumber(String bookingNumber) {
        return (root, query, criteriaBuilder) -> {
            if (bookingNumber == null || bookingNumber.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.upper(root.get("bookingNumber")), "%" + bookingNumber.toUpperCase() + "%"
            );
        };
    }

    public static Specification<Booking> hasFlightDepartureDate(LocalDate departureDate) {
        return (root, query, criteriaBuilder) -> {
            if (departureDate == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> flight = root.join("flight", JoinType.INNER);
            LocalDateTime startOfDay =departureDate.atStartOfDay();
            LocalDateTime endOfDay = departureDate.plusDays(1).atStartOfDay();
            return criteriaBuilder.between(flight.get("departureTime"), startOfDay, endOfDay);
        };
    }

    public static Specification<Booking> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("totalPrice"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("totalPrice"), minPrice);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("totalPrice"), maxPrice);
        };
    }

    public static Specification<Booking> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> user = root.join("user", JoinType.INNER);
            return criteriaBuilder.equal(user.get("id"), userId);
        };
    }

    public static Specification<Booking> hasUserEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> user = root.join("user", JoinType.INNER);
            return criteriaBuilder.equal(criteriaBuilder.lower(user.get("email")), email.toLowerCase());
        };
    }

    public static Specification<Booking> hasUserName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> user = root.join("user", JoinType.INNER);
            String pattern = "%" + name.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(user.get("firstName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(user.get("lastName")), pattern));
        };
    }

    public static Specification<Booking> hasFlightId(Long flightId) {
        return (root, query, criteriaBuilder) -> {
            if (flightId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> flight = root.join("flight", JoinType.INNER);
            return criteriaBuilder.equal(flight.get("id"), flightId);
        };
    }

    public static Specification<Booking> hasFlightNumber(String flightNumber) {
        return (root, query, criteriaBuilder) -> {
            if (flightNumber == null || flightNumber.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> flight = root.join("flight", JoinType.INNER);
            return criteriaBuilder.like(
                    criteriaBuilder.upper(flight.get("flightNumber")), "%" + flightNumber.toUpperCase() + "%"
            );
        };
    }

    public static Specification<Booking> hasOriginAirportOrCode(String originAirport) {
        return (root, query, criteriaBuilder) -> {
            if (originAirport == null || originAirport.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> flight = root.join("flight", JoinType.INNER);
            Join<Object, Object> route = flight.join("route", JoinType.INNER);
            Join<Object, Object> origin = route.join("origin", JoinType.INNER);
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(origin.get("city")), "%" + originAirport.toLowerCase() + "%"),
                    criteriaBuilder.equal(criteriaBuilder.upper(origin.get("code")), originAirport.toUpperCase())
            );
        };
    }

    public static Specification<Booking> hasDestinationAirportOrCode(String destinationAirport) {
        return (root, query, criteriaBuilder) -> {
            if (destinationAirport == null || destinationAirport.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> flight = root.join("flight", JoinType.INNER);
            Join<Object, Object> route = flight.join("route", JoinType.INNER);
            Join<Object, Object> destination = route.join("destination", JoinType.INNER);
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(destination.get("city")), "%" + destinationAirport.toLowerCase() + "%"),
                    criteriaBuilder.equal(criteriaBuilder.upper(destination.get("code")),destinationAirport.toUpperCase())
            );
        };
    }

    public static Specification<Booking> hasPassengerName(String passengerName) {
        return (root, query, criteriaBuilder) -> {
            if (passengerName == null || passengerName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> passengerNames = root.join("passengerNames", JoinType.INNER);
            return criteriaBuilder.like(
                    criteriaBuilder.lower(passengerNames.as(String.class)), "%" + passengerName.toLowerCase() + "%" );
        };
    }

    public static Specification<Booking> hasFutureFlights() {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> flight = root.join("flight", JoinType.INNER);
            return criteriaBuilder.greaterThan(flight.get("departureTime"), LocalDateTime.now());
        };
    }

    public static Specification<Booking> hasPastFlights() {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> flight = root.join("flight", JoinType.INNER);
            return criteriaBuilder.lessThanOrEqualTo(flight.get("departureTime"), LocalDateTime.now());
        };
    }

    public static Specification<Booking> isActive() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.notEqual(root.get("bookingStatus"), BookingStatus.CANCELLED);
    }

    public static Specification<Booking> isPending() {
        return hasStatus(BookingStatus.CREATED);
    }
}
