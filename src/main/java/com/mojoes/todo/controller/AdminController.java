package com.mojoes.todo.controller;

import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Controller", description = "Admin Operations on users")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Get all users",
            description = "Get list of all registered users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can access")
    })
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get single user",
            description ="Get single registered user by user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can access")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Block user",
            description ="Set the user account as block account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User blocked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can access"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/block/{id}")
    public ResponseEntity<String> blockUser(@PathVariable("id") Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok("User blocked successfully.");
    }

    @Operation(summary = "Unblock user",
            description ="Removes block status of a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User unblocked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can access"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/unblock/{id}")
    public ResponseEntity<String> unblockUser(@PathVariable("id") Long id) {
        userService.unBlockUser(id);
        return ResponseEntity.ok("User unblocked successfully.");
    }

}




