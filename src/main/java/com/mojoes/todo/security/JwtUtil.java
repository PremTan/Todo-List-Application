package com.mojoes.todo.security;

import com.mojoes.todo.entity.AuthProviderType;
import com.mojoes.todo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationTimeMS;

    private Key getkey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user){
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationTimeMS);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(exp)
                .addClaims(Map.of("email", user.getEmail(),
                        "id", user.getId(),
                        "name", user.getName()))
                .signWith(getkey())
                .compact();
    }

    private Claims getClaims(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getkey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }

    }

    public String getEmailFromClaims(String token){
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (token == null || userDetails == null) return false;
        String email = getEmailFromClaims(token);
        return email != null && email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        try{
            return getClaims(token).getExpiration().before(new Date());
        }catch (ExpiredJwtException e){
            return true;
        }
    }

    public AuthProviderType getProviderTypeFromRegistrationId(String registrationId){
        return switch (registrationId.toLowerCase()){
            case "google" -> AuthProviderType.GOOGLE;
            case "github" -> AuthProviderType.GITHUB;
            case "facebook" -> AuthProviderType.FACEBOOK;
            default -> {
                log.error("Unknown OAuth2 provider : {}", registrationId);
                throw new IllegalArgumentException("Unsupported OAuth2 provider : "+registrationId);
            }
        };
    }

    public String getProviderIdFromOAuth2User(OAuth2User user, String registrationId){
        Object providerId = switch (registrationId.toLowerCase()){
            case "google" -> user.getAttribute("sub");
            case "github" -> user.getAttribute("id");
            case "facebook" -> user.getAttribute("id");
            default -> {
                log.error("Unknown OAuth2 provider for user ID : {}", registrationId);
                throw new IllegalArgumentException("Unsupported OAuth2 provider for provider ID");
            }
        };
        if(providerId == null){
            log.error("Unable to determine provider Id for provider : {}", registrationId);
            throw new IllegalArgumentException("Unable to extract provider ID from OAuth2User");
        }
        return String.valueOf(providerId);
    }

}
