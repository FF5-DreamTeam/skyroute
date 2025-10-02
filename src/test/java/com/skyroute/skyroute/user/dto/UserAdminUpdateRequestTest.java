package com.skyroute.skyroute.user.dto;

import com.skyroute.skyroute.user.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserAdminUpdateRequestTest {

    @Test
    void hasAnyField_WithAllNullFields_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, null, null, null, null, null, null
        );

        assertThat(request.hasAnyField()).isFalse();
    }

    @Test
    void hasAnyField_WithFirstName_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", null, null, null, null, null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithLastName_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, "Doe", null, null, null, null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithBirthDate_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, LocalDate.of(1990, 1, 1), null, null, null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithUserImgUrl_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, null, "image.jpg", null, null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithPhoneNumber_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, null, null, "1234567890", null, null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithEmail_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, null, null, null, "test@email.com", null, null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithPassword_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, null, null, null, null, "password123", null
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithRole_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, null, null, null, null, null, Role.ADMIN
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasAnyField_WithMultipleFields_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), "image.jpg", 
                "1234567890", "test@email.com", "password123", Role.ADMIN
        );

        assertThat(request.hasAnyField()).isTrue();
    }

    @Test
    void hasRequiredFields_WithAllRequiredFields_ShouldReturnTrue() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), null, 
                "1234567890", "test@email.com", null, Role.ADMIN
        );

        assertThat(request.hasRequiredFields()).isTrue();
    }

    @Test
    void hasRequiredFields_WithMissingFirstName_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, "Doe", LocalDate.of(1990, 1, 1), null, 
                "1234567890", "test@email.com", null, Role.ADMIN
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingLastName_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", null, LocalDate.of(1990, 1, 1), null, 
                "1234567890", "test@email.com", null, Role.ADMIN
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingBirthDate_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", "Doe", null, null, 
                "1234567890", "test@email.com", null, Role.ADMIN
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingPhoneNumber_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), null, 
                null, "test@email.com", null, Role.ADMIN
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingEmail_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), null, 
                "1234567890", null, null, Role.ADMIN
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithMissingRole_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), null, 
                "1234567890", "test@email.com", null, null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }

    @Test
    void hasRequiredFields_WithAllNullFields_ShouldReturnFalse() {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                null, null, null, null, null, null, null, null
        );

        assertThat(request.hasRequiredFields()).isFalse();
    }
}
