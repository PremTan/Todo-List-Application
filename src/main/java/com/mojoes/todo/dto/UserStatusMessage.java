package com.mojoes.todo.dto;

import com.mojoes.todo.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusMessage {

    private Long userId;
    private UserStatus userStatus;
    private LocalDateTime lastSeen;
}
