package com.skyroute.skyroute.user.controller;

import com.skyroute.skyroute.user.dto.UserAdminUpdateRequest;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import com.skyroute.skyroute.user.repository.UserRepository;
import com.skyroute.skyroute.cloudinary.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(cloudinaryService.uploadImage(any())).thenReturn("https://example.com/test-image.jpg");
        when(cloudinaryService.updateImage(any(), any())).thenReturn("https://example.com/updated-image.jpg");

        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AsAdmin_ShouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_WithoutAuthentication_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserByAdmin_WithValidData_ShouldUpdateUser() throws Exception {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest(
                "Jane", "Smith", LocalDate.of(1992, 5, 15), null,
                "0987654321", "jane@test.com", "newpassword", Role.ADMIN);

        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test content".getBytes());

        mockMvc.perform(multipart("/api/users/{id}", testUser.getId())
                .file(image)
                .param("firstName", updateRequest.firstName())
                .param("lastName", updateRequest.lastName())
                .param("birthDate", updateRequest.birthDate().toString())
                .param("phoneNumber", updateRequest.phoneNumber())
                .param("email", updateRequest.email())
                .param("password", updateRequest.password())
                .param("role", updateRequest.role().toString())
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserByAdmin_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest(
                "Jane", "Smith", null, null, null, null, null, null);

        mockMvc.perform(multipart("/api/users/999")
                .param("firstName", updateRequest.firstName())
                .param("lastName", updateRequest.lastName())
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUserByAdmin_AsUser_ShouldReturnForbidden() throws Exception {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest(
                "Jane", "Smith", null, null, null, null, null, null);

        mockMvc.perform(multipart("/api/users/{id}", testUser.getId())
                .param("firstName", updateRequest.firstName())
                .param("lastName", updateRequest.lastName())
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithValidId_ShouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithValidData_ShouldCreateUser() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test content".getBytes());

        mockMvc.perform(multipart("/api/users")
                .file(image)
                .param("firstName", "Jane")
                .param("lastName", "Smith")
                .param("email", "jane@test.com")
                .param("password", "password123"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithExistingEmail_ShouldReturnConflict() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test content".getBytes());

        mockMvc.perform(multipart("/api/users")
                .file(image)
                .param("firstName", "Jane")
                .param("lastName", "Smith")
                .param("email", "john@test.com")
                .param("password", "password123"))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_WithoutAuthentication_ShouldReturnForbidden() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test content".getBytes());

        mockMvc.perform(multipart("/api/users")
                .file(image)
                .param("firstName", "Jane")
                .param("lastName", "Smith")
                .param("email", "jane@test.com")
                .param("password", "password123"))
                .andExpect(status().isForbidden());
    }
}
