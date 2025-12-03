package com.mojoes.listener;

import com.mojoes.todo.dto.UserStatusMessage;
import com.mojoes.todo.entity.UserStatus;
import com.mojoes.todo.service.UserActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketActiveStatusListener {

    private final UserActionService userActionService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void onConnect(SessionConnectedEvent connectedEvent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(connectedEvent.getMessage());

        Principal principal = accessor.getUser();
        if (principal != null) {
            Long userId = Long.parseLong(principal.getName());

            userActionService.setOnline(userId);
            simpMessagingTemplate.convertAndSend("/topic/user-status", new UserStatusMessage(userId, UserStatus.ONLINE, null));

            log.info("User {} is ONLINE", userId);
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent disconnectEvent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(disconnectEvent.getMessage());

        Principal principal = accessor.getUser();
        if (principal != null) {
            Long userId = Long.parseLong(principal.getName());

            userActionService.setOffline(userId);
            LocalDateTime lastSeen = userActionService.getLastSeen(userId);

            simpMessagingTemplate.convertAndSend("/topic/user-status", new UserStatusMessage(userId, UserStatus.OFFLINE, lastSeen));

            log.info("User {} is OFFLINE and LastSeen is {}", userId, lastSeen);
        }
    }
}
