package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.NotificationController;
import com.eventease.eventease_service.service.EmailService;
import com.eventease.eventease_service.service.TwilioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerUnitTest {

    @Mock
    private TwilioService twilioService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationController notificationController;

    private static final String RECIPIENT_NUMBER = "+12135514209";
    private static final String TEST_MESSAGE = "Hello, this is a test notification from EventEase.";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SUBJECT = "Test Subject";

    @Test
    void testSendMessage_Success() {
        Map<String, Object> request = new HashMap<>();
        request.put("number", RECIPIENT_NUMBER);
        request.put("message", TEST_MESSAGE);

        doNothing().when(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Notification sent!", response.getBody());
        verify(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);
    }

    @Test
    void testSendMessage_MissingPhoneNumber() {
        Map<String, Object> request = new HashMap<>();
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Phone number is required", response.getBody());
        verify(twilioService, never()).sendSms(any(), any());
    }

    @Test
    void testSendMessage_MissingMessage() {
        Map<String, Object> request = new HashMap<>();
        request.put("number", RECIPIENT_NUMBER);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Message is required", response.getBody());
        verify(twilioService, never()).sendSms(any(), any());
    }

    @Test
    void testSendMessage_InvalidPhoneNumber() {
        Map<String, Object> request = new HashMap<>();
        request.put("number", "12135514209"); // Missing +
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Phone number must start with +", response.getBody());
        verify(twilioService, never()).sendSms(any(), any());
    }

    @Test
    void testSendMessage_ServiceError() {
        Map<String, Object> request = new HashMap<>();
        request.put("number", RECIPIENT_NUMBER);
        request.put("message", TEST_MESSAGE);

        doThrow(new RuntimeException("Service error"))
                .when(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Failed to send notification: Service error", response.getBody());
        verify(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);
    }

    @Test
    void testSendEmail_Success() {
        Map<String, Object> request = new HashMap<>();
        request.put("to", TEST_EMAIL);
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        doNothing().when(emailService).sendEmail(TEST_EMAIL, TEST_SUBJECT, TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email sent successfully!", response.getBody());
        verify(emailService).sendEmail(TEST_EMAIL, TEST_SUBJECT, TEST_MESSAGE);
    }

    @Test
    void testSendEmail_MissingEmail() {
        Map<String, Object> request = new HashMap<>();
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Recipient email is required", response.getBody());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void testSendEmail_InvalidEmailFormat() {
        Map<String, Object> request = new HashMap<>();
        request.put("to", "invalid-email");
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid email format", response.getBody());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void testSendEmail_ServiceError() {
        Map<String, Object> request = new HashMap<>();
        request.put("to", TEST_EMAIL);
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendEmail(TEST_EMAIL, TEST_SUBJECT, TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Failed to send email: Email service error", response.getBody());
        verify(emailService).sendEmail(TEST_EMAIL, TEST_SUBJECT, TEST_MESSAGE);
    }
}