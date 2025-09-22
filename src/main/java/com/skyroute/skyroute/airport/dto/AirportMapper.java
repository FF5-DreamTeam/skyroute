package com.skyroute.skyroute.airport.dto;

import com.skyroute.skyroute.airport.entity.Airport;

public class AirportMapper {
    public static Airport toEntity(AirportRequest dto) {
        return Airport.builder()
                .code(dto.code())
                .city(dto.city())
                .imageUrl(dto.imageUrl())
                .build();
    }

    public static AirportResponse toDto(Airport airport) {
        return new AirportResponse(
                airport.getId(),
                airport.getCode(),
                airport.getCity(),
                airport.getImageUrl()
        );
    }

    public static Airport toEntityFromCreate(AirportCreateRequest dto, String imageUrl) {
        return Airport.builder()
                .code(dto.code())
                .city(dto.city())
                .imageUrl(imageUrl)
                .build();
    }
}