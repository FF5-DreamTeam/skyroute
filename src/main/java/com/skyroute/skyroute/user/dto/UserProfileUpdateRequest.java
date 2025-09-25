package com.skyroute.skyroute.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileUpdateRequest(
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,

        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,

        @Past(message = "Birth date must be in the past")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate,

        String userImgUrl,

        String phoneNumber,

        @Email(message = "Email should be valid") String email,

        @Size(min = 6, message = "Password must be at least 6 characters") String password) {

    public boolean hasAnyField() {
        return firstName != null || lastName != null || birthDate != null ||
                userImgUrl != null || phoneNumber != null || email != null || password != null;
    }

    public boolean hasRequiredFields() {
        return firstName != null && lastName != null && birthDate != null &&
                phoneNumber != null && email != null;
    }
}
