package com.skyroute.skyroute.user.controller;

import com.skyroute.skyroute.user.dto.RoleUpdateRequest;
import com.skyroute.skyroute.user.dto.UserAdminUpdateRequest;
import com.skyroute.skyroute.user.dto.UserProfileUpdateRequest;
import com.skyroute.skyroute.user.dto.UserRequest;
import com.skyroute.skyroute.user.dto.UserResponse;
import com.skyroute.skyroute.user.enums.Role;
import com.skyroute.skyroute.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_ShouldReturnCreatedUser() {
        UserRequest userRequest = new UserRequest("John", "Doe", "john@test.com", "password123");
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        when(userService.createUser(any(UserRequest.class), any())).thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.createUser("John", "Doe", "john@test.com", "password123",
                image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("John");
        assertThat(response.getBody().email()).isEqualTo("john@test.com");
    }

    @Test
    void createUser_WithoutImage_ShouldReturnCreatedUser() {
        UserRequest userRequest = new UserRequest("John", "Doe", "john@test.com", "password123");
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                null, "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        when(userService.createUser(any(UserRequest.class), any())).thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.createUser("John", "Doe", "john@test.com", "password123",
                null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("John");
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().firstName()).isEqualTo("John");
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        when(userService.getUserByEmail("john@test.com")).thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.getUserByEmail("john@test.com");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("john@test.com");
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        UserResponse user1 = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image1.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());
        UserResponse user2 = new UserResponse(2L, "Jane", "Smith", LocalDate.of(1992, 5, 15),
                "image2.jpg", "0987654321", "jane@test.com", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now());

        Page<UserResponse> expectedPage = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 10), 2);

        when(userService.getAllUsers(0, 10)).thenReturn(expectedPage);

        ResponseEntity<Page<UserResponse>> response = userController.getAllUsers(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(2);
        assertThat(response.getBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    void updateUserByAdmin_ShouldReturnUpdatedUser() {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("John", "Doe",
                LocalDate.of(1990, 1, 1), null, "1234567890", "john@test.com", "newpassword", Role.ADMIN);
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now());
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        when(userService.updateUserByAdmin(anyLong(), any(UserAdminUpdateRequest.class), any()))
                .thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.updateUserByAdmin(1L, "John", "Doe",
                "1990-01-01", "1234567890", "john@test.com", "newpassword", "ADMIN", image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void updateUserByAdmin_WithNullValues_ShouldReturnUpdatedUser() {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest(null, null, null, null, null, null, null,
                null);
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        when(userService.updateUserByAdmin(anyLong(), any(UserAdminUpdateRequest.class), any()))
                .thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.updateUserByAdmin(1L, null, null,
                null, null, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateUserProfile_ShouldReturnUpdatedUser() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("John", "Doe",
                LocalDate.of(1990, 1, 1), null, "1234567890", "john@test.com", "newpassword");
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        when(userService.updateUserProfile(any(UserProfileUpdateRequest.class), any()))
                .thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.updateUserProfile("John", "Doe",
                "1990-01-01", "1234567890", "john@test.com", "newpassword", image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("John");
    }

    @Test
    void updateUserProfile_WithNullValues_ShouldReturnUpdatedUser() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(null, null, null, null, null, null, null);
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.USER, LocalDateTime.now(), LocalDateTime.now());

        when(userService.updateUserProfile(any(UserProfileUpdateRequest.class), any()))
                .thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.updateUserProfile(null, null,
                null, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateUserRole_ShouldReturnUpdatedUser() {
        RoleUpdateRequest roleRequest = new RoleUpdateRequest(Role.ADMIN);
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", LocalDate.of(1990, 1, 1),
                "image.jpg", "1234567890", "john@test.com", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now());

        when(userService.updateUserRole(1L, roleRequest)).thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userController.updateUserRole(1L, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).deleteUser(1L);
    }
}
