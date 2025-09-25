package com.skyroute.skyroute.route.service;

import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.service.AirportService;
import com.skyroute.skyroute.route.dto.RouteMapper;
import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.route.repository.RouteRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidRouteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final AirportService airportService;

    @Override
    public RouteResponse createRoute(RouteRequest request) {
        validateRouteRequest(request);

        Airport origin = airportService.findAirportById(request.originId());
        Airport destination = airportService.findAirportById(request.destinationId());

        validateUniqueRoute(origin.getId(), destination.getId());

        Route route = RouteMapper.toEntity(request, origin, destination);
        Route savedRoute = routeRepository.save(route);

        return RouteMapper.toDto(savedRoute);
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