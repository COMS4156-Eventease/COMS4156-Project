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
    private static final String TEST_PHONE = "+11234567890";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EVENT_NAME = "Sample Event";
    private static final String TEST_ONECLICK_LINK = "https://eventease-439518.ue.r.appspot.com/api/events/1c/" + EVENT_ID + "/" + USER_ID;
    private static final String TEST_EMAIL_SUBJECT = "EventEase Invitation - Please RSVP";
    private static final String TEST_EMAIL_BODY = "Dear " + TEST_FIRST_NAME + " " + TEST_LAST_NAME + ",\n" +
        "\n" +
        "You have been invited to the following event: " + TEST_EVENT_NAME + ".\n" +
        "\n" +
        "Please click on the following link to accept:\n" +
        TEST_ONECLICK_LINK;
    private static final String TEST_SMS_MESSAGE = "You have been invited to the event: " + TEST_EVENT_NAME + " - click the link below to accept!";

    private User getMockUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName(TEST_FIRST_NAME);
        mockUser.setLastName(TEST_LAST_NAME);
        mockUser.setPhoneNumber(TEST_PHONE);
        mockUser.setEmail(TEST_EMAIL);
        mockUser.setRole(User.Role.ELDERLY);
        return mockUser;
    }

    private Event getMockEvent() {
        Event mockEvent = new Event();
        mockEvent.setId(EVENT_ID);
        mockEvent.setName(TEST_EVENT_NAME);
        return mockEvent;
    }

    /**
     * Tests the successful sending of an SMS message with valid inputs.
     * Verifies response status, message, and service interactions.
     */
    @Test
    void testSendMessage_Success() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doNothing().when(twilioService).sendSms(TEST_PHONE, TEST_SMS_MESSAGE);
        doNothing().when(twilioService).sendSms(TEST_PHONE, TEST_ONECLICK_LINK);

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Notification sent!", response.getBody());
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verify(twilioService).sendSms(TEST_PHONE, TEST_SMS_MESSAGE);
        verify(twilioService).sendSms(TEST_PHONE, TEST_ONECLICK_LINK);
    }

    /**
     * Tests the handling of an invalid user ID format in the SMS endpoint.
     * Verifies that a 400 status is returned and no service calls are made.
     */
    @Test
    void testSendMessage_InvalidUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "invalid");
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid User ID format", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    /**
     * Tests the SMS endpoint's response when the userId is missing from the request.
     * Verifies that appropriate error message is returned and no services are called.
     */
    @Test
    void testSendMessage_MissingUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User ID is required", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    /**
     * Tests the handling of an invalid phone number format.
     * Verifies that a 400 status is returned with appropriate error message.
     */
    @Test
    void testSendMessage_InvalidPhoneNumber() {
        User mockUser = getMockUser();
        mockUser.setPhoneNumber("invalid-phone");

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid phone number format", response.getBody());
    }

    /**
     * Tests the handling of a missing phone number in the user profile.
     * Verifies appropriate error response when phone number is null.
     */
    @Test
    void testSendMessage_MissingPhoneNumber() {
        User mockUser = getMockUser();
        mockUser.setPhoneNumber(null);

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User's phone number is not available", response.getBody());
    }

    /**
     * Tests the successful sending of an email with valid inputs.
     * Verifies response status, message, and email service interaction.
     */
    @Test
    void testSendEmail_Success() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doNothing().when(emailService).sendEmail(TEST_EMAIL, TEST_EMAIL_SUBJECT, TEST_EMAIL_BODY);

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email sent successfully!", response.getBody());
        verify(emailService).sendEmail(TEST_EMAIL, TEST_EMAIL_SUBJECT, TEST_EMAIL_BODY);
    }


    /**
     * Tests handling of non-existent event ID in SMS endpoint.
     * Verifies appropriate error response when event is not found.
     */
    @Test
    void testSendMessage_EventNotFound() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<String> response = notificationController.sendMessage(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Event does not exist: Event not found", response.getBody());
    }

    /**
     * Tests handling of non-existent user ID in SMS endpoint.
     * Verifies appropriate error response when user is not found.
     */
    @Test
    void testSendMessage_UserNotExist() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenThrow(new UserNotExistException("User not found"));

        ResponseEntity<String> response = notificationController.sendMessage(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("User does not exist: User not found", response.getBody());
    }

    /**
     * Tests handling of SMS service failures.
     * Verifies 500 status response and error message when Twilio service fails.
     */
    @Test
    void testSendMessage_InternalServerError() {
        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doThrow(new RuntimeException("Twilio service error"))
                .when(twilioService).sendSms(anyString(), anyString());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to send notification"));
    }

    /**
     * Tests handling of email service failures.
     * Verifies 500 status response and error message when email service fails.
     */
    @Test
    void testSendEmail_InternalServerError() {
        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());
        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to send email"));
    }

    /**
     * Tests handling of IllegalArgumentException before service calls in email endpoint.
     * Verifies appropriate error response for invalid arguments.
     */
    @Test
    void testSendEmail_IllegalArgumentBeforeServiceCall() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new IllegalArgumentException("Test exception"));

        ResponseEntity<String> response = notificationController.sendEmail(request);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Invalid request format"));
    }

    /**
     * Tests handling of ClassCastException in email endpoint.
     * Verifies error response when request contains invalid data types.
     */
    @Test
    void testSendEmail_ClassCastException() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", 123);
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendEmail(request);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to send email"));
    }

    /**
     * Tests validation of empty event ID in SMS endpoint.
     * Verifies appropriate error response for empty event ID.
     */
    @Test
    void testSendMessage_EmptyEventId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", "");

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid Event ID format", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    /**
     * Tests phone number formatting functionality.
     * Verifies correct formatting of phone numbers with dashes.
     */
    @Test
    void testSendMessage_PhoneNumberFormatting() {
        User mockUser = getMockUser();
        mockUser.setPhoneNumber("123-456-7890");

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(200, response.getStatusCode().value());
        verify(twilioService).sendSms(TEST_PHONE, TEST_SMS_MESSAGE);
    }

    /**
     * Tests validation of null email in user profile.
     * Verifies appropriate error response when email is null.
     */
    @Test
    void testSendEmail_NullEmail() {
        User mockUser = getMockUser();
        mockUser.setEmail(null);

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid user email", response.getBody());
        verifyNoInteractions(emailService);
    }

    /**
     * Tests validation of empty event ID in email endpoint.
     * Verifies appropriate error response for empty event ID.
     */
    @Test
    void testSendEmail_EmptyEventId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", "");

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid Event ID format", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    /**
     * Tests handling of invalid event ID format in SMS endpoint.
     * Verifies appropriate error response for malformed event ID.
     */
    @Test
    void testSendMessage_InvalidEventId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", "invalid");

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid Event ID format", response.getBody());
        verifyNoInteractions(userService, eventService, twilioService);
    }

    /**
     * Tests handling of non-existent user in email endpoint.
     * Verifies appropriate error response when user is not found.
     */
    @Test
    void testSendEmail_UserNotExist() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenThrow(new UserNotExistException("User not found"));

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User does not exist: User not found", response.getBody());
        verify(userService).findUserById(USER_ID);
        verifyNoInteractions(emailService);
    }

    /**
     * Tests handling of non-existent event in email endpoint.
     * Verifies appropriate error response when event is not found.
     */
    @Test
    void testSendEmail_EventNotExist() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Event does not exist: Event not found", response.getBody());
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verifyNoInteractions(emailService);
    }

    /**
     * Tests handling of non-string user ID in SMS endpoint.
     * Verifies error response when userId is wrong type.
     */
    @Test
    void testSendMessage_IllegalArgumentException_NonStringUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", 123L);
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Failed to send notification"));
        verifyNoInteractions(userService, eventService, twilioService);
    }

    /**
     * Tests handling of IllegalArgumentException from service layer in SMS endpoint.
     * Verifies appropriate error response when service throws exception.
     */
    @Test
    void testSendMessage_IllegalArgumentException_FromService() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenReturn(getMockUser());
        when(eventService.findById(EVENT_ID)).thenThrow(new IllegalArgumentException("Invalid event data"));

        ResponseEntity<String> response = notificationController.sendMessage(request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Invalid request format"));
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verifyNoInteractions(twilioService);
    }

    /**
     * Tests handling of IllegalArgumentException from Twilio service.
     * Verifies appropriate error response when SMS service throws exception.
     */
    @Test
    void testSendMessage_IllegalArgumentException_FromTwilioService() {
        User mockUser = getMockUser();
        Event mockEvent = getMockEvent();

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

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

    /**
     * Tests validation of missing user ID in email endpoint.
     * Verifies appropriate error response when userId is missing.
     */
    @Test
    void testSendEmail_MissingUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid User ID format", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    /**
     * Tests validation of empty user ID in email endpoint.
     * Verifies appropriate error response for whitespace-only userId.
     */
    @Test
    void testSendEmail_EmptyUserId() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "   ");
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid User ID format", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    /**
     * Tests validation of invalid ID format in email endpoint.
     * Verifies appropriate error response for malformed IDs.
     */
    @Test
    void testSendEmail_InvalidIdFormat() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "invalid");
        request.put("eventId", EVENT_ID.toString());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid User ID format", response.getBody());
        verifyNoInteractions(userService, eventService, emailService);
    }

    /**
     * Tests validation of invalid email format in email endpoint.
     * Verifies appropriate error response for malformed email address.
     */
    @Test
    void testSendEmail_InvalidEmailFormat() {
        User mockUser = getMockUser();
        mockUser.setEmail("invalid-email-format");

        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(getMockEvent());

        ResponseEntity<String> response = notificationController.sendEmail(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid user email", response.getBody());
        verify(userService).findUserById(USER_ID);
        verify(eventService).findById(EVENT_ID);
        verifyNoInteractions(emailService);
    }

    /**
     * Tests handling of email service exceptions.
     * Verifies error response when email service throws runtime exception.
     */
    @Test
    void testSendEmail_EmailServiceException() {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", USER_ID.toString());
        request.put("eventId", EVENT_ID.toString());

        User mockUser = getMockUser();
        Event mockEvent = getMockEvent();
        when(userService.findUserById(USER_ID)).thenReturn(mockUser);
        when(eventService.findById(EVENT_ID)).thenReturn(mockEvent); // Add missing mock
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