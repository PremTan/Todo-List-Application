package com.mojoes.todo.service;

import com.mojoes.todo.dto.AuthRequest;
import com.mojoes.todo.dto.UserRequest;
import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.dto.UserUpdateRequest;

public interface UserService {

    UserResponse createUser(UserRequest request);

    void deleteUser();

    UserResponse updateUser(UserUpdateRequest request);

    String login(AuthRequest request);

    UserResponse getCurrentUser();
}
