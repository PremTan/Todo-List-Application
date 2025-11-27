package com.mojoes.todo.config;

import com.mojoes.todo.entity.Role;
import com.mojoes.todo.entity.User;
import com.mojoes.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@todoapp.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists.");
            return;
        }

        User admin = new User();
        admin.setName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setBlocked(false);

        userRepository.save(admin);

        log.info("Default ADMIN created successfully..");
        log.info("Email : {}", adminEmail);
        log.info("Password : admin123");
    }
}
