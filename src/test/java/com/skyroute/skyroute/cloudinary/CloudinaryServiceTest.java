package com.skyroute.skyroute.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        lenient().when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadImage_WithValidImage_ShouldReturnImageUrl() throws IOException {
        byte[] imageBytes = "test image".getBytes();
        Map<String, Object> uploadResult = Map.of("secure_url", "https://cloudinary.com/test-image.jpg");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(imageBytes);
        when(uploader.upload(any(byte[].class), any())).thenReturn(uploadResult);

        String imageUrl = cloudinaryService.uploadImage(multipartFile);

        assertThat(imageUrl).isEqualTo("https://cloudinary.com/test-image.jpg");
        verify(uploader).upload(imageBytes, Map.of());
    }

    @Test
    void uploadImage_WithNullImage_ShouldThrowImageUploadException() throws IOException {
        assertThatThrownBy(() -> cloudinaryService.uploadImage(null))
                .isInstanceOf(ImageUploadException.class)
                .hasMessageContaining("Image file is required");

        verify(uploader, never()).upload(any(), any());
    }

    @Test
    void uploadImage_WithEmptyImage_ShouldThrowImageUploadException() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> cloudinaryService.uploadImage(multipartFile))
                .isInstanceOf(ImageUploadException.class)
                .hasMessageContaining("Image file is required");

        verify(uploader, never()).upload(any(), any());
    }

    @Test
    void uploadImage_WhenIOExceptionOccurs_ShouldThrowImageUploadException() throws IOException {
        byte[] imageBytes = "test image".getBytes();

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(imageBytes);
        when(uploader.upload(any(byte[].class), any())).thenThrow(new IOException("Upload failed"));

        assertThatThrownBy(() -> cloudinaryService.uploadImage(multipartFile))
                .isInstanceOf(ImageUploadException.class)
                .hasMessageContaining("Error uploading image");

        verify(uploader).upload(imageBytes, Map.of());
    }

    @Test
    void deleteImageByUrl_WithValidCloudinaryUrl_ShouldDeleteImage() throws IOException {
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/v1234567890/sample.jpg";

        when(uploader.destroy(anyString(), any())).thenReturn(Map.of("result", "ok"));

        cloudinaryService.deleteImageByUrl(imageUrl);

        verify(uploader).destroy("sample", Map.of());
    }

    @Test
    void deleteImageByUrl_WithNullUrl_ShouldNotCallCloudinary() throws IOException {
        cloudinaryService.deleteImageByUrl(null);

        verify(uploader, never()).destroy(anyString(), any());
    }

    @Test
    void deleteImageByUrl_WithNonCloudinaryUrl_ShouldNotCallCloudinary() throws IOException {
        String imageUrl = "https://example.com/image.jpg";

        cloudinaryService.deleteImageByUrl(imageUrl);

        verify(uploader, never()).destroy(anyString(), any());
    }

    @Test
    void deleteImageByUrl_WhenIOExceptionOccurs_ShouldThrowRuntimeException() throws IOException {
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/v1234567890/sample.jpg";

        when(uploader.destroy(anyString(), any())).thenThrow(new IOException("Delete failed"));

        assertThatThrownBy(() -> cloudinaryService.deleteImageByUrl(imageUrl))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not delete image");

        verify(uploader).destroy("sample", Map.of());
    }

    @Test
    void updateImage_WithValidImages_ShouldUploadNewAndDeleteOld() throws IOException {
        String oldImageUrl = "https://res.cloudinary.com/demo/image/upload/v1234567890/old.jpg";
        byte[] newImageBytes = "new image".getBytes();
        Map<String, Object> uploadResult = Map.of("secure_url", "https://cloudinary.com/new-image.jpg");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(newImageBytes);
        when(uploader.upload(any(byte[].class), any())).thenReturn(uploadResult);
        when(uploader.destroy(anyString(), any())).thenReturn(Map.of("result", "ok"));

        String newImageUrl = cloudinaryService.updateImage(oldImageUrl, multipartFile);

        assertThat(newImageUrl).isEqualTo("https://cloudinary.com/new-image.jpg");
        verify(uploader).upload(newImageBytes, Map.of());
        verify(uploader).destroy("old", Map.of());
    }

    @Test
    void updateImage_WithNullOldUrl_ShouldOnlyUploadNew() throws IOException {
        byte[] newImageBytes = "new image".getBytes();
        Map<String, Object> uploadResult = Map.of("secure_url", "https://cloudinary.com/new-image.jpg");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(newImageBytes);
        when(uploader.upload(any(byte[].class), any())).thenReturn(uploadResult);

        String newImageUrl = cloudinaryService.updateImage(null, multipartFile);

        assertThat(newImageUrl).isEqualTo("https://cloudinary.com/new-image.jpg");
        verify(uploader).upload(newImageBytes, Map.of());
        verify(uploader, never()).destroy(anyString(), any());
    }
}
