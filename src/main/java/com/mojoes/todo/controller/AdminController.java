package com.mojoes.todo.controller;

import com.mojoes.todo.dto.UserResponse;
import com.mojoes.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/block/{id}")
    public ResponseEntity<String> blockUser(@PathVariable("id") Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok("User blocked successfully.");
    }

    @PatchMapping("/unblock/{id}")
    public ResponseEntity<String> unblockUser(@PathVariable("id") Long id) {
        userService.unBlockUser(id);
        return ResponseEntity.ok("User unblocked successfully.");
    }

}
