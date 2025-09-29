package com.skyroute.skyroute.flight.controller;

import com.skyroute.skyroute.flight.dto.FlightBudgetRequest;
import com.skyroute.skyroute.flight.dto.FlightRequest;
import com.skyroute.skyroute.flight.dto.FlightResponse;
import com.skyroute.skyroute.flight.dto.FlightSimpleResponse;
import com.skyroute.skyroute.flight.dto.FlightUpdate;
import com.skyroute.skyroute.flight.service.FlightService;
import com.skyroute.skyroute.flight.service.FlightServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flights", description = "API for managing and browsing flights")
public class FlightController {

    private final FlightServiceImpl flightService;

    @Operation(summary = "Search flights by parameters")
    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(
            @RequestParam @Parameter(description = "Origin airport code or city") String origin,
            @RequestParam @Parameter(description = "Destination airport code or city") String destination,
            @RequestParam @Parameter(description = "Departure date (dd/MM/yyyy)") String departureDate,
            @RequestParam(required = false) @Parameter(description = "Return date (dd/MM/yyyy)") String returnDate,
            @RequestParam @Parameter(description = "Number of passengers") Integer passengers,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<FlightSimpleResponse> flights = flightService.searchFlights(origin, destination, departureDate, returnDate, passengers, pageable);
        if (flights.isEmpty()) {
            return ResponseEntity.ok("No flights found with the given criteria.");
        }
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "Search flights by budget")
    @PostMapping("/budget")
    public ResponseEntity<?> searchFlightsByBudget(
            @Valid @RequestBody FlightBudgetRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<FlightSimpleResponse> flights = flightService.searchFlightsByBudget(request.budget(), pageable);
        if (flights.isEmpty()) {
            return ResponseEntity.ok("No flights found within the given budget.");
        }
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "Get flight by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FlightSimpleResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightSimpleById(id));
    }

    @Operation(summary = "Search flights by city")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<FlightSimpleResponse>> getFlightsByCity(@PathVariable String city) {
        return ResponseEntity.ok(flightService.getAvailableFlightsByCity(city));
    }

    @Operation(summary = "Get all flights (Admin only)")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FlightResponse>> getAllFlightsAdmin(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(flightService.getFlightsPage(pageable));
    }

    @Operation(summary = "Get flight by ID (Admin)")
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightResponse> getFlightByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @Operation(summary = "Create a new flight (Admin)")
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody FlightRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.createFlight(request));
    }

    @Operation(summary = "Update an existing flight (Admin)")
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightResponse> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightUpdate request) {
        return ResponseEntity.ok(flightService.updateFlight(id, request));
    }

    @Operation(summary = "Delete a flight (Admin)")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
