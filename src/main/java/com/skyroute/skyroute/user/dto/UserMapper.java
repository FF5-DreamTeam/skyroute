package com.skyroute.skyroute.user.dto;

import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(request.password())
                .role(Role.USER)
                .build();
    }

    public User toEntity(UserAdminUpdateRequest request) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .userImgUrl(request.userImgUrl())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .password(request.password())
                .role(request.role())
                .build();
    }

    public User toEntity(UserProfileUpdateRequest request) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .userImgUrl(request.userImgUrl())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .password(request.password())
                .role(Role.USER)
                .build();
    }

    public void updateEntity(User existing, UserProfileUpdateRequest request) {
        if (request.firstName() != null) {
            existing.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            existing.setLastName(request.lastName());
        }
        if (request.birthDate() != null) {
            existing.setBirthDate(request.birthDate());
        }
        if (request.userImgUrl() != null) {
            existing.setUserImgUrl(request.userImgUrl());
        }
        if (request.phoneNumber() != null) {
            existing.setPhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            existing.setEmail(request.email());
        }
        if (request.password() != null) {
            existing.setPassword(request.password());
        }
    }

    public void updateEntity(User existing, UserAdminUpdateRequest request) {
        if (request.firstName() != null) {
            existing.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            existing.setLastName(request.lastName());
        }
        if (request.birthDate() != null) {
            existing.setBirthDate(request.birthDate());
        }
        if (request.userImgUrl() != null) {
            existing.setUserImgUrl(request.userImgUrl());
        }
        if (request.phoneNumber() != null) {
            existing.setPhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            existing.setEmail(request.email());
        }
        if (request.password() != null) {
            existing.setPassword(request.password());
        }
        if (request.role() != null) {
            existing.setRole(request.role());
        }
    }

    public void updateRole(User existing, RoleUpdateRequest request) {
        if (request.role() != null) {
            existing.setRole(request.role());
        }
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getUserImgUrl(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
