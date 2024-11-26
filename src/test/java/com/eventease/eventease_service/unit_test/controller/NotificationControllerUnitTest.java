package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.NotificationController;
import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.LinkedList;
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
        mockUser.setId(1L);
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
    void testSendMessage_InvalidUserId() {
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
    void testSendMessage_InvalidPhoneNumber() {
        User mockUser = getMockUser();
        mockUser.setPhoneNumber("invalid-phone");

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid phone number format", response.getBody());
    }

    @Test
    void testSendMessage_MissingPhoneNumber() {
        User mockUser = getMockUser();
        mockUser.setPhoneNumber(null);

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User's phone number is not available", response.getBody());
    }

    @Test
    void testSendEmail_Success() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doNothing().when(emailService).sendEmail(
                "john.doe@example.com",
                TEST_SUBJECT,
                "Dear John Doe,\n\n" + TEST_MESSAGE
        );

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email sent successfully!", response.getBody());
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendEmail_MissingRequiredFields() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Subject is required", response.getBody());

        request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);

        response = notificationController.sendEmail(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Message is required", response.getBody());
    }

    @Test
    void testSendMessage_EventNotFound() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<String> response = notificationController.sendMessage(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Event does not exist: Event not found", response.getBody());
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
    }

    @Test
    void testSendMessage_InternalServerError() {
        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doThrow(new RuntimeException("Twilio service error"))
                .when(twilioService).sendSms(anyString(), anyString());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to send notification"));
    }

    @Test
    void testSendEmail_InternalServerError() {
        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to send email"));
    }


    @Test
    void testSendEmail_IllegalArgumentBeforeServiceCall() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new IllegalArgumentException("Test exception"));

        ResponseEntity<String> response = notificationController.sendEmail(request);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Invalid request format"));  // Changed to match actual error message
    }

    @Test
    void testSendEmail_ClassCastException() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", 123); // Integer instead of String
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to send email"));
    }

    @Test
    void testSendMessage_EmptyMessage() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", "");  // Empty message

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Message is required", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_NullMessage() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", null);  // Null message

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Message is required", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_EmptyEventId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", "");  // Empty event ID
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid Event ID format", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_PhoneNumberFormatting() {
        User mockUser = getMockUser();
        mockUser.setPhoneNumber("123-456-7890");  // Test phone number with dashes

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(200, response.getStatusCode().value());
        verify(twilioService).sendSms("+11234567890", "Dear John Doe,\n" + TEST_MESSAGE);
    }

    @Test
    void testSendEmail_NullEmail() {
        User mockUser = getMockUser();
        mockUser.setEmail(null);

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid user email", response.getBody());
        verifyNoInteractions(emailService);
    }

    @Test
    void testSendEmail_EmptyEventId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", "");
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Event ID is required", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    @Test
    void testSendMessage_InvalidEventId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", "invalid");
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid Event ID format", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendEmail_UserNotExist() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        // Simulate user not found scenario
        when(userService.findUserById(USER_ID)).thenThrow(new UserNotExistException("User not found"));

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User does not exist: User not found", response.getBody());
        verify(userService).findUserById(USER_ID);
        verifyNoInteractions(emailService);
    }

    @Test
    void testSendEmail_EventNotExist() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        // Mock successful user retrieval but event not found
        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Event does not exist: Event not found", response.getBody());
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verifyNoInteractions(emailService);
    }


    @Test
    void testSendMessage_IllegalArgumentException_NonStringUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", 123L);  // Putting a Long instead of String
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Failed to send notification"));
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_IllegalArgumentException_NonStringMessage() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", new HashMap<>());  // Putting a Map instead of String

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Failed to send notification"));
        verifyNoInteractions(userService, eventService, twilioService);
    }

    @Test
    void testSendMessage_IllegalArgumentException_FromService() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new IllegalArgumentException("Invalid event data"));

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Invalid request format"));
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verifyNoInteractions(twilioService);
    }

    @Test
    void testSendMessage_IllegalArgumentException_FromTwilioService() {
        User mockUser = getMockUser();
        Event mockEvent = getMockEvent();

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(mockEvent);
        doThrow(new IllegalArgumentException("Invalid phone number"))
                .when(twilioService).sendSms(anyString(), anyString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Invalid request format"));
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verify(twilioService).sendSms(anyString(), anyString());
    }

    @Test
    void testSendEmail_MissingUserId() {
        Map<String, Object> request = new HashMap<>();
        // userId is missing
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User ID is required", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    @Test
    void testSendEmail_EmptyUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "   ");  // Empty with spaces
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User ID is required", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }


    @Test
    void testSendEmail_MissingSubject() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        // subject is missing
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Subject is required", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    @Test
    void testSendEmail_EmptySubject() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", "   ");  // Empty subject with spaces
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Subject is required", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    @Test
    void testSendEmail_MissingMessage() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        // message is missing

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Message is required", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    @Test
    void testSendEmail_EmptyMessage() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", "  ");  // Empty message with spaces

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Message is required", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    @Test
    void testSendEmail_InvalidIdFormat() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "invalid");
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid ID format", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    @Test
    void testSendEmail_InvalidEmailFormat() {
        User mockUser = getMockUser();
        mockUser.setEmail("invalid-email-format");

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid user email", response.getBody());
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verifyNoInteractions(emailService);
    }

    @Test
    void testSendEmail_EmailServiceException() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());
        request.put("subject", TEST_SUBJECT);
        request.put("message", TEST_MESSAGE);

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to send email"));
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

}