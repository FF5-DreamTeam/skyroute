package com.skyroute.skyroute.airport.repository;

import com.skyroute.skyroute.airport.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCode(String code);
}
