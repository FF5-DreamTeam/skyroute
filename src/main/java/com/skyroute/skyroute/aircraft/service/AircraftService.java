package com.skyroute.skyroute.aircraft.service;

import com.skyroute.skyroute.aircraft.dto.AircraftRequest;
import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.aircraft.entity.Aircraft;

import java.util.List;

public interface AircraftService {

    AircraftResponse createAircraft(AircraftRequest request);

    AircraftResponse getAircraftById(Long id);

    List<AircraftResponse> getAllAircrafts();

    AircraftResponse updateAircraft(Long id, AircraftRequest request);

    void deleteAircraft(Long id);

    Aircraft findById(Long id);

    boolean existsById(Long id);
}