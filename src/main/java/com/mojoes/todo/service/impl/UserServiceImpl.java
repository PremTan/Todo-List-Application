package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.UserRequest;
import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.DuplicateEmailException;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public UserResponse createUser(UserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists");
        }
        User user = mapper.map(request, User.class);
        User saved = userRepository.save(user);
        return mapper.map(saved, UserResponse.class);
    }
}
