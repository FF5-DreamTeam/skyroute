package com.skyroute.skyroute.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyroute.skyroute.auth.dto.LoginRequest;
import com.skyroute.skyroute.auth.dto.LoginResponse;
import com.skyroute.skyroute.auth.dto.RefreshTokenRequest;
import com.skyroute.skyroute.auth.dto.RegisterResponse;
import com.skyroute.skyroute.auth.service.AuthService;
import com.skyroute.skyroute.shared.exception.custom_exception.EmailAlreadyExistsException;
import com.skyroute.skyroute.user.dto.UserRequest;
import com.skyroute.skyroute.user.dto.UserResponse;
import com.skyroute.skyroute.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_shouldReturnRegisterResponse_whenValidRequest() throws Exception {
        UserRequest request = createUserRequest();
        RegisterResponse response = createRegisterResponse();
        when(authService.register(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"))
                .andExpect(jsonPath("$.user.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.user.role").value("USER"))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService).register(any(UserRequest.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        UserRequest invalidRequest = new UserRequest("", "", "invalid-email", "123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        UserRequest request = createUserRequest();
        when(authService.register(any(UserRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists: john.doe@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(authService).register(any(UserRequest.class));
    }

    @Test
    void register_shouldHandleValidationErrors() throws Exception {
        UserRequest invalidRequest = new UserRequest(null, null, null, null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void login_shouldReturnLoginResponse_whenValidCredentials() throws Exception {
        LoginRequest request = createLoginRequest();
        LoginResponse response = createLoginResponse();
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    void login_shouldReturnUnauthorized_whenInvalidCredentials() throws Exception {
        LoginRequest request = createLoginRequest();
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_shouldHandleMalformedJson() throws Exception {
        String malformedJson = "{ \"email\": \"test@example.com\", \"password\": }";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    void refreshToken_shouldReturnNewTokens_whenValidRefreshToken() throws Exception {
        RefreshTokenRequest request = createRefreshTokenRequest();
        LoginResponse response = createLoginResponse();
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void refreshToken_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        RefreshTokenRequest invalidRequest = new RefreshTokenRequest("");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).refreshToken(any());
    }

    @Test
    void refreshToken_shouldReturnInternalServerError_whenInvalidToken() throws Exception {
        RefreshTokenRequest request = createRefreshTokenRequest();
        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void logout_shouldReturnLogoutResponse_whenValidToken() throws Exception {
        doNothing().when(authService).logout(anyString());

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout successful"))
                .andExpect(jsonPath("$.success").value(true));

        verify(authService).logout("validToken");
    }

    @Test
    void logout_shouldReturnLogoutResponse_whenNoToken() throws Exception {
        doNothing().when(authService).logout(anyString());

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout successful"))
                .andExpect(jsonPath("$.success").value(true));

        verify(authService, never()).logout(anyString());
    }

    @Test
    void logout_shouldReturnLogoutResponse_whenInvalidTokenFormat() throws Exception {
        doNothing().when(authService).logout(anyString());

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "InvalidFormat validToken"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout successful"))
                .andExpect(jsonPath("$.success").value(true));

        verify(authService, never()).logout(anyString());
    }

    @Test
    void authEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
        UserRequest request = createUserRequest();
        RegisterResponse response = createRegisterResponse();
        when(authService.register(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authService).register(any(UserRequest.class));
    }

    @Test
    void authEndpoints_shouldHandleCorsHeaders() throws Exception {
        UserRequest request = createUserRequest();
        RegisterResponse response = createRegisterResponse();
        when(authService.register(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));

        verify(authService).register(any(UserRequest.class));
    }

    private UserRequest createUserRequest() {
        return new UserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123");
    }

    private LoginRequest createLoginRequest() {
        return new LoginRequest(
                "john.doe@example.com",
                "password123");
    }

    private RefreshTokenRequest createRefreshTokenRequest() {
        return new RefreshTokenRequest("refreshToken");
    }

    private UserResponse createUserResponse() {
        return new UserResponse(
                1L,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                null,
                "+1234567890",
                "john.doe@example.com",
                Role.USER,
                null,
                null);
    }

    private RegisterResponse createRegisterResponse() {
        return new RegisterResponse(
                createUserResponse(),
                "accessToken",
                "refreshToken",
                "Bearer");
    }

    private LoginResponse createLoginResponse() {
        return new LoginResponse(
                "accessToken",
                "refreshToken",
                "Bearer");
    }
}
