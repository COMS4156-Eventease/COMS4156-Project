package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.EmailService;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.TwilioService;
import com.eventease.eventease_service.service.UserService;
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
    private final UserService userService;
    private final EventService eventService;

    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    public NotificationController(TwilioService twilioService,
                                  EmailService emailService,
                                  UserService userService,
                                  EventService eventService) {
        this.twilioService = twilioService;
        this.emailService = emailService;
        this.eventService = eventService;
        this.userService = userService;
    }


    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            String userIdString = (String) request.get("userId");
            String eventIdString = (String) request.get("eventId");
            if (userIdString == null || userIdString.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("User ID is required");
            }

            Long userId;
            try {
                userId = Long.parseLong(userIdString);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid User ID format");
            }
            Long eventId;
            try {
                eventId = Long.parseLong(eventIdString);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid Event ID format");
            }

            String message = (String) request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is required");
            }

            User user;
            Event event;
            try {
                user = userService.findUserById(userId);
            } catch (UserNotExistException e) {
                return ResponseEntity.badRequest().body("User does not exist: " + e.getMessage());
            }
            try {
                event = eventService.findById(eventId);
            } catch (EventNotExistException e) {
                return ResponseEntity.badRequest().body("Event does not exist: " + e.getMessage());
            }
            System.out.println(event);
            String formattedMessage = String.format("Dear %s %s,\n%s",
                    user.getFirstName(), user.getLastName(), message);


            String phoneNumber = user.getPhoneNumber();
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("User's phone number is not available");
            }

            phoneNumber = phoneNumber.replace("-", "").trim();
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+1" + phoneNumber; // Assuming "+1" as the default area code
            }

            if (!phoneNumber.matches("\\+\\d{10,15}")) {
                return ResponseEntity.badRequest().body("Invalid phone number format");
            }

            twilioService.sendSms(phoneNumber, formattedMessage);
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
            String userIdString = (String) request.get("userId");
            String eventIdString = (String) request.get("eventId");
            String subject = (String) request.get("subject");
            String message = (String) request.get("message");

            if (userIdString == null || userIdString.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("User ID is required");
            }
            if (eventIdString == null || eventIdString.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Event ID is required");
            }
            if (subject == null || subject.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Subject is required");
            }
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is required");
            }

            Long userId;
            Long eventId;
            try {
                userId = Long.parseLong(userIdString);
                eventId = Long.parseLong(eventIdString);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid ID format");
            }

            // Get user and event
            User user = userService.findUserById(userId);
            Event event = eventService.findById(eventId);

            // Validate email
            if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                return ResponseEntity.badRequest().body("Invalid user email");
            }

            String formattedMessage = String.format("Dear %s %s,\n\n%s",
                    user.getFirstName(), user.getLastName(), message);

            emailService.sendEmail(user.getEmail(), subject, formattedMessage);
            return ResponseEntity.ok("Email sent successfully!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request format: " + e.getMessage());
        } catch (UserNotExistException e) {
            return ResponseEntity.badRequest().body("User does not exist: " + e.getMessage());
        } catch (EventNotExistException e) {
            return ResponseEntity.badRequest().body("Event does not exist: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
        }
    }
}