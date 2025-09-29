package com.skyroute.skyroute.flight.validation;

import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FlightPublicValidator {

    public void validateSearch(String origin, String destination, LocalDate departureDate,
                               Double minPrice, Double maxPrice, Integer passengers) {

        if ((origin == null || origin.isBlank()) && (destination == null || destination.isBlank()) && minPrice == null) {
            throw new BusinessException("At least one search criterion must be specified: origin, destination, or minimum price.");
        }

        if (passengers != null && passengers <= 0) {
            throw new BusinessException("The number of passengers must be greater than zero.");
        }

        if (minPrice != null && minPrice < 0) {
            throw new BusinessException("Minimum price cannot be negative.");
        }

        if (maxPrice != null && maxPrice < 0) {
            throw new BusinessException("Maximum price cannot be negative.");
        }

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new BusinessException("Minimum price cannot be greater than maximum price.");
        }

        if (departureDate != null && departureDate.isBefore(LocalDate.now())) {
            throw new BusinessException("Departure date must be today or in the future.");
        }
    }
}