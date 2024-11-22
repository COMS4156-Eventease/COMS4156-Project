package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final TwilioService twilioService;
    private static final String RECIPIENT_NUMBER = "+12135514209";
    private static final String TEST_MESSAGE = "Hello, this is a test notification from EventEase.";

    @Autowired
    public NotificationController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @GetMapping("/send-notification")
    public ResponseEntity<String> sendNotification() {
        try {
            twilioService.sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);
            return ResponseEntity.ok("Notification sent!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid phone number format");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to send notification: " + e.getMessage());
        }
    }
}