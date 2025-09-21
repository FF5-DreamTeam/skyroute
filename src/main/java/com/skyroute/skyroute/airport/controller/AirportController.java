package com.skyroute.skyroute.airport.controller;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
@Tag(name = "Airport", description = "Airport management APIs")
public class AirportController {
    private final AirportService airportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Airport", description = "Create airport, all fields and image required")
    public ResponseEntity<AirportResponse> createAirport(
            @RequestPart("airport") @Valid AirportCreateRequest request,
            @RequestPart("image")MultipartFile image){
        AirportResponse response = airportService.createAirport(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all Airports", description = "Retrieve a list of all registered airports")
    public ResponseEntity<List<AirportResponse>> getAllAirports(){
        return ResponseEntity.ok(airportService.getAllAirports());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Airport by ID", description = "Retrieve a single airport by its ID")
    public ResponseEntity<AirportResponse> getAirportById(@PathVariable Long id){
        return ResponseEntity.ok(airportService.getAirportById(id));
    }
}