package com.skyroute.skyroute.flight.controller;

import com.skyroute.skyroute.flight.dto.FlightBudgetRequest;
import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.dto.FlightSimpleResponse;
import com.skyroute.skyroute.flight.dto.FlightUpdate;
import com.skyroute.skyroute.flight.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flights", description = "API for managing and search flights")
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/search")
    @Operation(summary = "Search flights by parameters", description = "Search available flights by origin, destination, date and passengers")
    public ResponseEntity<?> searchFlights(
            @RequestParam @Parameter(description = "Origin airport code or city") Optional<String> origin,
            @RequestParam @Parameter(description = "Destination airport code or city") Optional<String> destination,
            @RequestParam @Parameter(description = "Departure date (dd/MM/yyyy)") Optional<String> departureDate,
            @RequestParam @Parameter(description = "Number of passengers") Optional<Integer> passengers,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<FlightSimpleResponse> flights = flightService.searchFlights(
                origin,
                destination,
                departureDate,
                passengers,
                pageable
        );
        return ResponseEntity.ok(flights);
    }

    @PostMapping("/budget")
    @Operation(summary = "Search flights by budget", description = "Find available flights within specified budget")
    public ResponseEntity<Page<FlightSimpleResponse>> searchFlightsByBudget(
            @Valid @RequestBody FlightBudgetRequest request,
            @PageableDefault(size = 10, sort = {"price", "departureTime"}) Pageable pageable
    ) {
        Page<FlightSimpleResponse> flights = flightService.searchFlightsByBudget(request.budget(), pageable);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieve flight details by ID")
    public ResponseEntity<FlightSimpleResponse> getFlightById(@PathVariable Long id) {
        FlightSimpleResponse flight = flightService.getFlightSimpleById(id);
        return ResponseEntity.ok(flight);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Search flights by city", description = "Find available flights from or to a specific city")
    public ResponseEntity<List<FlightSimpleResponse>> getFlightsByCity(@PathVariable String city) {
        List<FlightSimpleResponse> flights = flightService.getAvailableFlightsByCity(city);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all flights (Admin only)", description = "Retrieve paginated list of all flights")
    public ResponseEntity<Page<FlightResponse>> getAllFlights(@PageableDefault(size = 10) Pageable pageable) {
        Page<FlightResponse> flights = flightService.getFlightsPage(pageable);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get flight by ID (Admin)", description = "Retrieve detailed flight information by ID")
    public ResponseEntity<FlightResponse> getFlightDetailsById(@PathVariable Long id) {
        FlightResponse flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new flight (Admin)")
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody FlightRequest request) {
        FlightResponse flight = flightService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(flight);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing flight (Admin)", description = "Update an existing flight")
    public ResponseEntity<FlightResponse> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightUpdate request) {
        FlightResponse flight = flightService.updateFlight(id, request);
        return ResponseEntity.ok(flight);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a flight (Admin)", description = "Delete a flight by ID")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
