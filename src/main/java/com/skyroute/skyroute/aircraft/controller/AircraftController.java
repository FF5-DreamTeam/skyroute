package com.skyroute.skyroute.aircraft.controller;

import com.skyroute.skyroute.aircraft.dto.AircraftRequest;
import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.aircraft.service.AircraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aircrafts")
@RequiredArgsConstructor
@Tag(name = "Aircraft", description = "Manage aircrafts in the system")
public class AircraftController {

    private final AircraftService aircraftService;

    @PostMapping
    @Operation(summary = "Create a new aircraft")
    public ResponseEntity<AircraftResponse> createAircraft(@Valid @RequestBody AircraftRequest request) {
        AircraftResponse response = aircraftService.createAircraft(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get aircraft by ID")
    public ResponseEntity<AircraftResponse> getAircraft(@PathVariable Long id) {
        AircraftResponse response = aircraftService.getAircraftById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all aircrafts")
    public ResponseEntity<List<AircraftResponse>> getAllAircrafts() {
        List<AircraftResponse> responseList = aircraftService.getAllAircrafts();
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update aircraft by ID")
    public ResponseEntity<AircraftResponse> updateAircraft(@PathVariable Long id,
                                                           @Valid @RequestBody AircraftRequest request) {
        AircraftResponse response = aircraftService.updateAircraft(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete aircraft by ID")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);
        return ResponseEntity.noContent().build();
    }
}
