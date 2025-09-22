package com.skyroute.skyroute.airport.service;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AirportService {
    AirportResponse createAirport(AirportCreateRequest request, MultipartFile image);
    List<AirportResponse> getAllAirports();
    AirportResponse getAirportById(Long id);
}