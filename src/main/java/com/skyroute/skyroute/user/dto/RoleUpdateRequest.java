package com.skyroute.skyroute.user.dto;

import com.skyroute.skyroute.user.enums.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "Role is required")
        Role role
) {
}
