package com.skyroute.skyroute.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skyroute.skyroute.user.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserAdminUpdateRequest(
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        String userImgUrl,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        Role role ) {

    public boolean hasAnyField() {
        return firstName != null || lastName != null || birthDate != null ||
                userImgUrl != null || phoneNumber != null || email != null ||
                password != null || role != null;
    }

    public boolean hasRequiredFields() {
        return firstName != null && lastName != null && birthDate != null &&
                phoneNumber != null && email != null && role != null;
    }
}
