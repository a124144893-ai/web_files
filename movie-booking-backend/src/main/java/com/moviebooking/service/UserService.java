package com.moviebooking.service;

import com.moviebooking.dto.request.LoginRequest;
import com.moviebooking.dto.request.RegisterRequest;
import com.moviebooking.dto.response.LoginResponse;
import com.moviebooking.dto.response.UserResponse;

public interface UserService {
    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    UserResponse getCurrentUserProfile();

    UserResponse getUserById(Long id);

    UserResponse updateUserProfile(UserResponse userResponse);
}
