package com.skyroute.skyroute.route.service;

import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.service.AirportService;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.repository.RouteRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidRouteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final AirportService airportService;

    @Override
    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        validateRouteRequest(request);

        Airport origin = airportService.findAirportById(request.originId());
        Airport destination = airportService.findAirportById(request.destinationId());

        validateUniqueRoute(origin.getId(), destination.getId());

        Route route = RouteMapper.toEntity(request, origin, destination);
        Route savedRoute = routeRepository.save(route);

        return RouteMapper.toDto(savedRoute);
    }

    @Override
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll()
                .stream()
                .map(RouteMapper::toDto)
                .toList();
    }

    @Override
    public RouteResponse getRouteById(Long id) {
        Route route = findRouteById(id);
        return RouteMapper.toDto(route);
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Long id, RouteRequest request) {
        validateRouteRequest(request);

        Route existingRoute = findRouteById(id);
        Airport origin = airportService.findAirportById(request.originId());
        Airport destination = airportService.findAirportById(request.destinationId());

        validateUniqueRouteFromUpdate(id, origin.getId(), destination.getId());

        existingRoute.setOrigin(origin);
        existingRoute.setDestination(destination);

        Route updatedRoute = routeRepository.save(existingRoute);
        return RouteMapper.toDto(updatedRoute);
    }

    @Override
    public void deleteRoute(Long id) {
        Route route = findRouteById(id);
        routeRepository.delete(route);
    }

    private void validateUniqueRouteFromUpdate(Long routeId, Long originId, Long destinationId){
        if (routeRepository.existsByOriginIdAndDestinationIdAndIdNot(originId, destinationId, routeId)){
            throw new EntityAlreadyExistsException(
                    "Route already exists between these airports: " + originId + " -> " + destinationId
            );
        }
    }

    @Override
    public Route findRouteById(Long id){
        return routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route not found with ID: " + id));
    }

    private void validateRouteRequest(RouteRequest request){
        if (request.originId().equals(request.destinationId())){
            throw new InvalidRouteException("Origin and destination airports cannot be the same");
        }
    }

    private void validateUniqueRoute(Long originId, Long destinationId){
        if (routeRepository.existsByOriginIdAndDestinationId(originId, destinationId)){
            throw new EntityAlreadyExistsException(
                    "Route already exists between these airports: " + originId + " -> " + destinationId
            );
        }
    }
}