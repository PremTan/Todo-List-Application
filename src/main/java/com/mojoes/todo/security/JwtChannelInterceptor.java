package com.mojoes.todo.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(accessor == null) return message;

        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            Map<String, Object> session = accessor.getSessionAttributes();
            if(session == null) return message;

            String token = (String) session.get("token");
            Long userId = (Long) session.get("userId");

            if (token == null || userId == null){
                throw new IllegalArgumentException("No JWT token in WS session");
            }

            String email = jwtUtil.getEmailFromClaims(token);
            if (email == null) {
                throw new IllegalArgumentException("Invalid JWT token: no email inside");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (!jwtUtil.isTokenValid(token, userDetails)) {
                throw new IllegalArgumentException("Invalid or expired JWT token");
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId.toString(), null, userDetails.getAuthorities());

            accessor.setUser(auth);
        }

        return message;
    }
}
