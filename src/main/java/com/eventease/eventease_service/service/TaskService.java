package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.TaskNotExistException;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    public Task createTask(Long eventId, Task task) {
        Event event = eventService.findById(eventId);
        User user = userService.findUserById(task.getAssignedUser().getId());
        task.setEvent(event);
        task.setAssignedUser(user);
        return taskRepository.save(task);
    }

    public List<Task> getTasksByEvent(Long eventId) {
        return taskRepository.findByEventId(eventId);
    }

    public Task getTaskByEventAndId(Long eventId, Long taskId) {
        return taskRepository.findByIdAndEventId(taskId, eventId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId + " for event ID: " + eventId));
    }

    public void updateTaskStatus(Long eventId, Long taskId, Task.TaskStatus status) {
        int updatedRows = taskRepository.updateTaskStatus(taskId, eventId, status);
        if (updatedRows == 0) {
            throw new TaskNotExistException("Task not found with ID: " + taskId + " for event ID: " + eventId);
        }
    }

    public void updateTaskAssignedUser(Long eventId, Long taskId, Long userId) {
        userService.findUserById(userId); // Verify user exists
        int updatedRows = taskRepository.updateTaskAssignedUser(taskId, eventId, userId);
        if (updatedRows == 0) {
            throw new TaskNotExistException("Task not found with ID: " + taskId + " for event ID: " + eventId);
        }
    }

    public void deleteTask(Long eventId, Long taskId) {
        taskRepository.deleteByIdAndEventId(taskId, eventId);
    }

    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId));
    }
}