package com.skyroute.skyroute.airport.controller;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.dto.AirportUpdateRequest;
import com.skyroute.skyroute.airport.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
            @ModelAttribute @Valid AirportCreateRequest request
    ) {
        AirportResponse response = airportService.createAirport(request);
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

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update Airport", description = "Update airport - all fields and image optional (partial update)")
    public ResponseEntity<AirportResponse> updateAirport(
            @PathVariable Long id,
            @RequestParam(value = "code", required = false) @Size(min = 3, max = 3) @Pattern(regexp = "[A-Z]{3}") String code,
            @RequestParam(value = "city", required = false) @Size(min = 2, max = 100) String city,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        AirportUpdateRequest request = new AirportUpdateRequest(code, city);
        AirportResponse response = airportService.updateAirport(id, request, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Airport")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id){
        airportService.deleteAirport(id);
        return ResponseEntity.noContent().build();
    }
}