package com.mojoes.todo.controller;

import com.mojoes.todo.dto.ChatMessageRequest;
import com.mojoes.todo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/send")
    public void send(ChatMessageRequest request, Principal principal) {
        log.info("Message sent from {} to {}", request.getSenderId(), request.getReceiverId());
        long senderId = Long.parseLong(principal.getName());
        request.setSenderId(senderId);
        chatService.sendMessage(request);
    }

    @MessageMapping("/chat/group")
    public void groupMessage(ChatMessageRequest request, Principal principal) {
        log.info("Message sent from {} to all", request.getSenderId());
        long senderId = Long.parseLong(principal.getName());
        request.setSenderId(senderId);
        chatService.sendGroupMessage(request);
    }

}
