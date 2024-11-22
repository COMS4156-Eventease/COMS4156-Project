package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.NotificationController;
import com.eventease.eventease_service.service.TwilioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerUnitTest {

    @Mock
    private TwilioService twilioService;

    @InjectMocks
    private NotificationController notificationController;

    private static final String RECIPIENT_NUMBER = "+12135514209";
    private static final String TEST_MESSAGE = "Hello, this is a test notification from EventEase.";

    @Test
    void testSendNotification_Success() {
        doNothing().when(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendNotification();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Notification sent!", response.getBody());
        verify(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);
    }

    @Test
    void testSendNotification_InvalidPhoneNumber() {
        doThrow(new IllegalArgumentException("Invalid phone number"))
                .when(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendNotification();

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid phone number format", response.getBody());
        verify(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);
    }

    @Test
    void testSendNotification_ServiceError() {
        doThrow(new RuntimeException("Service error"))
                .when(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendNotification();

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Failed to send notification: Service error", response.getBody());
        verify(twilioService).sendSms(RECIPIENT_NUMBER, TEST_MESSAGE);
    }
}