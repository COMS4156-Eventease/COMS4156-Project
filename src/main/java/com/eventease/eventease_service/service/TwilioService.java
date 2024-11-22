package com.eventease.eventease_service.service;

import com.eventease.eventease_service.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private final TwilioConfig twilioConfig;

    @Autowired
    public TwilioService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        initializeTwilio();
    }

    private void initializeTwilio() {
        if (twilioConfig.getAccountSid() != null && twilioConfig.getAuthToken() != null) {
            Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
        }
    }

    public void sendSms(String to, String messageBody) {
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                messageBody)
                .create();

        System.out.println("SMS sent with SID: " + message.getSid());
    }
}