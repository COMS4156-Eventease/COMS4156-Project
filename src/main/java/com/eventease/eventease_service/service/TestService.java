package com.eventease.eventease_service.service;

import com.eventease.eventease_service.model.TestModel;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public TestModel getTestMessage() {
        return new TestModel("Hello, this is a test message!");
    }
}
