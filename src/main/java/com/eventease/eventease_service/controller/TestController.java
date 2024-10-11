package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.model.TestModel;
import com.eventease.eventease_service.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public TestModel getTest() {
        return testService.getTestMessage();
    }

}