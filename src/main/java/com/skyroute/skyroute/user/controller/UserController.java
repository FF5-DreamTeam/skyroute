package com.skyroute.skyroute.user.controller;

import com.skyroute.skyroute.user.dto.RoleUpdateRequest;
import com.skyroute.skyroute.user.dto.UserAdminUpdateRequest;
import com.skyroute.skyroute.user.dto.UserRequest;
import com.skyroute.skyroute.user.dto.UserProfileUpdateRequest;
import com.skyroute.skyroute.user.dto.UserResponse;
import com.skyroute.skyroute.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

        private final UserService userService;

        @Operation(summary = "Create a new user", description = "Creates a new user account with the provided information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content)
        })
        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<UserResponse> createUser(
                        @RequestParam("firstName") @Size(min = 2, max = 50) String firstName,
                        @RequestParam("lastName") @Size(min = 2, max = 50) String lastName,
                        @RequestParam("email") @Email String email,
                        @RequestParam("password") @Size(min = 6) String password,
                        @RequestPart(value = "image", required = false) MultipartFile image) {

                UserRequest request = new UserRequest(firstName, lastName, email, password);
                UserResponse userResponse = userService.createUser(request, image);
                return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        }

        @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<UserResponse> getUserById(
                        @Parameter(description = "User ID", required = true) @PathVariable Long id) {
                UserResponse userResponse = userService.getUserById(id);
                return ResponseEntity.ok(userResponse);
        }

        @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
        })
        @GetMapping("/email/{email}")
        public ResponseEntity<UserResponse> getUserByEmail(
                        @Parameter(description = "User email", required = true) @PathVariable String email) {
                UserResponse userResponse = userService.getUserByEmail(email);
                return ResponseEntity.ok(userResponse);
        }

        @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content)
        })
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Page<UserResponse>> getAllUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<UserResponse> users = userService.getAllUsers(page, size);
                return ResponseEntity.ok(users);
        }

        @Operation(summary = "Update user by admin", description = "Updates user information by admin. Can update all fields including role. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content)
        })
        @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UserResponse> updateUserByAdmin(
                        @Parameter(description = "User ID", required = true) @PathVariable Long id,
                        @RequestParam(value = "firstName", required = false) @Size(min = 2, max = 50) String firstName,
                        @RequestParam(value = "lastName", required = false) @Size(min = 2, max = 50) String lastName,
                        @RequestParam(value = "birthDate", required = false) String birthDate,
                        @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                        @RequestParam(value = "email", required = false) @Email String email,
                        @RequestParam(value = "password", required = false) @Size(min = 6) String password,
                        @RequestParam(value = "role", required = false) String role,
                        @RequestPart(value = "image", required = false) MultipartFile image) {

                UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                                firstName, lastName,
                                birthDate != null ? java.time.LocalDate.parse(birthDate) : null,
                                null, phoneNumber, email, password,
                                role != null ? com.skyroute.skyroute.user.enums.Role.valueOf(role) : null);
                UserResponse userResponse = userService.updateUserByAdmin(id, request, image);
                return ResponseEntity.ok(userResponse);
        }

        @Operation(summary = "Update user profile", description = "Updates user's own profile information. Users can only update their own profile.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content)
        })
        @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<UserResponse> updateUserProfile(
                        @RequestParam(value = "firstName", required = false) @Size(min = 2, max = 50) String firstName,
                        @RequestParam(value = "lastName", required = false) @Size(min = 2, max = 50) String lastName,
                        @RequestParam(value = "birthDate", required = false) String birthDate,
                        @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                        @RequestParam(value = "email", required = false) @Email String email,
                        @RequestParam(value = "password", required = false) @Size(min = 6) String password,
                        @RequestPart(value = "image", required = false) MultipartFile image) {

                UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                                firstName, lastName,
                                birthDate != null ? java.time.LocalDate.parse(birthDate) : null,
                                null, phoneNumber, email, password);
                UserResponse userResponse = userService.updateUserProfile(request, image);
                return ResponseEntity.ok(userResponse);
        }

        @Operation(summary = "Update user role", description = "Updates user role. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid role update request", content = @Content),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content)
        })
        @PatchMapping("/{id}/role")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UserResponse> updateUserRole(
                        @Parameter(description = "User ID", required = true) @PathVariable Long id,
                        @Valid @RequestBody RoleUpdateRequest roleRequest) {
                UserResponse userResponse = userService.updateUserRole(id, roleRequest);
                return ResponseEntity.ok(userResponse);
        }

        @Operation(summary = "Delete user", description = "Deletes a user by ID. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content)
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteUser(
                        @Parameter(description = "User ID", required = true) @PathVariable Long id) {
                userService.deleteUser(id);
                return ResponseEntity.noContent().build();
        }

}
