package com.skyroute.skyroute.user.dto;

import com.skyroute.skyroute.user.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(Long id,
                           String firstName,
                           String lastName,
                           LocalDate birthDate,
                           String userImgUrl,
                           String phoneNumber,
                           String email,
                           Role role,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
}
