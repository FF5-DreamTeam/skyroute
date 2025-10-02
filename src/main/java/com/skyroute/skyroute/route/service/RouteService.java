package com.skyroute.skyroute.route.service;

import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;
import com.skyroute.skyroute.route.entity.Route;

import java.util.List;

public interface RouteService {
    RouteResponse createRoute(RouteRequest request);
    List<RouteResponse> getAllRoutes();
    RouteResponse getRouteById(Long id);
    RouteResponse updateRoute(Long id, RouteRequest request);
    void deleteRoute(Long id);
    Route findRouteById(Long id);
}