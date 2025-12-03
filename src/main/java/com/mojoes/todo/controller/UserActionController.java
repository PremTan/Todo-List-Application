package com.mojoes.todo.controller;

import com.mojoes.todo.service.UserActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/action")
@RequiredArgsConstructor
public class UserActionController {

    private final UserActionService userActionService;

    @GetMapping("/{userId}")
    public Map<String, Object> getUserAction(@PathVariable("userId") Long userId){

        boolean online = userActionService.isOnline(userId);
        LocalDateTime lastSeen = userActionService.getLastSeen(userId);

        return Map.of("online", online, "lastSeen", lastSeen);
    }
}
