package com.mojoes.todo.security;

import com.mojoes.todo.dto.AuthResponse;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private  final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User auth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();

        AuthResponse authResponse = userService.handleOAuth2LoginRequest(auth2User, registrationId);

        String jwt = authResponse.getToken();

//        response.sendRedirect("http://localhost:3000/oauth-success?token=" + jwt);

        response.setContentType("application/json");
        response.getWriter().write("Token : " + jwt);
    }

}
