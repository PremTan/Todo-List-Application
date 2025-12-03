package com.mojoes.todo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private MessageType type;

}
