package com.skyroute.skyroute.airport;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.repository.AirportRepository;
import com.skyroute.skyroute.airport.service.AirportServiceImpl;
import com.skyroute.skyroute.cloudinary.CloudinaryService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AirportServiceTest {
    @Mock
    private AirportRepository airportRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private AirportServiceImpl airportService;

    private Airport testAirport;
    private AirportCreateRequest testCreateRequest;
    private MultipartFile testImage;
    private Map<String, Object> testCloudinaryResult;

    @BeforeEach
    void setUp(){
        testAirport = createTestAirport();
        testImage = createTestImage();
        testCreateRequest = new AirportCreateRequest("MAD", "Madrid", testImage);
        testCloudinaryResult = Map.of("secure_curl", "http://example.com/image.jpg");
    }

    @Nested
    class CreateAirportTests {
        @Test
        void createAirport_shouldReturnAirportResponse_whenValidRequest() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            when(cloudinaryService.uploadFile(testImage)).thenReturn(testCloudinaryResult);
            when(airportRepository.save(any(Airport.class))).thenReturn(testAirport);

            AirportResponse result = airportService.createAirport(testCreateRequest);

            assertNotNull(result);
            assertEquals(testAirport.getId(), result.id());
            assertEquals(testAirport.getCode(), result.code());
            assertEquals(testAirport.getCity(), result.city());
            assertEquals(testAirport.getImageUrl(), result.imageUrl());

            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService).uploadFile(testImage);
            verify(airportRepository).save(any(Airport.class));
        }

        @Test
        void createAirport_shouldThrowEntityAlreadyExistsException_whenAirportCodeExists() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.of(testAirport));

            EntityAlreadyExistsException exception = assertThrows(
                    EntityAlreadyExistsException.class,
                    () -> airportService.createAirport(testCreateRequest)
            );

            assertEquals("Airport code already exist: MAD", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService, never()).uploadFile(any());
            verify(airportRepository, never()).save(any());
        }

        @Test
        void createAirport_shouldThrowImageUploadException_whenImageIsNull() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());

            AirportCreateRequest requestWithNullImage = new AirportCreateRequest(
                    "MAD",
                    "Madrid",
                    null
            );

            ImageUploadException exception = assertThrows(
                    ImageUploadException.class,
                    () -> airportService.createAirport(requestWithNullImage)
            );

            assertEquals("Image file is required", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService, never()).uploadFile(any());
            verify(airportRepository, never()).save(any());
        }

        @Test
        void createAirport_shouldThrowImageUploadException_whenImageIsEmpty() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            MultipartFile emptyImage = mock(MultipartFile.class);
            when(emptyImage.isEmpty()).thenReturn(true);

            AirportCreateRequest emptyImageRequest = new AirportCreateRequest(
                    "MAD",
                    "Madrid",
                    emptyImage
            );

            ImageUploadException exception = assertThrows(
                    ImageUploadException.class,
                    () -> airportService.createAirport(emptyImageRequest)
            );

            assertEquals("Image file is required", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService, never()).uploadFile(any());
            verify(airportRepository, never()).save(any());
        }

        @Test
        void createAirport_shouldThrowImageUploadException_whenCloudinaryUploadFails() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            when(cloudinaryService.uploadFile(testImage)).thenThrow(new IOException("Upload failed"));

            ImageUploadException exception = assertThrows(
                    ImageUploadException.class,
                    () -> airportService.createAirport(testCreateRequest)
            );

            assertEquals("Error uploading image: Upload failed", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService).uploadFile(testImage);
            verify(airportRepository, never()).save(any());
        }
    }

    @Nested
    class  GetAllAirportsTests{
        @Test
        void getAllAirports_shouldReturnListOfAirports_whenAirportsExist(){
            List<Airport> airports = List.of(
                    testAirport,
                    createAirportWithData(2L, "BCN", "Barcelona", "http://example.com/bcn.jpg")
            );
            when(airportRepository.findAll()).thenReturn(airports);

            List<AirportResponse> result = airportService.getAllAirports();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("MAD", result.get(0).code());
            assertEquals("BCN", result.get(1).code());
            verify(airportRepository).findAll();
        }

        @Test
        void getAllAirports_shouldReturnEmptyList_whenNoAirportsExist(){
            when(airportRepository.findAll()).thenReturn(List.of());

            List<AirportResponse> result = airportService.getAllAirports();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(airportRepository).findAll();
        }
    }

    @Nested
    class GetAirportsByIdTests{
        @Test
        void getAirportsById_shouldReturnAirportsResponse_whenAirportExists(){
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));

            AirportResponse result = airportService.getAirportById(1L);

            assertNotNull(result);
            assertEquals(testAirport.getId(), result.id());
            assertEquals(testAirport.getCode(), result.code());
            assertEquals(testAirport.getCity(), result.city());
            assertEquals(testAirport.getImageUrl(), result.imageUrl());
            verify(airportRepository).findById(1L);
        }

        @Test
        void getAirportsById_shouldThrowEntityNotFoundException_whenAirportsDoesNotExist(){
            when(airportRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () ->airportService.getAirportById(99L)
            );

            assertEquals("Airport not found with ID: 99", exception.getMessage());
            verify(airportRepository).findById(99L);
        }
    }

    private Airport createAirportWithData(Long id, String code, String city, String imageUrl){
        return Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl(imageUrl)
                .build();
    }

    private Airport createTestAirport(){
        return Airport.builder()
                .id(1L)
                .code("MAD")
                .city("Madrid")
                .imageUrl("https://res.cloudinary.com/demo/image/upload/v123456789/madrid.jpg")
                .build();
    }

    private MultipartFile createTestImage(){
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }
}