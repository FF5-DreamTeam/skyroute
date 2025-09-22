package com.skyroute.skyroute.aircraft.service;

import com.skyroute.skyroute.aircraft.dto.AircraftRequest;
import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftDeletionException;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;

    @Override
    public AircraftResponse createAircraft(AircraftRequest request) {
        Aircraft aircraft = AircraftMapper.toEntity(request);
        Aircraft saved = aircraftRepository.save(aircraft);
        return AircraftMapper.toDto(saved);
    }

    @Override
    public AircraftResponse getAircraftById(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aircraft not found with id " + id));
        return AircraftMapper.toDto(aircraft);
    }

    @Override
    public List<AircraftResponse> getAllAircrafts() {
        return aircraftRepository.findAll().stream()
                .map(AircraftMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AircraftResponse updateAircraft(Long id, AircraftRequest request) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aircraft not found with id " + id));

        aircraft.setModel(request.model());
        aircraft.setManufacturer(request.manufacturer());
        aircraft.setCapacity(request.capacity());

        Aircraft updated = aircraftRepository.save(aircraft);
        return AircraftMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteAircraft(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("Aircraft not found with id: " + id));

        if (aircraft.getFlights() != null && !aircraft.getFlights().isEmpty()) {
            throw new AircraftDeletionException("Cannot delete aircraft with associated flights");
        }

        try {
            aircraftRepository.delete(aircraft);
        } catch (Exception e) {
            throw new AircraftDeletionException("Error deleting aircraft with id: " + id, e);
        }
    }


}
