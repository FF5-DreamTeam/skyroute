package com.skyroute.skyroute.user.service;

import com.skyroute.skyroute.cloudinary.CloudinaryService;
import com.skyroute.skyroute.security.details.CustomUserDetails;
import com.skyroute.skyroute.shared.exception.custom_exception.EmailAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.shared.exception.custom_exception.ImageUploadException;
import com.skyroute.skyroute.shared.exception.custom_exception.InvalidUpdateRequestException;
import com.skyroute.skyroute.user.dto.RoleUpdateRequest;
import com.skyroute.skyroute.user.dto.UserAdminUpdateRequest;
import com.skyroute.skyroute.user.dto.UserMapper;
import com.skyroute.skyroute.user.dto.UserProfileUpdateRequest;
import com.skyroute.skyroute.user.dto.UserRequest;
import com.skyroute.skyroute.user.dto.UserResponse;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @Override
    public UserResponse createUser(UserRequest userRequest, MultipartFile image) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new EmailAlreadyExistsException("User with email " + userRequest.email() + " already exists");
        }

        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (image != null && !image.isEmpty()) {
            String imageUrl = uploadImage(image);
            user.setUserImgUrl(imageUrl);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserEntityById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = findUserEntityByEmail(email);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponse updateUserByAdmin(Long id, UserAdminUpdateRequest updateRequest, MultipartFile image) {
        if (!updateRequest.hasAnyField() && (image == null || image.isEmpty())) {
            throw new InvalidUpdateRequestException("At least one field must be provided for update");
        }

        User user = findUserEntityById(id);

        if (updateRequest.email() != null && !updateRequest.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.email())) {
                throw new EmailAlreadyExistsException("User with email " + updateRequest.email() + " already exists");
            }
        }

        updateUserFields(user, updateRequest);

        if (updateRequest.password() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.password()));
        }

        if (image != null && !image.isEmpty()) {
            updateUserImage(user, image);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUserProfile(UserProfileUpdateRequest updateRequest, MultipartFile image) {
        if (!updateRequest.hasAnyField() && (image == null || image.isEmpty())) {
            throw new InvalidUpdateRequestException("At least one field must be provided for update");
        }

        User currentUser = getCurrentUser();

        if (updateRequest.email() != null && !updateRequest.email().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.email())) {
                throw new EmailAlreadyExistsException("User with email " + updateRequest.email() + " already exists");
            }
        }

        updateUserProfileFields(currentUser, updateRequest);

        if (updateRequest.password() != null) {
            currentUser.setPassword(passwordEncoder.encode(updateRequest.password()));
        }

        if (image != null && !image.isEmpty()) {
            updateUserImage(currentUser, image);
        }

        User savedUser = userRepository.save(currentUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserEntityById(id);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private void updateUserFields(User user, UserAdminUpdateRequest updateRequest) {
        if (updateRequest.firstName() != null) {
            user.setFirstName(updateRequest.firstName());
        }
        if (updateRequest.lastName() != null) {
            user.setLastName(updateRequest.lastName());
        }
        if (updateRequest.birthDate() != null) {
            user.setBirthDate(updateRequest.birthDate());
        }
        if (updateRequest.userImgUrl() != null) {
            user.setUserImgUrl(updateRequest.userImgUrl());
        }
        if (updateRequest.phoneNumber() != null) {
            user.setPhoneNumber(updateRequest.phoneNumber());
        }
        if (updateRequest.email() != null) {
            user.setEmail(updateRequest.email());
        }
        if (updateRequest.role() != null) {
            user.setRole(updateRequest.role());
        }
    }

    private void updateUserProfileFields(User user, UserProfileUpdateRequest updateRequest) {
        if (updateRequest.firstName() != null) {
            user.setFirstName(updateRequest.firstName());
        }
        if (updateRequest.lastName() != null) {
            user.setLastName(updateRequest.lastName());
        }
        if (updateRequest.birthDate() != null) {
            user.setBirthDate(updateRequest.birthDate());
        }
        if (updateRequest.userImgUrl() != null) {
            user.setUserImgUrl(updateRequest.userImgUrl());
        }
        if (updateRequest.phoneNumber() != null) {
            user.setPhoneNumber(updateRequest.phoneNumber());
        }
        if (updateRequest.email() != null) {
            user.setEmail(updateRequest.email());
        }
    }

    @Override
    public UserResponse updateUserRole(Long id, RoleUpdateRequest roleRequest) {
        User user = findUserEntityById(id);
        user.setRole(roleRequest.role());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails(User user)) {
            return user;
        }
        throw new IllegalStateException("No authenticated user found");
    }

    private String uploadImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageUploadException("Image file is required");
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) cloudinaryService.uploadFile(image);
            return (String) result.get("secure_url");
        } catch (IOException exception) {
            throw new ImageUploadException("Error uploading image: " + exception.getMessage());
        }
    }

    private void deleteOldImage(String imageUrl) {
        if (imageUrl != null && imageUrl.contains("cloudinary.com")) {
            try {
                String[] parts = imageUrl.split("/");
                String fileName = parts[parts.length - 1];
                String publicId = fileName.substring(0, fileName.lastIndexOf("."));
                cloudinaryService.deleteFile(publicId);
            } catch (IOException exception) {
                throw new RuntimeException("Could not delete old image: " + exception.getMessage());
            }
        }
    }

    private void updateUserImage(User user, MultipartFile newImage) {
        String oldImageUrl = user.getUserImgUrl();
        String newImageUrl = uploadImage(newImage);
        user.setUserImgUrl(newImageUrl);
        deleteOldImage(oldImageUrl);
    }
}
