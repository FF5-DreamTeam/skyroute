package com.skyroute.skyroute.auth;

import com.skyroute.skyroute.auth.dto.ForgotPasswordRequest;
import com.skyroute.skyroute.auth.dto.ResetPasswordRequest;
import com.skyroute.skyroute.auth.service.AuthService;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import com.skyroute.skyroute.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
@Transactional
public class PasswordResetTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testForgotPassword() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .password("password123")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        ForgotPasswordRequest request = new ForgotPasswordRequest("john.doe@test.com");
        var response = authService.forgotPassword(request);

        assertTrue(response.success());
        assertEquals("Password reset instructions have been sent to your email", response.message());

        User updatedUser = userRepository.findByEmail("john.doe@test.com").orElseThrow();
        assertNotNull(updatedUser.getPasswordResetToken());
        assertNotNull(updatedUser.getPasswordResetTokenExpiresAt());
        assertTrue(updatedUser.getPasswordResetTokenExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testResetPassword() {
        String resetToken = UUID.randomUUID().toString();
        User user = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@test.com")
                .password("oldpassword")
                .role(Role.USER)
                .passwordResetToken(resetToken)
                .passwordResetTokenExpiresAt(LocalDateTime.now().plusHours(1))
                .build();
        userRepository.save(user);

        ResetPasswordRequest request = new ResetPasswordRequest(resetToken, "newpassword123");
        var response = authService.resetPassword(request);

        assertTrue(response.success());
        assertEquals("Password has been successfully reset", response.message());

        User updatedUser = userRepository.findByEmail("jane.doe@test.com").orElseThrow();
        assertTrue(passwordEncoder.matches("newpassword123", updatedUser.getPassword()));
        assertNull(updatedUser.getPasswordResetToken());
        assertNull(updatedUser.getPasswordResetTokenExpiresAt());
    }

    @Test
    public void testResetPasswordWithExpiredToken() {
        String resetToken = UUID.randomUUID().toString();
        User user = User.builder()
                .firstName("Bob")
                .lastName("Smith")
                .email("bob.smith@test.com")
                .password("oldpassword")
                .role(Role.USER)
                .passwordResetToken(resetToken)
                .passwordResetTokenExpiresAt(LocalDateTime.now().minusHours(1))
                .build();
        userRepository.save(user);

        ResetPasswordRequest request = new ResetPasswordRequest(resetToken, "newpassword123");

        assertThrows(RuntimeException.class, () -> {
            authService.resetPassword(request);
        });
    }
}
