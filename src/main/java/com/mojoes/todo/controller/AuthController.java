package com.mojoes.todo.controller;

import com.mojoes.todo.dto.*;
import com.mojoes.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication Controller", description = "Operations like Login, Register and Password related")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Register new user",
            description ="Register new user with email, name and password")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @Operation(summary = "Login user",
            description ="Registered users can login with email and password and it returns jwt token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User login successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(summary = "Update password",
            description ="Logged in user can update password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @Operation(summary = "Forgot password",
            description ="Forgot password send OTP to users mail to reset password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP sent to mail successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok("OTP sent to your email");
    }

    @Operation(summary = "Reset password",
            description ="Reset password  via OTP from email and enter new password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }

}
