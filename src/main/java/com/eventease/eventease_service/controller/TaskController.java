package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events/{eventId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

}