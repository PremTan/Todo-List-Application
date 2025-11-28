package com.mojoes.todo.security;

import com.mojoes.todo.entity.AuthProviderType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getCurrentUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

}
