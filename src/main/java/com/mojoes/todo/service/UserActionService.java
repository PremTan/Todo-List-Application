package com.mojoes.todo.service;

import java.time.LocalDateTime;

public interface UserActionService {

    void setOnline(Long userId);

    void setOffline(Long userId);

    boolean isOnline(Long userId);

    LocalDateTime getLastSeen(Long userId);

}
