package com.mojoes.todo.service;

import com.mojoes.todo.dto.*;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    void deleteUser();

    UserResponse updateUser(UserUpdateRequest request);

    String login(AuthRequest request);

    UserResponse getCurrentUser();

    void updatePassword(UpdatePasswordRequest request);

    void forgotPassword(ForgotPasswordRequest req);

    void resetPassword(ResetPasswordRequest request);

    void blockUser(Long userId);

    void unBlockUser(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    AuthResponse handleOAuth2LoginRequest(OAuth2User auth2User, String registrationId);
}
