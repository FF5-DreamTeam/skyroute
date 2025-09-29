package com.skyroute.skyroute.flight.specification;

import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FlightSpecification {
    public static Specification<Flight> hasOriginEquals(String origin){
        return (root, query, criteriaBuilder) ->
                origin == null ? null :
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("route").get("origin").get("city"), origin),
                        criteriaBuilder.equal(root.get("route").get("origin").get("city"), origin)
        );
    }

    public static Specification<Flight> hasDestinationEquals(String destination){
        return (root, query, criteriaBuilder) ->
                destination == null ? null :
                        criteriaBuilder.or(
                          criteriaBuilder.equal(root.get("route").get("destination").get("city"), destination),
                          criteriaBuilder.equal(root.get("route").get("destination").get("city"), destination)
                        );
    }

    public static Specification<Flight> hasDepartureDateEquals(String departureDate){
        if (departureDate == null || departureDate.isBlank()) return null;

        LocalDate date = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("departureTime"), startOfDay, endOfDay);
    }

    public static Specification<Flight> hasPassengersAvailable(Integer passengers){
        return (root, query, criteriaBuilder) ->
                passengers == null ? null :
                        criteriaBuilder.greaterThanOrEqualTo(root.get("availableSeats"), passengers);
    }

    public static Specification<Flight> isOnlyAvailable(LocalDateTime now){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.isTrue(root.get("available")),
                        criteriaBuilder.greaterThan(root.get("departureTime"), now)
                );
    }

    public static Specification<Flight> hasPricelessThanOrEqual(Double maxPrice){
        return (root, query, criteriaBuilder) ->
                maxPrice == null ? null :
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}
