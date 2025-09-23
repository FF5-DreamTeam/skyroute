package com.skyroute.skyroute.aircraft;

import com.skyroute.skyroute.aircraft.dto.AircraftRequest;
import com.skyroute.skyroute.aircraft.dto.AircraftResponse;
import com.skyroute.skyroute.aircraft.entity.Aircraft;
import com.skyroute.skyroute.aircraft.repository.AircraftRepository;
import com.skyroute.skyroute.aircraft.service.AircraftServiceImpl;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftDeletionException;
import com.skyroute.skyroute.shared.exception.custom_exception.AircraftNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AircraftServiceImplTest {

    @Mock
    private AircraftRepository aircraftRepository;

    @InjectMocks
    private AircraftServiceImpl aircraftService;

    private AircraftRequest aircraftRequest;
    private Aircraft aircraft;
    private AircraftResponse expectedResponse;

    @BeforeEach
    void setUp() {
        aircraftRequest = new AircraftRequest("Boeing 737", "Boeing", 180);

        aircraft = Aircraft.builder()
                .id(1L)
                .model("Boeing 737")
                .manufacturer("Boeing")
                .capacity(180)
                .flights(Collections.emptyList())
                .build();

        expectedResponse = new AircraftResponse(1L, "Boeing 737", "Boeing", 180);
    }

    @Test
    void createAircraft_ShouldReturnAircraftResponse_WhenValidRequest() {
        // Given
        Aircraft savedAircraft = Aircraft.builder()
                .id(1L)
                .model("Boeing 737")
                .manufacturer("Boeing")
                .capacity(180)
                .build();

        when(aircraftRepository.save(any(Aircraft.class))).thenReturn(savedAircraft);

        // When
        AircraftResponse result = aircraftService.createAircraft(aircraftRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.model()).isEqualTo("Boeing 737");
        assertThat(result.manufacturer()).isEqualTo("Boeing");
        assertThat(result.capacity()).isEqualTo(180);

        verify(aircraftRepository, times(1)).save(any(Aircraft.class));
    }

    @Test
    void getAircraftById_ShouldReturnAircraftResponse_WhenAircraftExists() {
        // Given
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));

        // When
        AircraftResponse result = aircraftService.getAircraftById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.model()).isEqualTo("Boeing 737");
        assertThat(result.manufacturer()).isEqualTo("Boeing");
        assertThat(result.capacity()).isEqualTo(180);

        verify(aircraftRepository, times(1)).findById(1L);
    }

    @Test
    void getAircraftById_ShouldThrowException_WhenAircraftNotFound() {
        // Given
        when(aircraftRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> aircraftService.getAircraftById(999L))
                .isInstanceOf(AircraftNotFoundException.class)
                .hasMessageContaining("Aircraft not found with id: 999");

        verify(aircraftRepository, times(1)).findById(999L);
    }

    @Test
    void getAllAircrafts_ShouldReturnListOfAircrafts_WhenAircraftsExist() {
        // Given
        Aircraft aircraft2 = Aircraft.builder()
                .id(2L)
                .model("Airbus A320")
                .manufacturer("Airbus")
                .capacity(150)
                .build();

        List<Aircraft> aircrafts = Arrays.asList(aircraft, aircraft2);
        when(aircraftRepository.findAll()).thenReturn(aircrafts);

        // When
        List<AircraftResponse> result = aircraftService.getAllAircrafts();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).model()).isEqualTo("Boeing 737");
        assertThat(result.get(1).model()).isEqualTo("Airbus A320");

        verify(aircraftRepository, times(1)).findAll();
    }

    @Test
    void getAllAircrafts_ShouldReturnEmptyList_WhenNoAircraftsExist() {
        // Given
        when(aircraftRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<AircraftResponse> result = aircraftService.getAllAircrafts();

        // Then
        assertThat(result).isEmpty();
        verify(aircraftRepository, times(1)).findAll();
    }

    @Test
    void updateAircraft_ShouldReturnUpdatedAircraftResponse_WhenAircraftExists() {
        // Given
        AircraftRequest updateRequest = new AircraftRequest("Boeing 737-800", "Boeing", 189);
        Aircraft updatedAircraft = Aircraft.builder()
                .id(1L)
                .model("Boeing 737-800")
                .manufacturer("Boeing")
                .capacity(189)
                .build();

        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        when(aircraftRepository.save(any(Aircraft.class))).thenReturn(updatedAircraft);

        // When
        AircraftResponse result = aircraftService.updateAircraft(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.model()).isEqualTo("Boeing 737-800");
        assertThat(result.capacity()).isEqualTo(189);

        verify(aircraftRepository, times(1)).findById(1L);
        verify(aircraftRepository, times(1)).save(any(Aircraft.class));
    }

    @Test
    void updateAircraft_ShouldThrowException_WhenAircraftNotFound() {
        // Given
        AircraftRequest updateRequest = new AircraftRequest("Boeing 737-800", "Boeing", 189);
        when(aircraftRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> aircraftService.updateAircraft(999L, updateRequest))
                .isInstanceOf(AircraftNotFoundException.class)
                .hasMessageContaining("Aircraft not found with id: 999");

        verify(aircraftRepository, times(1)).findById(999L);
        verify(aircraftRepository, never()).save(any(Aircraft.class));
    }

    @Test
    void deleteAircraft_ShouldDeleteSuccessfully_WhenAircraftExistsAndHasNoFlights() {
        // Given
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        doNothing().when(aircraftRepository).delete(aircraft);

        // When
        aircraftService.deleteAircraft(1L);

        // Then
        verify(aircraftRepository, times(1)).findById(1L);
        verify(aircraftRepository, times(1)).delete(aircraft);
    }

    @Test
    void deleteAircraft_ShouldThrowException_WhenAircraftNotFound() {
        // Given
        when(aircraftRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> aircraftService.deleteAircraft(999L))
                .isInstanceOf(AircraftNotFoundException.class)
                .hasMessageContaining("Aircraft not found with id: 999");

        verify(aircraftRepository, times(1)).findById(999L);
        verify(aircraftRepository, never()).delete(any(Aircraft.class));
    }

    @Test
    void deleteAircraft_ShouldThrowException_WhenAircraftHasAssociatedFlights() {
        // Given
        Flight flight = new Flight();
        aircraft.setFlights(Arrays.asList(flight));

        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));

        // When & Then
        assertThatThrownBy(() -> aircraftService.deleteAircraft(1L))
                .isInstanceOf(AircraftDeletionException.class)
                .hasMessageContaining("Cannot delete aircraft with associated flights");

        verify(aircraftRepository, times(1)).findById(1L);
        verify(aircraftRepository, never()).delete(any(Aircraft.class));
    }

    @Test
    void deleteAircraft_ShouldThrowException_WhenRepositoryThrowsException() {
        // Given
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        doThrow(new RuntimeException("Database error")).when(aircraftRepository).delete(aircraft);

        // When & Then
        assertThatThrownBy(() -> aircraftService.deleteAircraft(1L))
                .isInstanceOf(AircraftDeletionException.class)
                .hasMessageContaining("Error deleting aircraft with id: 1");

        verify(aircraftRepository, times(1)).findById(1L);
        verify(aircraftRepository, times(1)).delete(aircraft);
    }
}