package com.mojoes.todo.service;

import com.mojoes.todo.dto.ChatMessageRequest;

public interface ChatService {

    void sendMessage(ChatMessageRequest request);

    void sendGroupMessage(ChatMessageRequest request);
}
