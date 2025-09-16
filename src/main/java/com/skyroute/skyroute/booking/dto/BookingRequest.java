package com.skyroute.skyroute.booking.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record BookingRequest(
    @NotNull(message = "Flight ID is required")
    @Positive(message = "Flight ID must be positive")
    Long flightId,

    @Positive(message = "Flight ID must be positive")
    @Max(value = 10, message = "Maximum 10 seats per booking")
    int seatsBooked,

    @NotEmpty(message = "At least one passenger name is required")
    @Size(min = 1, max = 10, message = "Between 1 and 10 passenger allowed")
    List<@NotBlank(message = "Passenger name required") @Size(min = 2, max = 100, message = "Passenger name must contain between 2 and 100 characters") String> passengerNames,

    @NotEmpty(message = "At least one birth date is required")
    @Size(min = 1, max = 10, message = "birth dates must match the number of passengers")
    List<@NotNull(message = "Birth date cannot be null") @Past(message = "Birth date must be in the past") LocalDate> passengerBirthDates
) {
    public BookingRequest {
        if (passengerNames != null && passengerBirthDates != null) {
            if (passengerNames.size() != passengerBirthDates.size()) {
                throw  new IllegalArgumentException("Number of passenger names must match number of passenger birth dates");
            }
            if (seatsBooked != passengerNames.size()) {
                throw new IllegalArgumentException("Number of seats must match number of passengers");
            }
        }
    }
}
