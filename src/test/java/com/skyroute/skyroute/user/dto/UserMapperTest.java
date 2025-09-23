package com.skyroute.skyroute.user.dto;

import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    private User testUser;
    private UserRequest testUserRequest;
    private UserAdminUpdateRequest testAdminUpdateRequest;
    private UserProfileUpdateRequest testProfileUpdateRequest;
    private RoleUpdateRequest testRoleUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .userImgUrl("image.jpg")
                .role(Role.USER)
                .build();

        testUserRequest = new UserRequest("John", "Doe", "john@test.com", "password123");

        testAdminUpdateRequest = new UserAdminUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), "new-image.jpg", "0987654321", "jane@test.com", "newpassword", Role.ADMIN);

        testProfileUpdateRequest = new UserProfileUpdateRequest("Jane", "Smith",
                LocalDate.of(1992, 5, 15), "new-image.jpg", "0987654321", "jane@test.com", "newpassword");

        testRoleUpdateRequest = new RoleUpdateRequest(Role.ADMIN);
    }

    @Test
    void toEntity_WithUserRequest_ShouldReturnUser() {
        User result = userMapper.toEntity(testUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john@test.com");
        assertThat(result.getPassword()).isEqualTo("password123");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getBirthDate()).isNull();
        assertThat(result.getPhoneNumber()).isNull();
        assertThat(result.getUserImgUrl()).isNull();
    }

    @Test
    void toEntity_WithUserAdminUpdateRequest_ShouldReturnUser() {
        User result = userMapper.toEntity(testAdminUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("jane@test.com");
        assertThat(result.getPassword()).isEqualTo("newpassword");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1992, 5, 15));
        assertThat(result.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(result.getUserImgUrl()).isEqualTo("new-image.jpg");
    }

    @Test
    void toEntity_WithUserProfileUpdateRequest_ShouldReturnUser() {
        User result = userMapper.toEntity(testProfileUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("jane@test.com");
        assertThat(result.getPassword()).isEqualTo("newpassword");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1992, 5, 15));
        assertThat(result.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(result.getUserImgUrl()).isEqualTo("new-image.jpg");
    }

    @Test
    void toEntity_WithNullUserAdminUpdateRequest_ShouldReturnUserWithNulls() {
        UserAdminUpdateRequest nullRequest = new UserAdminUpdateRequest(null, null, null, null, null, null, null, null);
        User result = userMapper.toEntity(nullRequest);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getPassword()).isNull();
        assertThat(result.getRole()).isNull();
        assertThat(result.getBirthDate()).isNull();
        assertThat(result.getPhoneNumber()).isNull();
        assertThat(result.getUserImgUrl()).isNull();
    }

    @Test
    void toEntity_WithNullUserProfileUpdateRequest_ShouldReturnUserWithNulls() {
        UserProfileUpdateRequest nullRequest = new UserProfileUpdateRequest(null, null, null, null, null, null, null);
        User result = userMapper.toEntity(nullRequest);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getPassword()).isNull();
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getBirthDate()).isNull();
        assertThat(result.getPhoneNumber()).isNull();
        assertThat(result.getUserImgUrl()).isNull();
    }

    @Test
    void updateEntity_WithUserProfileUpdateRequest_ShouldUpdateOnlyNonNullFields() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest("Jane", null,
                LocalDate.of(1992, 5, 15), null, "0987654321", null, "newpassword");

        userMapper.updateEntity(testUser, updateRequest);

        assertThat(testUser.getFirstName()).isEqualTo("Jane");
        assertThat(testUser.getLastName()).isEqualTo("Doe");
        assertThat(testUser.getEmail()).isEqualTo("john@test.com");
        assertThat(testUser.getPassword()).isEqualTo("newpassword");
        assertThat(testUser.getBirthDate()).isEqualTo(LocalDate.of(1992, 5, 15));
        assertThat(testUser.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(testUser.getUserImgUrl()).isEqualTo("image.jpg");
    }

    @Test
    void updateEntity_WithUserAdminUpdateRequest_ShouldUpdateOnlyNonNullFields() {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest("Jane", null,
                LocalDate.of(1992, 5, 15), null, "0987654321", null, "newpassword", Role.ADMIN);

        userMapper.updateEntity(testUser, updateRequest);

        assertThat(testUser.getFirstName()).isEqualTo("Jane");
        assertThat(testUser.getLastName()).isEqualTo("Doe");
        assertThat(testUser.getEmail()).isEqualTo("john@test.com");
        assertThat(testUser.getPassword()).isEqualTo("newpassword");
        assertThat(testUser.getRole()).isEqualTo(Role.ADMIN);
        assertThat(testUser.getBirthDate()).isEqualTo(LocalDate.of(1992, 5, 15));
        assertThat(testUser.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(testUser.getUserImgUrl()).isEqualTo("image.jpg");
    }

    @Test
    void updateEntity_WithNullUserProfileUpdateRequest_ShouldNotUpdateUser() {
        UserProfileUpdateRequest nullRequest = new UserProfileUpdateRequest(null, null, null, null, null, null, null);
        String originalFirstName = testUser.getFirstName();
        String originalLastName = testUser.getLastName();
        String originalEmail = testUser.getEmail();

        userMapper.updateEntity(testUser, nullRequest);

        assertThat(testUser.getFirstName()).isEqualTo(originalFirstName);
        assertThat(testUser.getLastName()).isEqualTo(originalLastName);
        assertThat(testUser.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    void updateEntity_WithNullUserAdminUpdateRequest_ShouldNotUpdateUser() {
        UserAdminUpdateRequest nullRequest = new UserAdminUpdateRequest(null, null, null, null, null, null, null, null);
        String originalFirstName = testUser.getFirstName();
        String originalLastName = testUser.getLastName();
        String originalEmail = testUser.getEmail();
        Role originalRole = testUser.getRole();

        userMapper.updateEntity(testUser, nullRequest);

        assertThat(testUser.getFirstName()).isEqualTo(originalFirstName);
        assertThat(testUser.getLastName()).isEqualTo(originalLastName);
        assertThat(testUser.getEmail()).isEqualTo(originalEmail);
        assertThat(testUser.getRole()).isEqualTo(originalRole);
    }

    @Test
    void updateRole_WithValidRole_ShouldUpdateRole() {
        userMapper.updateRole(testUser, testRoleUpdateRequest);

        assertThat(testUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void updateRole_WithNullRole_ShouldNotUpdateRole() {
        RoleUpdateRequest nullRequest = new RoleUpdateRequest(null);
        Role originalRole = testUser.getRole();

        userMapper.updateRole(testUser, nullRequest);

        assertThat(testUser.getRole()).isEqualTo(originalRole);
    }

    @Test
    void toResponse_WithValidUser_ShouldReturnUserResponse() {
        LocalDateTime now = LocalDateTime.now();
        testUser.setCreatedAt(now);
        testUser.setUpdatedAt(now);

        UserResponse result = userMapper.toResponse(testUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.email()).isEqualTo("john@test.com");
        assertThat(result.role()).isEqualTo(Role.USER);
        assertThat(result.birthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(result.phoneNumber()).isEqualTo("1234567890");
        assertThat(result.userImgUrl()).isEqualTo("image.jpg");
        assertThat(result.createdAt()).isEqualTo(now);
        assertThat(result.updatedAt()).isEqualTo(now);
    }

    @Test
    void toResponse_WithUserWithNullFields_ShouldReturnUserResponseWithNulls() {
        User userWithNulls = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("password")
                .role(Role.ADMIN)
                .build();

        UserResponse result = userMapper.toResponse(userWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.firstName()).isEqualTo("Jane");
        assertThat(result.lastName()).isEqualTo("Smith");
        assertThat(result.email()).isEqualTo("jane@test.com");
        assertThat(result.role()).isEqualTo(Role.ADMIN);
        assertThat(result.birthDate()).isNull();
        assertThat(result.phoneNumber()).isNull();
        assertThat(result.userImgUrl()).isNull();
        assertThat(result.createdAt()).isNull();
        assertThat(result.updatedAt()).isNull();
    }
}
