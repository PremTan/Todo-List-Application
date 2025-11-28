package com.mojoes.todo.config;

import com.mojoes.todo.entity.Role;
import com.mojoes.todo.security.CustomAuthenticationEntryPoint;
import com.mojoes.todo.security.JwtAuthFilter;
import com.mojoes.todo.security.Oauth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final CustomAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.getLabel())
                        .requestMatchers("/api/todos/**").hasAnyRole(Role.ADMIN.getLabel(), Role.USER.getLabel())
                        .requestMatchers("/api/users/**").hasAnyRole(Role.ADMIN.getLabel(), Role.USER.getLabel())
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
                        .failureHandler((request, response, ex) -> {
                            log.error("OAuth Error : {}", ex.getMessage());
                        })
                        .successHandler(oauth2SuccessHandler));
        return security.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
