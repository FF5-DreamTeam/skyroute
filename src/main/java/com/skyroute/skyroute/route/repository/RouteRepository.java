package com.skyroute.skyroute.route.repository;

import com.skyroute.skyroute.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByOriginIdAndDestinationId(Long originId, Long destinationId);
}