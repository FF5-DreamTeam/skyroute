package com.skyroute.skyroute.aircraft.dto;
import com.skyroute.skyroute.aircraft.entity.Aircraft;

public class AircraftMapper {

    public static Aircraft toEntity(AircraftRequest dto) {
        Aircraft aircraft = new Aircraft();
        aircraft.setModel(dto.model());
        aircraft.setManufacturer(dto.manufacturer());
        aircraft.setCapacity(dto.capacity());
        return aircraft;
    }

    public static AircraftResponse toDto (Aircraft entity) {
        return new AircraftResponse(
                entity.getId(),
                entity.getModel(),
                entity.getManufacturer(),
                entity.getCapacity()
        );
    }
}
