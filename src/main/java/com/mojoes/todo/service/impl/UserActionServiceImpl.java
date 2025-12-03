package com.mojoes.todo.service.impl;

import com.mojoes.todo.entity.User;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.service.UserActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

    private final UserRepository userRepository;
    private final Map<Long, Boolean> online = new ConcurrentHashMap<>();

    @Override
    public void setOnline(Long userId) {
        online.put(userId, true);
    }

    @Override
    public void setOffline(Long userId) {
        online.put(userId, false);
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Override
    public boolean isOnline(Long userId) {
        return online.getOrDefault(userId, false);
    }

    @Override
    public LocalDateTime getLastSeen(Long userId) {
        return userRepository.findById(userId)
                .map(User::getLastSeen)
                .orElse(null);
    }
}
