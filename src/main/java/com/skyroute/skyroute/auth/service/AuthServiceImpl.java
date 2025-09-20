package com.skyroute.skyroute.auth.service;

import com.skyroute.skyroute.auth.dto.LoginRequest;
import com.skyroute.skyroute.auth.dto.LoginResponse;
import com.skyroute.skyroute.auth.dto.RefreshTokenRequest;
import com.skyroute.skyroute.auth.dto.RegisterResponse;
import com.skyroute.skyroute.security.details.CustomUserDetails;
import com.skyroute.skyroute.security.jwt.JwtUtil;
import com.skyroute.skyroute.security.jwt.TokenBlacklistService;
import com.skyroute.skyroute.shared.exception.custom_exception.EmailAlreadyExistsException;
import com.skyroute.skyroute.shared.exception.custom_exception.UserNotFoundException;
import com.skyroute.skyroute.user.dto.UserMapper;
import com.skyroute.skyroute.user.dto.UserRequest;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public RegisterResponse register(UserRequest request) {
        log.info("Registering new user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration attempt with existing email: {}", request.email());
            throw new EmailAlreadyExistsException("Email already exists: " + request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.info("Successfully registered user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
        
        return new RegisterResponse(
                userMapper.toResponse(savedUser),
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.info("Successful login for user: {}", userDetails.getUsername());
        return new LoginResponse(accessToken, refreshToken, "Bearer");
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token request received");
        String refreshToken = request.refreshToken();

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            log.warn("Attempted to use blacklisted refresh token");
            throw new RuntimeException("Token has been revoked");
        }

        String email = jwtUtil.extractUsername(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            log.warn("Invalid refresh token for user: {}", email);
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        tokenBlacklistService.addToBlacklist(refreshToken);

        log.info("Successfully refreshed tokens for user: {}", email);
        return new LoginResponse(newAccessToken, newRefreshToken, "Bearer");
    }

    @Override
    public void logout(String refreshToken) {
        log.info("User logout requested");
        tokenBlacklistService.addToBlacklist(refreshToken);
        SecurityContextHolder.clearContext();
        log.info("User successfully logged out");
    }
}
