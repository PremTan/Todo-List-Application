package com.mojoes.todo.service;

import com.mojoes.todo.dto.UserRequest;
import com.mojoes.todo.dto.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest request);
}
