package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.config.EmailConfig;
import com.eventease.eventease_service.service.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class EmailServiceUnitTest {

    private EmailConfig emailConfigMock;
    private JavaMailSender mailSenderMock;
    private EmailService emailService;

    private static final String FROM_EMAIL = "test@example.com";
    private static final String TO_EMAIL = "recipient@example.com";
    private static final String SUBJECT = "Test Subject";
    private static final String TEXT = "Test Message";

    @BeforeEach
    void setupMocks() throws NoSuchFieldException, IllegalAccessException {
        emailConfigMock = mock(EmailConfig.class);
        mailSenderMock = mock(JavaMailSender.class);

        when(emailConfigMock.getJavaMailSender()).thenReturn(mailSenderMock);
        when(emailConfigMock.verifyEmailConfig()).thenReturn(true);

        emailService = new EmailService(emailConfigMock);

        java.lang.reflect.Field fromEmailField = EmailService.class.getDeclaredField("fromEmail");
        fromEmailField.setAccessible(true);
        fromEmailField.set(emailService, FROM_EMAIL);
    }

    @Test
    void testSendEmail_Success() {
        emailService.sendEmail(TO_EMAIL, SUBJECT, TEXT);

        verify(mailSenderMock, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_ReinitializeOnFailure() {
        doThrow(new RuntimeException("Simulated failure"))
                .doNothing()
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        emailService.sendEmail(TO_EMAIL, SUBJECT, TEXT);

        verify(mailSenderMock, times(2)).send(any(SimpleMailMessage.class));

        verify(emailConfigMock, times(2)).getJavaMailSender();
    }


    @AfterEach
    void tearDown() {}
}
