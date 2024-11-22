package com.eventease.eventease_service.config;

import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
@Getter
public class TwilioConfig {
    private static final Logger logger = LoggerFactory.getLogger(TwilioConfig.class);

    @Value("${twilio.account-sid}") // Changed to match application.properties
    private String accountSid;

    @Value("${twilio.auth-token}") // Changed to match application.properties
    private String authToken;

    @Value("${twilio.phone-number}") // Changed to match application.properties
    private String phoneNumber;

    @PostConstruct
    public void init() {
        logger.info("Loading Twilio configuration...");
        if (accountSid == null || accountSid.startsWith("${")) {
            logger.error("twilio.account-sid not loaded from environment");
            throw new IllegalStateException("twilio.account-sid not properly configured");
        }
        if (authToken == null || authToken.startsWith("${")) {
            logger.error("twilio.auth-token not loaded from environment");
            throw new IllegalStateException("twilio.auth-token not properly configured");
        }
        if (phoneNumber == null || phoneNumber.startsWith("${")) {
            logger.error("twilio.phone-number not loaded from environment");
            throw new IllegalStateException("twilio.phone-number not properly configured");
        }
        logger.info("Twilio configuration loaded successfully. Account SID prefix: {}",
                accountSid.substring(0, Math.min(4, accountSid.length())));
    }
}