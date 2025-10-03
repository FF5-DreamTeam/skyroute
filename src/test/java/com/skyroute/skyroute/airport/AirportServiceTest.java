package com.skyroute.skyroute.airport;

import com.skyroute.skyroute.airport.dto.AirportCreateRequest;
import com.skyroute.skyroute.airport.dto.AirportResponse;
import com.skyroute.skyroute.airport.dto.AirportUpdateRequest;
import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.airport.repository.AirportRepository;
import com.skyroute.skyroute.airport.service.AirportServiceImpl;
import com.skyroute.skyroute.cloudinary.CloudinaryService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidUpdateRequestException;
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
    private AirportUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testAirport = createTestAirport();
        testImage = createTestImage();
        testCreateRequest = new AirportCreateRequest("MAD", "Madrid", testImage);
        testUpdateRequest = createTestUpdateRequest();
    }

    @Nested
    class CreateAirportTests {
        @Test
        void createAirport_shouldReturnAirportResponse_whenValidRequest() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            when(cloudinaryService.uploadImage(testImage)).thenReturn("http://example.com/image.jpg");
            when(airportRepository.save(any(Airport.class))).thenReturn(testAirport);

            AirportResponse result = airportService.createAirport(testCreateRequest);

            assertNotNull(result);
            assertEquals(testAirport.getId(), result.id());
            assertEquals(testAirport.getCode(), result.code());
            assertEquals(testAirport.getCity(), result.city());
            assertEquals(testAirport.getImageUrl(), result.imageUrl());

            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService).uploadImage(testImage);
            verify(airportRepository).save(any(Airport.class));
        }

        @Test
        void createAirport_shouldThrowEntityAlreadyExistsException_whenAirportCodeExists() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.of(testAirport));

            EntityAlreadyExistsException exception = assertThrows(
                    EntityAlreadyExistsException.class,
                    () -> airportService.createAirport(testCreateRequest));

            assertEquals("Airport code already exist: MAD", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService, never()).uploadImage(any());
            verify(airportRepository, never()).save(any());
        }

        @Test
        void createAirport_shouldThrowImageUploadException_whenImageIsNull() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            when(cloudinaryService.uploadImage(null))
                    .thenThrow(new ImageUploadException("Image file is required"));

            AirportCreateRequest requestWithNullImage = new AirportCreateRequest(
                    "MAD",
                    "Madrid",
                    null);

            ImageUploadException exception = assertThrows(
                    ImageUploadException.class,
                    () -> airportService.createAirport(requestWithNullImage));

            assertEquals("Image file is required", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(airportRepository, never()).save(any());
        }

        @Test
        void createAirport_shouldThrowImageUploadException_whenImageIsEmpty() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            MultipartFile emptyImage = mock(MultipartFile.class);
            lenient().when(emptyImage.isEmpty()).thenReturn(true);
            when(cloudinaryService.uploadImage(emptyImage))
                    .thenThrow(new ImageUploadException("Image file is required"));

            AirportCreateRequest emptyImageRequest = new AirportCreateRequest(
                    "MAD",
                    "Madrid",
                    emptyImage);

            ImageUploadException exception = assertThrows(
                    ImageUploadException.class,
                    () -> airportService.createAirport(emptyImageRequest));

            assertEquals("Image file is required", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(airportRepository, never()).save(any());
        }

        @Test
        void createAirport_shouldThrowImageUploadException_whenCloudinaryUploadFails() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            when(cloudinaryService.uploadImage(testImage))
                    .thenThrow(new ImageUploadException("Error uploading image: Upload failed"));

            ImageUploadException exception = assertThrows(
                    ImageUploadException.class,
                    () -> airportService.createAirport(testCreateRequest));

            assertEquals("Error uploading image: Upload failed", exception.getMessage());
            verify(airportRepository).findByCode("MAD");
            verify(cloudinaryService).uploadImage(testImage);
            verify(airportRepository, never()).save(any());
        }
    }

    @Nested
    class GetAllAirportsTests {
        @Test
        void getAllAirports_shouldReturnListOfAirports_whenAirportsExist() {
            List<Airport> airports = List.of(
                    testAirport,
                    createAirportWithData(2L, "BCN", "Barcelona", "http://example.com/bcn.jpg"));
            when(airportRepository.findAll()).thenReturn(airports);

            List<AirportResponse> result = airportService.getAllAirports();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("MAD", result.get(0).code());
            assertEquals("BCN", result.get(1).code());
            verify(airportRepository).findAll();
        }

        @Test
        void getAllAirports_shouldReturnEmptyList_whenNoAirportsExist() {
            when(airportRepository.findAll()).thenReturn(List.of());

            List<AirportResponse> result = airportService.getAllAirports();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(airportRepository).findAll();
        }
    }

    @Nested
    class GetAirportsByIdTests {
        @Test
        void getAirportsById_shouldReturnAirportsResponse_whenAirportExists() {
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
        void getAirportsById_shouldThrowEntityNotFoundException_whenAirportsDoesNotExist() {
            when(airportRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> airportService.getAirportById(99L));

            assertEquals("Airport not found with ID: 99", exception.getMessage());
            verify(airportRepository).findById(99L);
        }
    }

    @Nested
    class UpdateAirportTests {
        @Test
        void updateAirports_shouldReturnUpdatesAirport_whenValidRequestWithImage() throws IOException {
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(cloudinaryService.updateImage("https://res.cloudinary.com/demo/image/upload/v123456789/madrid.jpg",
                    testUpdateRequest.image())).thenReturn("http://example.com/new-image.jpg");
            when(airportRepository.save(testAirport)).thenReturn(testAirport);

            AirportResponse result = airportService.updateAirport(1L, testUpdateRequest);

            assertNotNull(result);
            assertEquals(testAirport.getId(), result.id());
            assertEquals("MAD", result.code());
            assertEquals("Madrid Updated", result.city());

            verify(airportRepository).findById(1L);
            verify(cloudinaryService).updateImage("https://res.cloudinary.com/demo/image/upload/v123456789/madrid.jpg",
                    testUpdateRequest.image());
            verify(airportRepository).save(testAirport);
        }

        @Test
        void updateAirports_shouldReturnUpdatesAirport_whenValidRequestWithoutImage() throws IOException {
            AirportUpdateRequest testUpdateRequest = new AirportUpdateRequest(
                    "MAD",
                    "Madrid Updated",
                    null);
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(airportRepository.save(testAirport)).thenReturn(testAirport);

            AirportResponse result = airportService.updateAirport(1L, testUpdateRequest);

            assertNotNull(result);
            assertEquals(testAirport.getId(), result.id());
            assertEquals("MAD", result.code());
            assertEquals("Madrid Updated", result.city());

            verify(airportRepository).findById(1L);
            verify(cloudinaryService, never()).uploadImage(any());
            verify(airportRepository).save(testAirport);
        }

        @Test
        void updateAirport_shouldReturnUpdatedAirport_whenPartialUpdate() {
            AirportUpdateRequest partialRequest = new AirportUpdateRequest(
                    null,
                    "Madrid Partial",
                    null);
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(airportRepository.save(testAirport)).thenReturn(testAirport);

            AirportResponse result = airportService.updateAirport(1L, partialRequest);

            assertNotNull(result);
            assertEquals(testAirport.getCode(), result.code());
            assertEquals("Madrid Partial", testAirport.getCity());
            verify(airportRepository).findById(1L);
            verify(airportRepository).save(testAirport);
        }

        @Test
        void updateAirport_shouldThrowEntityNotFoundException_whenAirportDoesNotExist() throws IOException {
            AirportUpdateRequest testUpdateRequest = new AirportUpdateRequest(
                    "MAD",
                    "Madrid Updated",
                    createTestImage());
            when(airportRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> airportService.updateAirport(99L, testUpdateRequest));

            assertEquals("Airport not found with ID: 99", exception.getMessage());
            verify(airportRepository).findById(99L);
            verify(cloudinaryService, never()).uploadImage(any());
            verify(airportRepository, never()).save(any());
        }

        @Test
        void updateAirport_shouldThrowInvalidUpdateRequestException_whenNoFieldsProvided() throws IOException {
            AirportUpdateRequest emptyRequest = new AirportUpdateRequest(
                    null,
                    null,
                    null);
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));

            InvalidUpdateRequestException exception = assertThrows(
                    InvalidUpdateRequestException.class,
                    () -> airportService.updateAirport(1L, emptyRequest));

            assertEquals("At least one field must be provided for update", exception.getMessage());
            verify(airportRepository).findById(1L);
            verify(cloudinaryService, never()).uploadImage(any());
            verify(airportRepository, never()).save(any());
        }

        @Test
        void updateAirport_shouldAllowImageOnlyUpdate() throws IOException {
            MultipartFile testImage = createTestImage();
            AirportUpdateRequest emptyFieldsRequest = new AirportUpdateRequest(
                    null,
                    null,
                    testImage);

            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(cloudinaryService.updateImage("https://res.cloudinary.com/demo/image/upload/v123456789/madrid.jpg",
                    testImage)).thenReturn("http://example.com/new-image.jpg");
            when(airportRepository.save(testAirport)).thenReturn(testAirport);

            AirportResponse result = airportService.updateAirport(1L, emptyFieldsRequest);

            assertNotNull(result);
            verify(airportRepository).findById(1L);
            verify(cloudinaryService).updateImage("https://res.cloudinary.com/demo/image/upload/v123456789/madrid.jpg",
                    testImage);
            verify(airportRepository).save(testAirport);
        }

        @Test
        void updateAirport_shouldThrowRuntimeException_whenImageUploadFailsOnUpdate() throws IOException {
            MultipartFile testImage = createTestImage();
            AirportUpdateRequest testUpdateRequest = new AirportUpdateRequest(
                    "MAD",
                    "Madrid Updated",
                    testImage);

            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(cloudinaryService.updateImage(anyString(), any(MultipartFile.class)))
                    .thenThrow(new ImageUploadException("Error uploading image: Upload failed"));

            ImageUploadException exception = assertThrows(
                    ImageUploadException.class,
                    () -> airportService.updateAirport(1L, testUpdateRequest));

            assertEquals("Error uploading image: Upload failed", exception.getMessage());
            verify(airportRepository).findById(1L);
            verify(cloudinaryService).updateImage(anyString(), eq(testImage));
            verify(airportRepository, never()).save(any());
        }

        @Test
        void updateAirport_shouldHandleEmptyImage() throws IOException {
            MultipartFile emptyImage = mock(MultipartFile.class);
            when(emptyImage.isEmpty()).thenReturn(true);
            AirportUpdateRequest testUpdateRequest = new AirportUpdateRequest(
                    "MAD",
                    "Madrid Updated",
                    emptyImage);

            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(airportRepository.save(testAirport)).thenReturn(testAirport);

            AirportResponse result = airportService.updateAirport(1L, testUpdateRequest);

            assertNotNull(result);
            verify(airportRepository).findById(1L);
            verify(cloudinaryService, never()).uploadImage(any());
            verify(airportRepository).save(testAirport);
        }

        @Test
        void updateAirport_shouldThrowInvalidUpdateRequestException_whenImageIsEmptyAndNoFieldsProvided()
                throws IOException {
            MultipartFile emptyImage = mock(MultipartFile.class);
            when(emptyImage.isEmpty()).thenReturn(true);

            AirportUpdateRequest emptyRequest = new AirportUpdateRequest(
                    null,
                    null,
                    emptyImage);

            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));

            InvalidUpdateRequestException exception = assertThrows(
                    InvalidUpdateRequestException.class,
                    () -> airportService.updateAirport(1L, emptyRequest));

            assertEquals("At least one field must be provided for update", exception.getMessage());
            verify(airportRepository).findById(1L);
            verify(cloudinaryService, never()).uploadImage(any());
            verify(airportRepository, never()).save(any());
        }

        @Test
        void updateAirports_shouldNotCallDeleteOldImage_whenExistingImageUrlIsNull() throws IOException {
            Airport airportWithNullImage = createAirportWithData(1L, "LPA", "Las Palmas", null);

            MultipartFile newImage = createTestImage();
            AirportUpdateRequest updateRequest = new AirportUpdateRequest(
                    "LPA",
                    "Las Palmas Updated",
                    newImage);

            when(airportRepository.findById(1L)).thenReturn(Optional.of(airportWithNullImage));
            when(cloudinaryService.updateImage(null, newImage)).thenReturn("http://example.com/new-image.jpg");
            when(airportRepository.save(any(Airport.class))).thenReturn(airportWithNullImage);

            airportService.updateAirport(1L, updateRequest);

            verify(cloudinaryService).updateImage(null, newImage);
            verify(airportRepository).save(any(Airport.class));
        }
    }

    @Nested
    class DeleteAirportTests {
        @Test
        void deleteAirport_shouldDeleteAirport_whenAirportExists() throws IOException {
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            doNothing().when(cloudinaryService).deleteImageByUrl(anyString());
            doNothing().when(airportRepository).delete(testAirport);

            assertDoesNotThrow(() -> airportService.deleteAirport(1L));

            verify(airportRepository).findById(1L);
            verify(cloudinaryService).deleteImageByUrl(anyString());
            verify(airportRepository).delete(testAirport);
        }

        @Test
        void deleteAirport_shouldThrowEntityNotFoundException_whenAirportDoesNotExist() throws IOException {
            when(airportRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> airportService.deleteAirport(99L));

            assertEquals("Airport not found with ID: 99", exception.getMessage());
            verify(airportRepository).findById(99L);
            verify(cloudinaryService, never()).deleteImageByUrl(anyString());
            verify(airportRepository, never()).delete(any());
        }

        @Test
        void deleteAirport_shouldContinueDeletion_whenCloudinaryDeleteFails() throws IOException {
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            doThrow(new RuntimeException("Could not delete image: Delete failed")).when(cloudinaryService)
                    .deleteImageByUrl(anyString());

            assertDoesNotThrow(() -> airportService.deleteAirport(1L));

            verify(airportRepository).findById(1L);
            verify(cloudinaryService).deleteImageByUrl(anyString());
            verify(airportRepository).delete(testAirport);
        }
    }

    @Nested
    class ImageHandlingTests {

        @Test
        void uploadImage_shouldExtractPublicIdCorrectly_forCloudinaryUrl() throws IOException {
            String cloudinaryUrl = "https://res.cloudinary.com/demo/image/upload/v123456789/sample.jpg";
            Airport airportWithCloudinaryImage = createAirportWithData(1L, "MAD", "Madrid", cloudinaryUrl);

            when(airportRepository.findById(1L)).thenReturn(Optional.of(airportWithCloudinaryImage));
            when(cloudinaryService.updateImage(eq(cloudinaryUrl), any(MultipartFile.class)))
                    .thenReturn("http://example.com/new-image.jpg");
            when(airportRepository.save(airportWithCloudinaryImage)).thenReturn(airportWithCloudinaryImage);

            airportService.updateAirport(1L, testUpdateRequest);

            verify(cloudinaryService).updateImage(eq(cloudinaryUrl), any(MultipartFile.class));
        }

        @Test
        void updateAirportImage_shouldReplaceOldImageWithNew() throws IOException {
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(cloudinaryService.updateImage(anyString(), any(MultipartFile.class)))
                    .thenReturn("http://example.com/new-image.jpg");
            when(airportRepository.save(testAirport)).thenReturn(testAirport);

            airportService.updateAirport(1L, testUpdateRequest);

            verify(cloudinaryService).updateImage(anyString(), any(MultipartFile.class));
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void createAirport_shouldHandleSpecialCharactersInImageUrl() throws IOException {
            when(airportRepository.findByCode("MAD")).thenReturn(Optional.empty());
            when(cloudinaryService.uploadImage(testImage))
                    .thenReturn("http://example.com/image-with-special_chars.jpg");
            when(airportRepository.save(any(Airport.class))).thenReturn(testAirport);

            AirportResponse result = airportService.createAirport(testCreateRequest);

            assertNotNull(result);
            verify(cloudinaryService).uploadImage(testImage);
        }

        @Test
        void updateAirport_shouldPreserveExistingValues_whenOnlyPartialUpdate() {
            String originalCode = testAirport.getCode();
            String originalImageUrl = testAirport.getImageUrl();

            AirportUpdateRequest partialRequest = new AirportUpdateRequest(null, "New City", null);
            when(airportRepository.findById(1L)).thenReturn(Optional.of(testAirport));
            when(airportRepository.save(testAirport)).thenReturn(testAirport);

            AirportResponse result = airportService.updateAirport(1L, partialRequest);

            assertEquals(originalCode, result.code());
            assertEquals(originalImageUrl, result.imageUrl());
            assertEquals("New City", result.city());
        }

        @Test
        void deleteOldImage_shouldHandleComplexCloudinaryUrls() throws IOException {
            String complexUrl = "https://res.cloudinary.com/demo/image/upload/v1234567890/folder/subfolder/image_name.jpg";
            Airport airportWithComplexUrl = createAirportWithData(1L, "MAD", "Madrid", complexUrl);

            when(airportRepository.findById(1L)).thenReturn(Optional.of(airportWithComplexUrl));
            doNothing().when(cloudinaryService).deleteImageByUrl(complexUrl);
            doNothing().when(airportRepository).delete(airportWithComplexUrl);

            airportService.deleteAirport(1L);

            verify(cloudinaryService).deleteImageByUrl(complexUrl);
        }
    }

    private Airport createAirportWithData(Long id, String code, String city, String imageUrl) {
        return Airport.builder()
                .id(id)
                .code(code)
                .city(city)
                .imageUrl(imageUrl)
                .build();
    }

    private Airport createTestAirport() {
        return Airport.builder()
                .id(1L)
                .code("MAD")
                .city("Madrid")
                .imageUrl("https://res.cloudinary.com/demo/image/upload/v123456789/madrid.jpg")
                .build();
    }

    private MultipartFile createTestImage() {
        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
    }

    private AirportUpdateRequest createTestUpdateRequest() {
        MultipartFile imageForUpdate = new MockMultipartFile(
                "image",
                "test-update.jpg",
                "image/jpeg",
                "updated image content".getBytes());
        return new AirportUpdateRequest("MAD", "Madrid Updated", imageForUpdate);
    }
}