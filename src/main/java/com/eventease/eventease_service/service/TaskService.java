package com.eventease.eventease_service.service;

import com.eventease.eventease_service.repository.TaskRepository;
import com.eventease.eventease_service.repository.EventRepository;
import com.eventease.eventease_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

}
