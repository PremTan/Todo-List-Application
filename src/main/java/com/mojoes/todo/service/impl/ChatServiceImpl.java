package com.mojoes.todo.service.impl;

import com.mojoes.todo.dto.ChatMessageRequest;
import com.mojoes.todo.dto.ChatMessageResponse;
import com.mojoes.todo.entity.ChatMessage;
import com.mojoes.todo.entity.MessageType;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.exception.ResourceNotFoundException;
import com.mojoes.todo.mapper.ChatMapper;
import com.mojoes.todo.repository.ChatMessageRepository;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessage(ChatMessageRequest request) {

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        MessageType type;

        if (sender.getId() == 1 && receiver.getId() != 1) {
            type = MessageType.ADMIN_TO_USER;
        } else if (sender.getId() != 1 && receiver.getId() == 1) {
            type = MessageType.USER_TO_ADMIN;
        } else {
            type = MessageType.USER_TO_USER;
        }

        ChatMessage msg = ChatMessage.builder()
                .sender("USER_" + sender.getId())
                .receiver("USER_" + receiver.getId())
                .message(request.getMessage())
                .timestamp(LocalDateTime.now())
                .type(type)
                .build();

        ChatMessageResponse response = ChatMapper.toDto(chatMessageRepository.save(msg));

        // Send to sender + receiver
        simpMessagingTemplate.convertAndSend("/topic/chat/user/" + sender.getId(), response);
        simpMessagingTemplate.convertAndSend("/topic/chat/user/" + receiver.getId(), response);

        log.info("Delivered message to {} and {}", sender.getId(), receiver.getId());
    }

    @Override
    public void sendGroupMessage(ChatMessageRequest request) {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        ChatMessage msg = ChatMessage.builder()
                .sender("USER_" + sender.getId())
                .receiver("GROUP")
                .message(request.getMessage())
                .timestamp(LocalDateTime.now())
                .type(MessageType.GROUP_MESSAGE)
                .build();

        ChatMessageResponse response = ChatMapper.toDto(chatMessageRepository.save(msg));

        simpMessagingTemplate.convertAndSend("/topic/chat/group", response);
    }

}
