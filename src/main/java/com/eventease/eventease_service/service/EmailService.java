package com.eventease.eventease_service.service;

import com.eventease.eventease_service.config.EmailConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailConfig emailConfig;
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
        initializeMailSender();
    }

    private void initializeMailSender() {
        this.mailSender = emailConfig.getJavaMailSender();
    }

    public void sendEmail(String to, String subject, String text) {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("The 'to' email cannot be null or empty.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject != null ? subject : "");
        message.setText(text != null ? text : "");

        int maxRetries = 3;
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                mailSender.send(message);
                return;
            } catch (Exception e) {
                lastException = e;
                if (e instanceof IllegalArgumentException) {
                    throw e;
                }
                initializeMailSender();
            }
        }
        throw new RuntimeException(lastException.getMessage(), lastException);
    }
}