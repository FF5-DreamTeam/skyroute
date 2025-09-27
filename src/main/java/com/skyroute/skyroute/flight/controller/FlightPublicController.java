package com.skyroute.skyroute.flight.controller;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.service.publicapi.FlightPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flights", description = "Public APIs to browse flights")
public class FlightPublicController {

    private final FlightPublicService flightPublicService;

    @Operation(summary = "Search flights by criteria")
    @GetMapping
    public ResponseEntity<Page<FlightSimpleResponse>> searchFlights(
            @RequestParam(required = false) @Parameter(description = "Origin airport code or city") String origin,
            @RequestParam(required = false) @Parameter(description = "Destination airport code or city") String destination,
            @RequestParam(required = false) @Parameter(description = "Departure date in format dd/MM/yyyy")
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate departureDate,
            @RequestParam(required = false) @Parameter(description = "Minimum price") Double minPrice,
            @RequestParam(required = false) @Parameter(description = "Maximum price") Double maxPrice,
            @RequestParam(required = false) @Parameter(description = "Number of passengers") Integer passengers,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<FlightSimpleResponse> flights = flightPublicService.getFlightsPage(
                pageable, origin, destination, departureDate, minPrice, maxPrice, passengers
        );
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "Get flight by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FlightSimpleResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightPublicService.getFlightById(id));
    }
}

