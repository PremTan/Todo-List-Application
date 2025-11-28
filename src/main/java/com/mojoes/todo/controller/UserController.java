package com.mojoes.todo.controller;

import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.dto.UserUpdateRequest;
import com.mojoes.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Logged in users oprations")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get logged in user details",
                description = "Get current logged in user details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @Operation(summary = "Update user",
            description = "Update current logged in user details like email and name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/update/me")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(request));
    }

    @Operation(summary = "Delete user",
            description = "Delete current logged in user account")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/delete/me")
    public ResponseEntity<Void> delete() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }

}
