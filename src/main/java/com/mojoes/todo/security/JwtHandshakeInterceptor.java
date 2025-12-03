package com.mojoes.todo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        String token = servletRequest.getServletRequest().getParameter("token");

        if (token == null || token.isBlank()) {
            return false;
        }

        if (jwtUtil.isTokenExpired(token)) {
            return false;
        }

        String email = jwtUtil.getEmailFromClaims(token);
        Long userId = jwtUtil.getIdFromClaims(token);
        String username = jwtUtil.getNameFromClaims(token);

        if (email == null) {
            return false;
        }

        attributes.put("token", token);
        attributes.put("email", email);
        attributes.put("userId", userId);
        attributes.put("username", username);

        log.info("WebSocket Authenticated User: {} : {}", username, userId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
    }

}
