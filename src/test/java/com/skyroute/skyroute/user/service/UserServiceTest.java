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
import com.skyroute.skyroute.user.enums.Role;
import com.skyroute.skyroute.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserResponse testUserResponse;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .userImgUrl("image.jpg")
                .role(Role.USER)
                .build();

        testUserResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        testUserRequest = new UserRequest("John", "Doe", "john@test.com", "password123");
    }

    @Test
    void createUser_WithValidData_ShouldReturnUserResponse() {
        User userWithRawPassword = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .userImgUrl("image.jpg")
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userMapper.toEntity(testUserRequest)).thenReturn(userWithRawPassword);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        UserResponse result = userService.createUser(testUserRequest, null);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.email()).isEqualTo("john@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithImage_ShouldReturnUserResponse() throws IOException {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/image.jpg");

        User userWithRawPassword = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .userImgUrl("image.jpg")
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userMapper.toEntity(testUserRequest)).thenReturn(userWithRawPassword);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        UserResponse result = userService.createUser(testUserRequest, image);

        assertThat(result).isNotNull();
        verify(cloudinaryService).uploadFile(image);
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(testUserRequest, null))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("User with email john@test.com already exists");
    }

    @Test
    void createUser_WithImageUploadError_ShouldThrowException() throws IOException {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        User userWithRawPassword = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .userImgUrl("image.jpg")
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userMapper.toEntity(testUserRequest)).thenReturn(userWithRawPassword);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(cloudinaryService.uploadFile(image)).thenThrow(new IOException("Upload failed"));

        assertThatThrownBy(() -> userService.createUser(testUserRequest, image))
                .isInstanceOf(ImageUploadException.class)
                .hasMessageContaining("Error uploading image");
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.firstName()).isEqualTo("John");
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id 999 not found");
    }

    @Test
    void getUserByEmail_WithValidEmail_ShouldReturnUserResponse() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        UserResponse result = userService.getUserByEmail("john@test.com");

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("john@test.com");
    }

    @Test
    void getUserByEmail_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail("invalid@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("invalid@test.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with email invalid@test.com not found");
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        Page<UserResponse> result = userService.getAllUsers(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().firstName()).isEqualTo("John");
    }

    @Test
    void updateUserByAdmin_WithValidData_ShouldReturnUpdatedUser() {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword", Role.ADMIN);
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.ADMIN)
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "image.jpg", "0987654321", "jane@test.com", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserByAdmin(1L, updateRequest, null);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Jane");
        assertThat(result.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void updateUserByAdmin_WithNoFields_ShouldThrowException() {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest(null, null, null, null, null, null, null,
                null);

        assertThatThrownBy(() -> userService.updateUserByAdmin(1L, updateRequest, null))
                .isInstanceOf(InvalidUpdateRequestException.class)
                .hasMessage("At least one field must be provided for update");
    }

    @Test
    void updateUserByAdmin_WithExistingEmail_ShouldThrowException() {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "existing@test.com", "newpassword", Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUserByAdmin(1L, updateRequest, null))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("User with email existing@test.com already exists");
    }

    @Test
    void updateUserProfile_WithValidData_ShouldReturnUpdatedUser() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword");
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.USER)
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "image.jpg", "0987654321", "jane@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserProfile(updateRequest, null);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Jane");
    }

    @Test
    void updateUserProfile_WithNoFields_ShouldThrowException() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(null, null, null, null, null, null, null);

        assertThatThrownBy(() -> userService.updateUserProfile(updateRequest, null))
                .isInstanceOf(InvalidUpdateRequestException.class)
                .hasMessage("At least one field must be provided for update");
    }

    @Test
    void updateUserProfile_WithNoAuthentication_ShouldThrowException() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> userService.updateUserProfile(updateRequest, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found");
    }

    @Test
    void updateUserRole_WithValidData_ShouldReturnUpdatedUser() {
        RoleUpdateRequest roleRequest = new RoleUpdateRequest(Role.ADMIN);
        User updatedUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserRole(1L, roleRequest);

        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id 999 not found");
    }

    @Test
    void findUserEntityById_WithValidId_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findUserEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void findUserEntityById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserEntityById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id 999 not found");
    }

    @Test
    void findUserEntityByEmail_WithValidEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));

        User result = userService.findUserEntityByEmail("john@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    void findUserEntityByEmail_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail("invalid@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserEntityByEmail("invalid@test.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with email invalid@test.com not found");
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        boolean result = userService.existsByEmail("john@test.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        when(userRepository.existsByEmail("nonexistent@test.com")).thenReturn(false);

        boolean result = userService.existsByEmail("nonexistent@test.com");

        assertThat(result).isFalse();
    }

    @Test
    void updateUserByAdmin_WithImage_ShouldUpdateUserImage() throws IOException {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword", Role.ADMIN);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.ADMIN)
                .userImgUrl("https://cloudinary.com/old-image.jpg")
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "https://cloudinary.com/new-image.jpg", "0987654321", "jane@test.com", Role.ADMIN, LocalDateTime.now(),
                LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserByAdmin(1L, updateRequest, image);

        assertThat(result).isNotNull();
        assertThat(result.userImgUrl()).isEqualTo("https://cloudinary.com/new-image.jpg");
        verify(cloudinaryService).uploadFile(image);
    }

    @Test
    void updateUserProfile_WithImage_ShouldUpdateUserImage() throws IOException {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword");
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.USER)
                .userImgUrl("https://cloudinary.com/old-image.jpg")
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "https://cloudinary.com/new-image.jpg", "0987654321", "jane@test.com", Role.USER, LocalDateTime.now(),
                LocalDateTime.now());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserProfile(updateRequest, image);

        assertThat(result).isNotNull();
        assertThat(result.userImgUrl()).isEqualTo("https://cloudinary.com/new-image.jpg");
        verify(cloudinaryService).uploadFile(image);
    }

    @Test
    void updateUserByAdmin_WithEmptyImage_ShouldNotUpdateImage() throws IOException {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword", Role.ADMIN);
        MockMultipartFile emptyImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.ADMIN)
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "image.jpg", "0987654321", "jane@test.com", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserByAdmin(1L, updateRequest, emptyImage);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Jane");
        verify(cloudinaryService, never()).uploadFile(any());
    }

    @Test
    void updateUserByAdmin_WithImageAndOldCloudinaryImage_ShouldDeleteOldImage() throws IOException {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword", Role.ADMIN);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");

        User userWithOldImage = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .userImgUrl("https://cloudinary.com/old-image.jpg")
                .role(Role.USER)
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.ADMIN)
                .userImgUrl("https://cloudinary.com/new-image.jpg")
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "https://cloudinary.com/new-image.jpg", "0987654321", "jane@test.com", Role.ADMIN, LocalDateTime.now(),
                LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithOldImage));
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserByAdmin(1L, updateRequest, image);

        assertThat(result).isNotNull();
        verify(cloudinaryService).uploadFile(image);
        verify(cloudinaryService).deleteFile("old-image");
    }

    @Test
    void updateUserByAdmin_WithImageAndNonCloudinaryImage_ShouldNotDeleteOldImage() throws IOException {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword", Role.ADMIN);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");

        User userWithOldImage = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .userImgUrl("https://other-service.com/old-image.jpg")
                .role(Role.USER)
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.ADMIN)
                .userImgUrl("https://cloudinary.com/new-image.jpg")
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "https://cloudinary.com/new-image.jpg", "0987654321", "jane@test.com", Role.ADMIN, LocalDateTime.now(),
                LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithOldImage));
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserByAdmin(1L, updateRequest, image);

        assertThat(result).isNotNull();
        verify(cloudinaryService).uploadFile(image);
        verify(cloudinaryService, never()).deleteFile(anyString());
    }

    @Test
    void updateUserProfile_WithEmptyImage_ShouldNotUpdateImage() throws IOException {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword");
        MockMultipartFile emptyImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.USER)
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "image.jpg", "0987654321", "jane@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserProfile(updateRequest, emptyImage);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Jane");
        verify(cloudinaryService, never()).uploadFile(any());
    }

    @Test
    void updateUserByAdmin_WithImageAndCloudinaryDeleteError_ShouldThrowRuntimeException() throws IOException {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword", Role.ADMIN);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");

        User userWithOldImage = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .userImgUrl("https://cloudinary.com/old-image.jpg")
                .role(Role.USER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithOldImage));
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        doThrow(new IOException("Delete failed")).when(cloudinaryService).deleteFile("old-image");

        assertThatThrownBy(() -> userService.updateUserByAdmin(1L, updateRequest, image))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not delete old image");
    }

    @Test
    void updateUserByAdmin_WithImageAndNullOldImage_ShouldNotDeleteOldImage() throws IOException {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword", Role.ADMIN);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");

        User userWithNullImage = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .userImgUrl(null)
                .role(Role.USER)
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.ADMIN)
                .userImgUrl("https://cloudinary.com/new-image.jpg")
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "https://cloudinary.com/new-image.jpg", "0987654321", "jane@test.com", Role.ADMIN, LocalDateTime.now(),
                LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithNullImage));
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserByAdmin(1L, updateRequest, image);

        assertThat(result).isNotNull();
        verify(cloudinaryService).uploadFile(image);
        verify(cloudinaryService, never()).deleteFile(anyString());
    }

    @Test
    void updateUserProfile_WithImageAndCloudinaryDeleteError_ShouldThrowRuntimeException() throws IOException {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword");
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");

        User userWithOldImage = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .userImgUrl("https://cloudinary.com/old-image.jpg")
                .role(Role.USER)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(userWithOldImage);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        doThrow(new IOException("Delete failed")).when(cloudinaryService).deleteFile("old-image");

        assertThatThrownBy(() -> userService.updateUserProfile(updateRequest, image))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not delete old image");
    }

    @Test
    void updateUserProfile_WithImageAndNullOldImage_ShouldNotDeleteOldImage() throws IOException {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), null, "0987654321", "jane@test.com", "newpassword");
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/new-image.jpg");

        User userWithNullImage = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .userImgUrl(null)
                .role(Role.USER)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(userWithNullImage);
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("encodedNewPassword")
                .birthDate(LocalDate.of(1992, 5, 15))
                .phoneNumber("0987654321")
                .role(Role.USER)
                .userImgUrl("https://cloudinary.com/new-image.jpg")
                .build();
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "https://cloudinary.com/new-image.jpg", "0987654321", "jane@test.com", Role.USER, LocalDateTime.now(),
                LocalDateTime.now());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(cloudinaryService.uploadFile(image)).thenReturn(uploadResult);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUserProfile(updateRequest, image);

        assertThat(result).isNotNull();
        verify(cloudinaryService).uploadFile(image);
        verify(cloudinaryService, never()).deleteFile(anyString());
    }
}
