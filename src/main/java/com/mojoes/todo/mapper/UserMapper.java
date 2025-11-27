package com.mojoes.todo.mapper;

import com.mojoes.todo.dto.UserRequest;
import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.dto.UserUpdateRequest;
import com.mojoes.todo.entity.User;

public class UserMapper {

    public static User toEntity(UserRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    public static UserResponse toDto(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static void updateEntity(User user, UserUpdateRequest req) {
        user.setName(req.getName());
        user.setEmail(req.getEmail());
    }

}
