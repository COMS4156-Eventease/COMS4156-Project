package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.service.EmailService;
import com.eventease.eventease_service.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class NotificationController {
    private final TwilioService twilioService;
    private final EmailService emailService;

    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    public NotificationController(TwilioService twilioService, EmailService emailService) {
        this.twilioService = twilioService;
        this.emailService = emailService;
    }


    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            String phoneNumber = (String) request.get("number");
            String message = (String) request.get("message");

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Phone number is required");
            }
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is required");
            }

            if (!phoneNumber.startsWith("+")) {
                return ResponseEntity.badRequest().body("Phone number must start with +");
            }

            twilioService.sendSms(phoneNumber, message);
            return ResponseEntity.ok("Notification sent!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to send notification: " + e.getMessage());
        }
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, Object> request) {
        try {
            String to = (String) request.get("to");
            String subject = (String) request.get("subject");
            String message = (String) request.get("message");

            // Validate required fields
            if (to == null || to.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Recipient email is required");
            }
            if (subject == null || subject.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Subject is required");
            }
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is required");
            }

            // Validate email format
            if (!EMAIL_PATTERN.matcher(to).matches()) {
                return ResponseEntity.badRequest().body("Invalid email format");
            }

            emailService.sendEmail(to, subject, message);
            return ResponseEntity.ok("Email sent successfully!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to send email: " + e.getMessage());
        }
    }
}