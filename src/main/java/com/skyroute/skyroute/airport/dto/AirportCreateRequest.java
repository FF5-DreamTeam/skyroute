package com.skyroute.skyroute.airport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AirportCreateRequest(
        @NotBlank(message = "Airport code cannot be blank")
        @Size(min = 3, max = 3, message = "Airport code must be 3 characters long")
        @Pattern(regexp = "[A-Z]{3}", message = "Airport code must consist of 3 uppercase letters")
        String code,

        @NotBlank(message = "City cannot be blank")
        @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters")
        String city
) {}