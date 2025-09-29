package com.skyroute.skyroute.aircraft.service;

import com.skyroute.skyroute.aircraft.dto.AircraftRequest;
import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.dto.AircraftMapper;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftDeletionException;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;

    @Override
    @Transactional
    public AircraftResponse createAircraft(AircraftRequest request) {
        log.info("Creating aircraft with model: {} and manufacturer: {}", request.model(), request.manufacturer());

        Aircraft aircraft = AircraftMapper.toEntity(request);
        Aircraft saved = aircraftRepository.save(aircraft);

        log.info("Aircraft created successfully with id: {}", saved.getId());
        return AircraftMapper.toDto(saved);
    }

    @Override
    public AircraftResponse getAircraftById(Long id) {
        log.debug("Fetching aircraft with id: {}", id);

        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("Aircraft not found with id: " + id));

        return AircraftMapper.toDto(aircraft);
    }

    @Override
    public List<AircraftResponse> getAllAircrafts() {
        log.debug("Fetching all aircrafts");

        List<Aircraft> aircrafts = aircraftRepository.findAll();
        log.info("Found {} aircrafts", aircrafts.size());

        return aircrafts.stream()
                .map(AircraftMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AircraftResponse updateAircraft(Long id, AircraftRequest request) {
        log.info("Updating aircraft with id: {}", id);

        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("Aircraft not found with id: " + id));

        aircraft.setModel(request.model());
        aircraft.setManufacturer(request.manufacturer());
        aircraft.setCapacity(request.capacity());

        Aircraft updated = aircraftRepository.save(aircraft);
        log.info("Aircraft updated successfully with id: {}", updated.getId());

        return AircraftMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteAircraft(Long id) {
        log.info("Attempting to delete aircraft with id: {}", id);

        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("Aircraft not found with id: " + id));

        if (aircraft.getFlights() != null && !aircraft.getFlights().isEmpty()) {
            log.warn("Cannot delete aircraft with id: {} - has {} associated flights", id, aircraft.getFlights().size());
            throw new AircraftDeletionException("Cannot delete aircraft with associated flights. Found " + aircraft.getFlights().size() + " flight(s)");
        }

        try {
            aircraftRepository.delete(aircraft);
            log.info("Aircraft deleted successfully with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting aircraft with id: {}", id, e);
            throw new AircraftDeletionException("Error deleting aircraft with id: " + id, e);
        }
    }

    @Override
    public Aircraft findById(Long id) {
        return aircraftRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aircraft not found with id: " + id));
    }

    @Override
    public boolean existsById(Long id) {
        return aircraftRepository.existsById(id);
    }
}