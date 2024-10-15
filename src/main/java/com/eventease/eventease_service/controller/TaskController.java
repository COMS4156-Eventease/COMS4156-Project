package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

}