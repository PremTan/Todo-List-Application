package com.mojoes.todo.service.impl;

import com.mojoes.todo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    // Simple Email
    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
            log.info("Simple Email sent to : {}",to);
        }catch (Exception e){
            log.error("Unexpected error while sending HTML email to {}. Error: {}",to, e.getMessage(), e);
        }

    }

    // Html template base d email
    @Async
    @Override
    public void sendHtmlEmail(String to, String subject, String body) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            mailSender.send(message);
            log.info("HTML Email sent to : {}",to);
        } catch (MessagingException e) {
            log.error("Unexpected error while sending HTML email to {}. Error: {}", to, e.getMessage(), e);
        }

    }
}
