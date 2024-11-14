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
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TwilioServiceTest {

    private TwilioConfig twilioConfig;
    private Message messageInstanceMock;
    private MessageCreator messageCreatorMock;
    private MockedStatic<Twilio> twilioMock;
    private MockedStatic<Message> messageMock;
    private TwilioService twilioService;

    private static final String VALID_TWILIO_NUMBER = "+15555555555"; // Valid US format
    private static final String VALID_RECIPIENT_NUMBER = "+14155555555"; // Valid US format
    private static final String TEST_MESSAGE = "Test message";
    private static final String TEST_ACCOUNT_SID = "TEST_ACCOUNT_SID";
    private static final String TEST_AUTH_TOKEN = "TEST_AUTH_TOKEN";

    private static final String TEST_ACCOUNT_SID = "TEST_ACCOUNT_SID";
    private static final String TEST_AUTH_TOKEN = "TEST_AUTH_TOKEN";

    private void setupMocks() {
        twilioMock = mockStatic(Twilio.class);
        messageMock = mockStatic(Message.class);

        twilioConfig = mock(TwilioConfig.class);
        when(twilioConfig.getAccountSid()).thenReturn(TEST_ACCOUNT_SID);
        when(twilioConfig.getAuthToken()).thenReturn(TEST_AUTH_TOKEN);
        when(twilioConfig.getPhoneNumber()).thenReturn(VALID_TWILIO_NUMBER);

        messageInstanceMock = mock(Message.class);
        messageCreatorMock = mock(MessageCreator.class);

        when(messageInstanceMock.getSid()).thenReturn("SMaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        when(messageCreatorMock.create()).thenReturn(messageInstanceMock);

        messageMock.when(() -> Message.creator(
                any(PhoneNumber.class),
                any(PhoneNumber.class),
                anyString())
        ).thenReturn(messageCreatorMock);
    }

    @Test
    void testSendSms() {
        twilioService.sendSms(VALID_RECIPIENT_NUMBER, TEST_MESSAGE);

        twilioMock.verify(() -> Twilio.init(
                TEST_ACCOUNT_SID,
                TEST_AUTH_TOKEN
        ));

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