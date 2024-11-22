package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.NotificationController;
import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.EmailService;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.TwilioService;
import com.eventease.eventease_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(NotificationController.class)
@ActiveProfiles("test")
class NotificationControllerUnitTest {

    @MockBean
    private TwilioService twilioService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    @Autowired
    private NotificationController notificationController;

    private static final Long USER_ID = 1L;
    private static final Long EVENT_ID = 1L;
    private static final String TEST_PHONE = "+1234567890";
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_MESSAGE = "Hello, this is a test message!";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EVENT_NAME = "Sample Event";

    private User getMockUser() {
        User mockUser = new User();
        mockUser.setId(1L); // Manually set the userId
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setPhoneNumber("+1234567890");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setRole(User.Role.ELDERLY);
        return mockUser;
    }

    private Event getMockEvent() {
        Event mockEvent = new Event();
        mockEvent.setId(EVENT_ID);
        mockEvent.setName(TEST_EVENT_NAME);
        return mockEvent;
    }

    @Test
    void testSendMessage_Success() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doNothing().when(twilioService).sendSms(TEST_PHONE, "Dear John Doe,\nHello, this is a test message!");

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Notification sent!", response.getBody());
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verify(twilioService).sendSms(TEST_PHONE, "Dear John Doe,\nHello, this is a test message!");
    }

    @Test
    void testSendMessage_MissingUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User ID is required", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_InvalidUserIdFormat() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "invalid");
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid User ID format", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_MissingEventId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid Event ID format", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_UserNotExist() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenThrow(new UserNotExistException("User not found"));

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User does not exist: User not found", response.getBody());
        verify(userService).findUserById(USER_ID);
        verifyNoInteractions(eventService, twilioService);
    }

    @Test
    void testSendEmail_Success() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "1");
        request.put("eventId", "1");
        request.put("subject", "Test Subject");
        request.put("message", "Hello, this is a test message!");

        // Mock user and event
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setPhoneNumber("+1234567890");
        mockUser.setEmail("john.doe@example.com");

        Event mockEvent = new Event();
        mockEvent.setId(1L);
        mockEvent.setName("Sample Event");

        when(userService.findUserById(1L)).thenReturn(mockUser);
        when(eventService.findById(1L)).thenReturn(mockEvent);

        doNothing().when(emailService).sendEmail(
                "john.doe@example.com", // Match the mock user's email
                "Test Subject",
                "Dear John Doe,\n\nHello, this is a test message!"
        );

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email sent successfully!", response.getBody());

        verify(userService).findUserById(1L);
        verify(eventService).findById(1L);
        verify(emailService).sendEmail(
                "john.doe@example.com", // Match the actual invocation
                "Test Subject",
                "Dear John Doe,\n\nHello, this is a test message!"
        );
    }


    @Test
    void testSendEmail_EventNotExist() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Event does not exist: Event not found", response.getBody());
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verifyNoInteractions(emailService);
    }
}
