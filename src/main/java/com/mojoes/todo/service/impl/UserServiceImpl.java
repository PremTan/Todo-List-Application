package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.*;
import com.mojoes.todo.entity.AuthProviderType;
import com.mojoes.todo.entity.PasswordReset;
import com.mojoes.todo.entity.Role;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.DuplicateEmailException;
import com.mojoes.todo.exception.ResourceNotFoundException;
import com.mojoes.todo.mapper.UserMapper;
import com.mojoes.todo.repository.PasswordResetRepository;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final PasswordResetRepository passwordResetRepository;

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
        String otp = random.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        passwordResetRepository.deleteByEmail(req.getEmail());
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setOtp(otp);
        passwordReset.setEmail(req.getEmail());
        passwordReset.setExpiryTime(LocalDateTime.now().plusSeconds(30));

        passwordResetRepository.save(passwordReset);

        log.info("OTP generated for {}", req.getEmail());

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your OTP for password reset is : " + otp + "\n" +
                "Todo List App";

        emailService.sendSimpleEmail(user.getEmail(), "Password reset OTP", body);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password for email = {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PasswordReset passwordReset = passwordResetRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("OTP not found"));

        if(passwordReset.getExpiryTime().isBefore(LocalDateTime.now())){
            log.error("OTP is Expired = {}", request.getEmail());
            throw new IllegalArgumentException("OTP is expired.. Generate new OTP again.");
        }

        if(!request.getOtp().equals(passwordReset.getOtp())){
            log.error("Invalid OTP for email = {}", request.getEmail());
            throw new IllegalArgumentException("Invalid OTP");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetRepository.deleteByEmail(request.getEmail());

        log.info("Password reset successful : {}", request.getEmail());

        String body = "Hi " + user.getName() + ",\n\n" +
                "Your password is reset successfully.."+
                "Todo List App";

        emailService.sendSimpleEmail(user.getEmail(), "Password reset successfully.", body);
    }

    @Transactional
    @Override
    public void blockUser(Long userId) {
        String currentEmail = SecurityUtil.getCurrentUserEmail();

        User admin = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(admin.getId().equals(user.getId())){
            throw new IllegalArgumentException("Admin cannot block himself..");
        }

        if (user.isBlocked()) {
            throw new IllegalStateException("User is already blocked.");
        }

        user.setBlocked(true);
        userRepository.save(user);
        log.info("Blocked user successfully : {}",user.getEmail());
    }

    @Transactional
    @Override
    public void unBlockUser(Long userId) {
        String currentEmail = SecurityUtil.getCurrentUserEmail();

        User admin = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (admin.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Admin cannot unblock himself.");
        }

        if (!user.isBlocked()) {
            throw new IllegalStateException("User is already unblocked.");
        }

        user.setBlocked(false);
        userRepository.save(user);
        log.info("Unblocked the user : {}",user.getEmail());
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public AuthResponse handleOAuth2LoginRequest(OAuth2User auth2User, String registrationId) {
        AuthProviderType providerType = jwtUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = jwtUtil.getProviderIdFromOAuth2User(auth2User, registrationId);

        String email = auth2User.getAttribute("email");
        String name = auth2User.getAttribute("name");

        if(email == null){
            email = providerType + "_" + providerId + "@oauth.user";
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : providerType + " User");
            user.setProvider(providerType);
            user.setProviderId(providerId);
            user.setOauthUser(true);
            user.setRole(Role.USER);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            userRepository.save(user);
        }

        String jwt = jwtUtil.generateToken(user);

        return new AuthResponse(jwt);
    }

}
