package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.*;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.DuplicateEmailException;
import com.mojoes.todo.exception.ResourceNotFoundException;
import com.mojoes.todo.mapper.UserMapper;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.security.JwtUtil;
import com.mojoes.todo.security.SecurityUtil;
import com.mojoes.todo.service.EmailService;
import com.mojoes.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    @Override
    public void deleteUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Deleting user = {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.deleteById(user.getId());
        log.info("User deleted = {}", user.getId());

        String body = "Hi " + user.getName()+
                "\n\nYour account is deleted successfully.\n\n"+
                "Todo List app.";
        emailService.sendSimpleEmail(email, "Account Deleted", body);
        log.info("User deleted mail sent to : {}", email);
    }

    @Transactional
    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("Creating new user = {}", request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists");
        }
        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        log.debug("User saved : {}", user);

        String body =
                "<h2 style='color:green;'>User Registered successfully....</h2>" +
                        "<p>Hi " + user.getName() + ",</p>" +
                        "<p>User Details :</p>" +
                        "<b>Id :</b>"+user.getId() +"<br>" +
                        "<b>Name :</b> "+user.getName()+"<br>" +
                        "<b>Email :</b>"+user.getEmail()+"<br><br>" +
                        "Thank you for register.";

        emailService.sendHtmlEmail(user.getEmail(), "Welcome to Todo List App", body);
        log.info("Registration email sent to user email id : {}", user.getEmail());
        return UserMapper.toDto(saved);
    }

    @Transactional
    @Override
    public UserResponse updateUser(UserUpdateRequest req) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Updating user = {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(req.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        UserMapper.updateEntity(user, req);

        User saved = userRepository.save(user);
        log.debug("User updated and saved : {}", saved.getEmail());

        String body = "Hi " + saved.getName()+
                "\n\nUser updated successfuly..\n\n"+
                "Todo List app.";
        emailService.sendSimpleEmail(email, "User Account Updated", body);
        log.info("User update email sent : {}", saved.getEmail());
        return UserMapper.toDto(saved);
    }

    @Override
    public String login(AuthRequest request) {
        log.info("Attempting login : {}", request.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("Login successful : {}", request.getEmail());
        return jwtUtil.generateToken(user);
    }

    @Override
    public UserResponse getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Fetching current user = {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public void updatePassword(UpdatePasswordRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("Updating password for user = {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password updated for user : {}", email);

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your password is changed successfully.\n\n" +
                "Todo List App";
        emailService.sendSimpleEmail(user.getEmail(), "Password Changed", body);
        log.info("Updated password successfully mail sent to user : {}", email);
    }

    @Transactional
    @Override
    public void forgotPassword(ForgotPasswordRequest req) {
        log.info("Generating OTP for user = {}", req.getEmail());

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SecureRandom random = new SecureRandom();
        String collect = random.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        user.setResetOtp(collect);
        userRepository.save(user);
        log.info("OTP generated for {}", req.getEmail());

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your OTP for password reset is : " + collect + "\n" +
                "Todo List App";

        emailService.sendSimpleEmail(user.getEmail(), "Password reset OTP", body);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password for email = {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!request.getOtp().equals(user.getResetOtp())){
            log.error("Invalid OTP for email = {}", request.getEmail());
            throw new IllegalArgumentException("Invalid OTP");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        userRepository.save(user);

        log.info("Password reset successful : {}", request.getEmail());

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your password is reset successfully.."+
                "Todo List App";

        emailService.sendSimpleEmail(user.getEmail(), "Password reset successfully.", body);
    }

}
