package com.skyroute.skyroute.aircraft.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AircraftRequest(

        @NotBlank(message = "Model cannot be blank")
        String model,

        @NotBlank(message = "Manufacturer cannot be blank")
        String manufacturer,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1 seat")
        Integer capacity
) {}
