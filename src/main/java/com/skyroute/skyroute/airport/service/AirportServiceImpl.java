package com.skyroute.skyroute.airport.service;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportMapper;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.repository.AirportRepository;
import com.skyroute.skyroute.cloudinary.CloudinaryService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {
    private final AirportRepository airportRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public AirportResponse createAirport(AirportCreateRequest request, MultipartFile image) {
        if (airportRepository.findByCode(request.code()).isPresent()){
            throw new EntityAlreadyExistsException("Airport code already exist: " + request.code());
        }

        String imageUrl = uploadImage(image);

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

    private String uploadImage(MultipartFile image){
        if (image == null || image.isEmpty()){
            throw new ImageUploadException("Image file is required");
        }
        try{
            Map result = cloudinaryService.uploadFile(image);
            return (String) result.get("secure_url");
        } catch (IOException exception){
            throw new ImageUploadException("Error uploading image: " + exception.getMessage());
        }
    }
}