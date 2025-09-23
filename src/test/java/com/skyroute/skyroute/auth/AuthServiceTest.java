package com.skyroute.skyroute.auth;

import com.skyroute.skyroute.auth.dto.LoginRequest;
import com.skyroute.skyroute.auth.dto.LoginResponse;
import com.skyroute.skyroute.auth.dto.RefreshTokenRequest;
import com.skyroute.skyroute.auth.dto.RegisterResponse;
import com.skyroute.skyroute.auth.service.AuthServiceImpl;
import com.skyroute.skyroute.security.details.CustomUserDetails;
import com.skyroute.skyroute.security.jwt.JwtUtil;
import com.skyroute.skyroute.security.jwt.TokenBlacklistService;
import com.skyroute.skyroute.shared.exception.custom_exception.EmailAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.UserNotFoundException;
import com.skyroute.skyroute.user.dto.UserMapper;
import com.skyroute.skyroute.user.dto.UserRequest;
import com.skyroute.skyroute.user.dto.UserResponse;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import com.skyroute.skyroute.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserRequest testUserRequest;
    private LoginRequest testLoginRequest;
    private RefreshTokenRequest testRefreshTokenRequest;
    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUserRequest = createTestUserRequest();
        testLoginRequest = createTestLoginRequest();
        testRefreshTokenRequest = createTestRefreshTokenRequest();
        testUserDetails = new CustomUserDetails(testUser);
    }

    @Test
    void register_shouldReturnRegisterResponse_whenValidRequest() {
        when(userRepository.existsByEmail(testUserRequest.email())).thenReturn(false);
        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(createTestUserResponse());
        when(jwtUtil.generateToken(any(CustomUserDetails.class))).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(any(CustomUserDetails.class))).thenReturn("refreshToken");

        RegisterResponse result = authService.register(testUserRequest);

        assertNotNull(result);
        assertNotNull(result.user());
        assertEquals(testUser.getEmail(), result.user().email());
        assertEquals(testUser.getFirstName(), result.user().firstName());
        assertEquals(testUser.getLastName(), result.user().lastName());
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("Bearer", result.tokenType());

        verify(userRepository).existsByEmail(testUserRequest.email());
        verify(userMapper).toEntity(testUserRequest);
        verify(passwordEncoder).encode(testUser.getPassword());
        verify(userRepository).save(testUser);
        verify(userMapper).toResponse(testUser);
        verify(jwtUtil).generateToken(any(CustomUserDetails.class));
        verify(jwtUtil).generateRefreshToken(any(CustomUserDetails.class));
    }

    @Test
    void register_shouldThrowEmailAlreadyExistsException_whenEmailExists() {
        when(userRepository.existsByEmail(testUserRequest.email())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(testUserRequest));

        assertEquals("Email already exists: " + testUserRequest.email(), exception.getMessage());
        verify(userRepository).existsByEmail(testUserRequest.email());
        verify(userMapper, never()).toEntity(any(UserRequest.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnLoginResponse_whenValidCredentials() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        when(jwtUtil.generateToken(testUserDetails)).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(testUserDetails)).thenReturn("refreshToken");

        LoginResponse result = authService.login(testLoginRequest);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("Bearer", result.tokenType());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(testUserDetails);
        verify(jwtUtil).generateRefreshToken(testUserDetails);
    }

    @Test
    void login_shouldThrowBadCredentialsException_whenInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(testLoginRequest));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any());
        verify(jwtUtil, never()).generateRefreshToken(any());
    }

    @Test
    void refreshToken_shouldReturnNewTokens_whenValidRefreshToken() {
        when(tokenBlacklistService.isBlacklisted("refreshToken")).thenReturn(false);
        when(jwtUtil.extractUsername("refreshToken")).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken("refreshToken", testUserDetails)).thenReturn(true);
        when(jwtUtil.generateToken(testUserDetails)).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(testUserDetails)).thenReturn("newRefreshToken");

        LoginResponse result = authService.refreshToken(testRefreshTokenRequest);

        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        assertEquals("newRefreshToken", result.refreshToken());
        assertEquals("Bearer", result.tokenType());

        verify(tokenBlacklistService).isBlacklisted("refreshToken");
        verify(jwtUtil).extractUsername("refreshToken");
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(jwtUtil).validateToken("refreshToken", testUserDetails);
        verify(jwtUtil).generateToken(testUserDetails);
        verify(jwtUtil).generateRefreshToken(testUserDetails);
        verify(tokenBlacklistService).addToBlacklist("refreshToken");
    }

    @Test
    void refreshToken_shouldThrowRuntimeException_whenTokenIsBlacklisted() {
        when(tokenBlacklistService.isBlacklisted("refreshToken")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.refreshToken(testRefreshTokenRequest));

        assertEquals("Token has been revoked", exception.getMessage());
        verify(tokenBlacklistService).isBlacklisted("refreshToken");
        verify(jwtUtil, never()).extractUsername(any());
    }

    @Test
    void refreshToken_shouldThrowUserNotFoundException_whenUserNotFound() {
        when(tokenBlacklistService.isBlacklisted("refreshToken")).thenReturn(false);
        when(jwtUtil.extractUsername("refreshToken")).thenReturn("nonexistent@email.com");
        when(userRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authService.refreshToken(testRefreshTokenRequest));

        assertEquals("User not found with email: nonexistent@email.com", exception.getMessage());
        verify(tokenBlacklistService).isBlacklisted("refreshToken");
        verify(jwtUtil).extractUsername("refreshToken");
        verify(userRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    void refreshToken_shouldThrowRuntimeException_whenInvalidToken() {
        when(tokenBlacklistService.isBlacklisted("refreshToken")).thenReturn(false);
        when(jwtUtil.extractUsername("refreshToken")).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken("refreshToken", testUserDetails)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.refreshToken(testRefreshTokenRequest));

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(tokenBlacklistService).isBlacklisted("refreshToken");
        verify(jwtUtil).extractUsername("refreshToken");
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(jwtUtil).validateToken("refreshToken", testUserDetails);
    }

    @Test
    void logout_shouldAddTokenToBlacklist_whenValidToken() {
        String refreshToken = "refreshToken";

        authService.logout(refreshToken);

        verify(tokenBlacklistService).addToBlacklist(refreshToken);
        SecurityContextHolder.clearContext();
    }

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .phoneNumber("+1234567890")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.USER)
                .build();
    }

    private UserRequest createTestUserRequest() {
        return new UserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123");
    }

    private LoginRequest createTestLoginRequest() {
        return new LoginRequest(
                "john.doe@example.com",
                "password123");
    }

    private RefreshTokenRequest createTestRefreshTokenRequest() {
        return new RefreshTokenRequest("refreshToken");
    }

    private UserResponse createTestUserResponse() {
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
}
