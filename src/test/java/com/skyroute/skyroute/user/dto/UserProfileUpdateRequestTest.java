package com.skyroute.skyroute.user.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserProfileUpdateRequestTest {

    @Test
    void hasAnyField_WithAllNullFields_ShouldReturnFalse() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, null, null, null, null, null, null
        );

        assertThat(request.hasAnyField()).isFalse();
    }

    @Test
    void hasAnyField_WithFirstName_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "John", null, null, null, null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithLastName_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, "Doe", null, null, null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithBirthDate_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, null, LocalDate.of(1990, 1, 1), null, null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithUserImgUrl_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, null, null, "image.jpg", null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithPhoneNumber_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, null, null, null, "1234567890", null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithEmail_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, null, null, null, null, "test@email.com", null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithPassword_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, null, null, null, null, null, "password123"
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithMultipleFields_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), "image.jpg", 
                "1234567890", "test@email.com", "password123"
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasRequiredFields_WithAllRequiredFields_ShouldReturnTrue() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), null, 
                "1234567890", "test@email.com", null
        );

        assertThat(request.hasRequiredFields()).isTrue();
    }

    @Test
    void hasRequiredFields_WithMissingFirstName_ShouldReturnFalse() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, "Doe", LocalDate.of(1990, 1, 1), null, 
                "1234567890", "test@email.com", null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingLastName_ShouldReturnFalse() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "John", null, LocalDate.of(1990, 1, 1), null, 
                "1234567890", "test@email.com", null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingBirthDate_ShouldReturnFalse() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "John", "Doe", null, null, 
                "1234567890", "test@email.com", null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingPhoneNumber_ShouldReturnFalse() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), null, 
                null, "test@email.com", null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingEmail_ShouldReturnFalse() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), null, 
                "1234567890", null, null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithAllNullFields_ShouldReturnFalse() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                null, null, null, null, null, null, null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }
}
