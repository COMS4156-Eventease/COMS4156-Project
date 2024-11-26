package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.config.TwilioConfig;
import com.eventease.eventease_service.service.TwilioService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TwilioServiceTest {

    private TwilioConfig twilioConfig;
    private Message messageInstanceMock;
    private MessageCreator messageCreatorMock;
    private MockedStatic<Twilio> twilioMock;
    private MockedStatic<Message> messageMock;
    private TwilioService twilioService;

    private static final String VALID_TWILIO_NUMBER = "+15555555555";
    private static final String VALID_RECIPIENT_NUMBER = "+14155555555";
    private static final String TEST_MESSAGE = "Test message";
    private static final String TEST_ACCOUNT_SID = "TEST_ACCOUNT_SID";
    private static final String TEST_AUTH_TOKEN = "TEST_AUTH_TOKEN";

    @BeforeEach
    void setupMocks() {
        twilioMock = mockStatic(Twilio.class);
        messageMock = mockStatic(Message.class);

        twilioConfig = mock(TwilioConfig.class);
        when(twilioConfig.getAccountSid()).thenReturn(TEST_ACCOUNT_SID);
        when(twilioConfig.getAuthToken()).thenReturn(TEST_AUTH_TOKEN);
        when(twilioConfig.getPhoneNumber()).thenReturn(VALID_TWILIO_NUMBER);

        twilioService = new TwilioService(twilioConfig);

        messageInstanceMock = mock(Message.class);
        messageCreatorMock = mock(MessageCreator.class);

        when(messageInstanceMock.getSid()).thenReturn("TESTINGTESTINGTESTING...");
        when(messageCreatorMock.create()).thenReturn(messageInstanceMock);

        messageMock.when(() -> Message.creator(
                any(PhoneNumber.class),
                any(PhoneNumber.class),
                anyString())).thenReturn(messageCreatorMock);
    }

    @Test
    void testSendSms() {
        twilioService.sendSms(VALID_RECIPIENT_NUMBER, TEST_MESSAGE);

        twilioMock.verify(() -> Twilio.init(
                TEST_ACCOUNT_SID,
                TEST_AUTH_TOKEN));

        verify(messageCreatorMock).create();
    }

    @Test
    void testSendSms_NullRecipientNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            twilioService.sendSms(null, TEST_MESSAGE);
        });

        assertEquals("The 'to' phone number cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(messageCreatorMock);
    }

    @Test
    void testSendSms_EmptyRecipientNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            twilioService.sendSms("", TEST_MESSAGE);
        });

        assertEquals("The 'to' phone number cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(messageCreatorMock);
    }

    @Test
    void testSendSms_NullMessageBody() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            twilioService.sendSms(VALID_RECIPIENT_NUMBER, null);
        });

        assertEquals("The message body cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(messageCreatorMock);
    }

    @Test
    void testSendSms_EmptyMessageBody() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            twilioService.sendSms(VALID_RECIPIENT_NUMBER, "");
        });

        assertEquals("The message body cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(messageCreatorMock);
    }

    @Test
    void testSendSms_InvalidRecipientNumber() {
        doThrow(new IllegalArgumentException("Invalid phone number"))
                .when(messageCreatorMock).create();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            twilioService.sendSms("invalid-number", TEST_MESSAGE);
        });

        assertEquals("Invalid phone number", exception.getMessage());
        verify(messageCreatorMock).create();
    }

    @Test
    void testTwilioInitialization() {
        twilioMock.verify(() -> Twilio.init(TEST_ACCOUNT_SID, TEST_AUTH_TOKEN), times(1));
    }

    @Test
    void testMessageCreatorWithValidInputs() {
        twilioService.sendSms(VALID_RECIPIENT_NUMBER, TEST_MESSAGE);

        messageMock.verify(() -> Message.creator(
                new PhoneNumber(VALID_RECIPIENT_NUMBER),
                new PhoneNumber(VALID_TWILIO_NUMBER),
                TEST_MESSAGE));
        verify(messageCreatorMock).create();
    }

    @Test
    void testSendSms_ExceptionDuringMessageCreation() {
        doThrow(new RuntimeException("Message creation failed"))
                .when(messageCreatorMock).create();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            twilioService.sendSms(VALID_RECIPIENT_NUMBER, TEST_MESSAGE);
        });

        assertEquals("Message creation failed", exception.getMessage());
        verify(messageCreatorMock).create();
    }


    @AfterEach
    void tearDown() {
        if (twilioMock != null) {
            twilioMock.close();
        }
        if (messageMock != null) {
            messageMock.close();
        }
    }

}
