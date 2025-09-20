package com.skyroute.skyroute.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void generateTokenAndExtractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void extractExpiration() {
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void generateRefreshToken_containsType() {
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        String username = jwtUtil.extractUsername(refreshToken);
        assertEquals(userDetails.getUsername(), username);

        String type = jwtUtil.extractClaim(refreshToken, claims -> (String) claims.get("type"));
        assertEquals("refresh", type);
    }

    @Test
    void validateToken_shouldBeValid() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldBeInvalidForWrongUser() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());
        assertFalse(jwtUtil.validateToken(token, otherUser));
    }

    @Test
    void validateToken_shouldBeInvalidForNullToken() {
        assertFalse(jwtUtil.validateToken(null, userDetails));
    }

    @Test
    void validateToken_shouldBeInvalidForEmptyToken() {
        assertFalse(jwtUtil.validateToken("", userDetails));
    }

    @Test
    void validateToken_shouldBeInvalidForMalformedToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here", userDetails));
    }

    @Test
    void extractUsername_shouldThrowExceptionForInvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.extractUsername("invalid.token"));
    }

    @Test
    void extractExpiration_shouldThrowExceptionForInvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.extractExpiration("invalid.token"));
    }

    @Test
    void generateRefreshToken_shouldHaveLongerExpiration() {
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        Date accessExpiration = jwtUtil.extractExpiration(accessToken);
        Date refreshExpiration = jwtUtil.extractExpiration(refreshToken);

        assertTrue(refreshExpiration.after(accessExpiration));
    }
}
