package com.skyroute.skyroute.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageUploadException("Image file is required");
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) cloudinary.uploader()
                    .upload(image.getBytes(), ObjectUtils.emptyMap());
            return (String) result.get("secure_url");
        } catch (IOException exception) {
            log.error("Error uploading image: {}", exception.getMessage());
            throw new ImageUploadException("Error uploading image: " + exception.getMessage());
        }
    }

    public void deleteImageByUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return;
        }
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException exception) {
            log.error("Error deleting image: {}", exception.getMessage());
            throw new RuntimeException("Could not delete image: " + exception.getMessage());
        }
    }

    public String updateImage(String oldImageUrl, MultipartFile newImage) {
        String newImageUrl = uploadImage(newImage);
        deleteImageByUrl(oldImageUrl);
        return newImageUrl;
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}