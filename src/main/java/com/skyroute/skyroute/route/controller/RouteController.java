package com.skyroute.skyroute.route.controller;

import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;
import com.skyroute.skyroute.route.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Route", description = "Route management APIs")
public class RouteController {
    private final RouteService routeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Route", description = "Create a new route between two airports")
    public ResponseEntity<RouteResponse> createRoute(@RequestBody @Valid RouteRequest request){
        RouteResponse response = routeService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all Routes", description = "Retrieve a List of all registered routes")
    public ResponseEntity<List<RouteResponse>> getAllRoute(){
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Route by ID", description = "Retrieve a single route by its ID")
    public ResponseEntity<RouteResponse> getRouteById(@PathVariable Long id){
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Route", description = "Update an existing route")
    public ResponseEntity<RouteResponse> updateRoute(
            @PathVariable Long id,
            @RequestBody @Valid RouteRequest request
    ){
        RouteResponse response = routeService.updateRoute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Route")
    private ResponseEntity<Void> deleteRoute(@PathVariable Long id){
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}