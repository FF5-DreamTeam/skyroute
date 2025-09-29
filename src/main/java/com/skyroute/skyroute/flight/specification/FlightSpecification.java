package com.skyroute.skyroute.flight.specification;

import com.skyroute.skyroute.flight.entity.Flight;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlightSpecification {
    public static Specification<Flight> hasOriginEquals(String origin){
        return (root, query, criteriaBuilder) ->{
            if (origin == null || origin.isBlank()){
                return criteriaBuilder.conjunction();
            }

            Predicate codeMatch = criteriaBuilder.equal(
                    root.get("route").get("origin").get("code"), origin
            );

            Predicate cityMatch = criteriaBuilder.equal(
                    criteriaBuilder.upper(root.get("route").get("origin").get("city")),
                    origin.toUpperCase()
            );

            return criteriaBuilder.or(codeMatch, cityMatch);
        };
    }

    public static Specification<Flight> hasDestinationEquals(String destination){
        return (root, query, criteriaBuilder) -> {
            if (destination == null || destination.isBlank()){
                return criteriaBuilder.conjunction();
            }

            Predicate codeMatch = criteriaBuilder.equal(
                    root.get("route").get("destination").get("code"), destination
            );

            Predicate cityMatch = criteriaBuilder.equal(
                    criteriaBuilder.upper(root.get("route").get("destination").get("city")),
                    destination.toUpperCase()
            );

            return criteriaBuilder.or(codeMatch, cityMatch);
        };
    }

    public static Specification<Flight> hasDepartureDateEquals(String departureDate) {
        if (departureDate == null || departureDate.isBlank()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        LocalDate date;
        try {
            date = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException exception) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        LocalDateTime searchStart = date.isEqual(LocalDate.now())
                ? LocalDateTime.now()
                : date.atStartOfDay();

        LocalDateTime searchEnd = date.plusDays(1).atStartOfDay();

        return (root, query, criteriaBuilder) -> {
            Path<LocalDateTime> departureTime = root.get("departureTime");

            Expression<String> departureDateStr = criteriaBuilder.function(
                    "FORMATDATETIME",
                    String.class,
                    departureTime,
                    criteriaBuilder.literal("yyyy-MM-dd")
            );

            String searchDateStr = date.toString();

            Predicate isAvailable = criteriaBuilder.isTrue(root.get("available"));
            Predicate dateMatches = criteriaBuilder.equal(departureDateStr, searchDateStr);
            Predicate timeIsValid = criteriaBuilder.greaterThanOrEqualTo(departureTime, searchStart);

            return criteriaBuilder.and(isAvailable, dateMatches, timeIsValid);
        };
    }

    public static Specification<Flight> hasPassengersAvailable(Integer passengers){
        return (root, query, criteriaBuilder) ->
                passengers == null ? criteriaBuilder.conjunction() :
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
                maxPrice == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}