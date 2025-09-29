package com.skyroute.skyroute.flight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to search flights by budget")
public record FlightBudgetRequest(
        @Schema(description = "Maximum budget for the flight", example = "300.0")
        @NotNull
        Double budget
) {
}
