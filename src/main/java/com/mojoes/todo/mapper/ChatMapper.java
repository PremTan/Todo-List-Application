package com.mojoes.todo.mapper;

import com.mojoes.todo.dto.ChatMessageResponse;
import com.mojoes.todo.entity.ChatMessage;

public class ChatMapper {

    public static ChatMessageResponse toDto(ChatMessage message) {

        Long senderId = extractId(message.getSender());
        Long receiverId = extractId(message.getReceiver());

        return ChatMessageResponse.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .message(message.getMessage())
                .timestamp(message.getTimestamp())
                .type(message.getType().name())
                .build();
    }

    private static Long extractId(String value) {
        if (value == null || !value.contains("_")) return null;
        try {
            return Long.parseLong(value.substring(value.indexOf("_") + 1));
        } catch (Exception e) {
            return null;
        }
    }

}
