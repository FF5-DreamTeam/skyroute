package com.skyroute.skyroute.airport.service;

import com.skyroute.skyroute.airport.dto.AirportResponse;

import java.util.List;

public interface AirportService {
    List<AirportResponse> getAllAirports();
    AirportResponse getAirportById(Long id);
}
