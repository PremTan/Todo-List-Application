package com.mojoes.todo.scheduler;

import com.mojoes.todo.entity.User;
import com.mojoes.todo.repository.UserRepository;
import com.mojoes.todo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockedUserEmailScheduler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * 1")
    public void sendEmailsToBlockedUsers() {
        List<User> usersList = userRepository.findAll();

        List<User> blockedUsersList = usersList.stream()
                .filter(User::isBlocked)
                .toList();

        blockedUsersList.forEach(user -> {
            String msg = """
                    <h3>Hello %s,</h3>
                    <p>Your account is currently blocked. Please contact support.</p>
                    """.formatted(user.getName());

            emailService.sendHtmlEmail(user.getEmail(),
                    "Your Account is Blocked",
                    msg);
            log.info("Sent block user notification email to : {}", user.getEmail());
        });
        log.info("Completed sending emails to blocked users : {}", blockedUsersList.size());
    }
}
