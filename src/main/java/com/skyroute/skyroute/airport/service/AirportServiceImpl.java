package com.skyroute.skyroute.airport.service;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportMapper;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.dto.AirportUpdateRequest;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.repository.AirportRepository;
import com.skyroute.skyroute.cloudinary.CloudinaryService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidUpdateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {
    private final AirportRepository airportRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public AirportResponse createAirport(AirportCreateRequest request) {
        if (airportRepository.findByCode(request.code()).isPresent()) {
            throw new EntityAlreadyExistsException("Airport code already exist: " + request.code());
        }

        String imageUrl = cloudinaryService.uploadImage(request.image());

        Airport airport = AirportMapper.toEntityFromCreate(request, imageUrl);

        Airport saved = airportRepository.save(airport);
        return AirportMapper.toDto(saved);
    }

    @Override
    public List<AirportResponse> getAllAirports() {
        return airportRepository.findAll()
                .stream()
                .map(AirportMapper::toDto)
                .toList();
    }

    @Override
    public AirportResponse getAirportById(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with ID: " + id));
        return AirportMapper.toDto(airport);
    }

    @Override
    @Transactional
    public AirportResponse updateAirport(Long id, AirportUpdateRequest request) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with ID: " + id));

        if (!request.hasAnyField() && (request.image() == null || request.image().isEmpty())) {
            throw new InvalidUpdateRequestException("At least one field must be provided for update");
        }

        if (request.image() != null && !request.image().isEmpty()) {
            String newImageUrl = cloudinaryService.updateImage(airport.getImageUrl(), request.image());
            airport.setImageUrl(newImageUrl);
        }

        AirportMapper.toEntityFromUpdate(airport, request);

        Airport updated = airportRepository.save(airport);
        return AirportMapper.toDto(updated);
    }

    @Override
    public void deleteAirport(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with ID: " + id));

        if (airport.getImageUrl() != null && !airport.getImageUrl().isEmpty()) {
            try {
                cloudinaryService.deleteImageByUrl(airport.getImageUrl());
            } catch (Exception e) {
                System.err.println("Failed to delete airport image from Cloudinary: " + e.getMessage());
            }
        }

        airportRepository.delete(airport);
    }

    @Override
    public Airport findAirportById(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with ID: " + id));
    }
}