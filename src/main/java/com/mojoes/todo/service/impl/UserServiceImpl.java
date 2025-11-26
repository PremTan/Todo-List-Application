package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.*;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.DuplicateEmailException;
import com.mojoes.todo.exception.ResourceNotFoundException;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.security.JwtUtil;
import com.mojoes.todo.security.SecurityUtil;
import com.mojoes.todo.service.EmailService;
import com.mojoes.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public UserResponse createUser(UserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists");
        }
        User user = mapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);

        String body =
                "<h2 style='color:green;'>User Registered successfully....</h2>" +
                        "<p>Hi " + user.getName() + ",</p>" +
                        "<p>User Details :</p>" +
                        "<b>Id :</b>"+user.getId() +"<br>" +
                        "<b>Name :</b> "+user.getName()+"<br>" +
                        "<b>Email :</b>"+user.getEmail()+"<br><br>" +
                        "Thank you for register.";

        emailService.sendHtmlEmail(user.getEmail(), "Welcome to Todo List App", body);
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

        String body = "Hi " + saved.getName()+
                "\n\nUser updated successfuly..\n\n"+
                "Todo List app.";
        emailService.sendSimpleEmail(email, "User Account Updated", body);
        return mapper.map(saved, UserResponse.class);
    }

    @Override
    public void deleteUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.deleteById(user.getId());

        String body = "Hi " + user.getName()+
                "\n\nYour account is deleted successfully.\n\n"+
                "Todo List app.";
        emailService.sendSimpleEmail(email, "Account Deleted", body);
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

    @Override
    public void updatePassword(UpdatePasswordRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your password is changed successfully.\n\n" +
                "Todo List App";
        emailService.sendSimpleEmail(user.getEmail(), "Password Changed", body);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SecureRandom random = new SecureRandom();
        String collect = random.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        user.setResetOtp(collect);
        userRepository.save(user);

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your OTP for password reset is : " + collect + "\n" +
                "Todo List App";

        emailService.sendSimpleEmail(user.getEmail(), "Password reset OTP", body);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!request.getOtp().equals(user.getResetOtp())){
            throw new IllegalArgumentException("Invalid OTP");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        userRepository.save(user);

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your password is reset successfully.."+
                "Todo List App";

        emailService.sendSimpleEmail(user.getEmail(), "Password reset successfully.", body);
    }

}
