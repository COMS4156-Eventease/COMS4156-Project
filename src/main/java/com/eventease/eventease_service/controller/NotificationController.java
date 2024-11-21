package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final TwilioService twilioService;

    @Autowired
    public NotificationController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @GetMapping("/send-notification")
    public String sendNotification() {
        String recipientNumber = "+12135514209";
        String message = "Hello, this is a test notification from EventEase.";

        twilioService.sendSms(recipientNumber, message);
        return "Notification sent!";
    }
}