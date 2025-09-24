package com.skyroute.skyroute.airport.service;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.dto.AirportUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AirportService {
    AirportResponse createAirport(AirportCreateRequest request);
    List<AirportResponse> getAllAirports();
    AirportResponse getAirportById(Long id);
    AirportResponse updateAirport(Long id, AirportUpdateRequest request, MultipartFile image);
    void deleteAirport(Long id);
}