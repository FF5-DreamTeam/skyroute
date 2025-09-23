package com.skyroute.skyroute.user.service;

import com.skyroute.skyroute.user.dto.RoleUpdateRequest;
import com.skyroute.skyroute.user.dto.UserAdminUpdateRequest;
import com.skyroute.skyroute.user.dto.UserProfileUpdateRequest;
import com.skyroute.skyroute.user.dto.UserRequest;
import com.skyroute.skyroute.user.dto.UserResponse;
import com.skyroute.skyroute.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse createUser(UserRequest userRequest, MultipartFile image);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(int page, int size);

    UserResponse updateUserByAdmin(Long id, UserAdminUpdateRequest updateRequest, MultipartFile image);

    UserResponse updateUserProfile(UserProfileUpdateRequest updateRequest, MultipartFile image);

    void deleteUser(Long id);

    User findUserEntityById(Long id);

    User findUserEntityByEmail(String email);

    boolean existsByEmail(String email);

    UserResponse updateUserRole(Long id, RoleUpdateRequest roleRequest);
}
