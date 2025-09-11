package com.skyroute.skyroute.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

public record BookingRequest(
    @NotNull
    Long flightId,

    @NotBlank(message = "Full name of passenger needed")
    String passengerName,

    @NotBlank(message = "Passport of passenger needed")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Passport must contain capital letters and numbers only")
    String passengerPassport,

    @NotBlank(message = "Passport of passenger needed")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "dd-MM-yyyy")
    String passengerBirthDate,

    int seatBooked
) {
}
