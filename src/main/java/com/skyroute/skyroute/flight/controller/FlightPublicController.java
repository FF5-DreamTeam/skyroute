package com.skyroute.skyroute.flight.controller;

import com.skyroute.skyroute.flight.dto.publicapi.FlightBudgetRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightMapper;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.service.publicapi.FlightPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flights", description = "Public APIs to browse flights")
public class FlightPublicController {

    private final FlightPublicService flightPublicService;
    private final FlightMapper flightMapper;

    @Operation(summary = "Search flights by parameters (origin, destination, date, passengers)")
    @GetMapping("/search")
    public ResponseEntity<?> searchFlightsByParams(
            @RequestParam @Parameter(description = "Origin airport code or city") String origin,
            @RequestParam @Parameter(description = "Destination airport code or city") String destination,
            @RequestParam @Parameter(description = "Departure date (dd/MM/yyyy)") String departureDate,
            @RequestParam(required = false) @Parameter(description = "Return date (dd/MM/yyyy)") String returnDate,
            @RequestParam @Parameter(description = "Number of passengers") Integer passengers,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<FlightSimpleResponse> flights = flightPublicService.searchFlightsByParams(
                origin, destination, departureDate, returnDate, passengers, pageable
        );
        if (flights.isEmpty()) {
            return ResponseEntity.ok("No flights found with the given criteria.");
        }
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "Search flights by budget")
    @PostMapping("/budget")
    public ResponseEntity<?> searchFlightsByBudget(
            @RequestBody FlightBudgetRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<FlightSimpleResponse> flights = flightPublicService.searchFlightsByBudget(request.budget(), pageable);
        if (flights.isEmpty()) {
            return ResponseEntity.ok("No flights found within the given budget.");
        }
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "Get flight by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FlightSimpleResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightPublicService.getFlightById(id));
    }

    @Operation(summary = "Search flights by city")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<FlightSimpleResponse>> getFlightsByCity(@PathVariable String city) {
        List<FlightSimpleResponse> flights = flightPublicService.getAvailableFlightsByCity(city);
        return ResponseEntity.ok(flights);
    }
}




