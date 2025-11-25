package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.AuthRequest;
import com.mojoes.todo.dto.UserRequest;
import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.dto.UserUpdateRequest;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.DuplicateEmailException;
import com.mojoes.todo.exception.ResourceNotFoundException;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.security.JwtUtil;
import com.mojoes.todo.security.SecurityUtil;
import com.mojoes.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse createUser(UserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists");
        }
        User user = mapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        return mapper.map(saved, UserResponse.class);
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest req) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(req.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        user.setEmail(req.getEmail());
        user.setName(req.getName());
        User saved = userRepository.save(user);
        return mapper.map(saved, UserResponse.class);
    }

    @Override
    public void deleteUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.deleteById(user.getId());
    }

    @Override
    public String login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return jwtUtil.generateToken(user);
    }

    @Override
    public UserResponse getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapper.map(user, UserResponse.class);
    }

}
