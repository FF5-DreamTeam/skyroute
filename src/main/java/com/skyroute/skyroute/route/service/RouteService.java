package com.skyroute.skyroute.route.service;

import com.skyroute.skyroute.route.dto.RouteRequest;
import com.skyroute.skyroute.route.dto.RouteResponse;

public interface RouteService {
    RouteResponse createRoute(RouteRequest request);
}