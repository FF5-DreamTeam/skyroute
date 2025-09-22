package com.skyroute.skyroute.auth.service;

import com.skyroute.skyroute.auth.dto.LoginRequest;
import com.skyroute.skyroute.auth.dto.LoginResponse;
import com.skyroute.skyroute.auth.dto.RefreshTokenRequest;
import com.skyroute.skyroute.auth.dto.RegisterResponse;
import com.skyroute.skyroute.user.dto.UserRequest;

public interface AuthService {
    RegisterResponse register(UserRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
    void logout(String refreshToken);
}
