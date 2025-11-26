package com.mojoes.todo.service;

import com.mojoes.todo.dto.*;
import jakarta.validation.Valid;

public interface UserService {

    UserResponse createUser(UserRequest request);

    void deleteUser();

    UserResponse updateUser(UserUpdateRequest request);

    String login(AuthRequest request);

    UserResponse getCurrentUser();

    void updatePassword(UpdatePasswordRequest request);

    void forgotPassword(ForgotPasswordRequest req);

    void resetPassword(ResetPasswordRequest request);
}
