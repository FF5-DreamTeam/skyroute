package com.skyroute.skyroute.flight.controller;

import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.service.publicapi.FlightPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/flights")
@RequiredArgsConstructor
@Tag(name = "Flight Public", description = "APIs for managing flights")
public class FlightPublicController {

    private final FlightPublicService flightPublicService;

    @Operation(summary = "Search available flights with pagination")
    @GetMapping("/search")
    public ResponseEntity<Page<FlightSimpleResponse>> searchFlights(
            FlightSearchRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(flightPublicService.searchFlights(request, pageable));
    }

}