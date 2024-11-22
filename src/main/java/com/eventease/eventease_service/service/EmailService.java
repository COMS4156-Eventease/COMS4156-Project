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