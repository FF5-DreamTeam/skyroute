package com.skyroute.skyroute.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record BookingRequest(
    @NotNull
    Long flightId,

    @NotBlank(message = "Full name of passenger needed")
    List<String> passengerNames,

    @NotBlank(message = "Passport of passenger needed")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Passport must contain capital letters and numbers only")
    List<String> passengerPassports,

    @NotNull(message = "Birth date of passenger needed")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "dd-MM-yyyy")
    List<LocalDate> passengerBirthDates,
    int seatsBooked/*,
    User user*/
) {
}
