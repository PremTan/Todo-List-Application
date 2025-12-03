package com.mojoes.todo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {

    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime timestamp;
    private String type;
}
