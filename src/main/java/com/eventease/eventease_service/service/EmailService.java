package com.eventease.eventease_service.service;

import com.eventease.eventease_service.config.EmailConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
        // Verify the configuration
        if (!emailConfig.verifyEmailConfig()) {
            throw new IllegalStateException("Email configuration verification failed");
        }
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // If sending fails, try to reinitialize the mail sender and retry once
            initializeMailSender();
            mailSender.send(message);
        }
    }
}