package com.skyroute.skyroute.security.details;

import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import com.skyroute.skyroute.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        String email = "test@example.com";
        User user = createUser(email, Role.USER);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        assertThat(result)
                .isInstanceOf(CustomUserDetails.class)
                .satisfies(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo(email);
                    assertThat(userDetails.getPassword()).isEqualTo(ENCODED_PASSWORD);
                    assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
                    assertThat(userDetails.isAccountNonExpired()).isTrue();
                    assertThat(userDetails.isAccountNonLocked()).isTrue();
                    assertThat(userDetails.isCredentialsNonExpired()).isTrue();
                    assertThat(userDetails.isEnabled()).isTrue();
                });

        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_WithAdminRole_ShouldReturnCorrectAuthorities() {
        String email = "admin@example.com";
        User adminUser = createUser(email, Role.ADMIN);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(adminUser));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "nonexistent@example.com" })
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException(String email) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + email);
    }

    @Test
    void loadUserByUsername_WithNullEmail_ShouldThrowException() {
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: null");
    }

    private User createUser(String email, Role role) {
        return User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email(email)
                .password(ENCODED_PASSWORD)
                .phoneNumber("+1234567890")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(role)
                .build();
    }
}