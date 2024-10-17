package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.*;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.TaskService;
import com.eventease.eventease_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, EventService eventService) {
        this.taskService = taskService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@PathVariable Long eventId,
                                        @Valid @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(eventId, task);
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", createdTask.getId());
            response.put("eventId", eventId);
            response.put("userId", createdTask.getAssignedUser().getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (TaskNotExistException | EventNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getTasksByEvent(@PathVariable Long eventId) {
        try {
            List<Task> tasks = taskService.getTasksByEvent(eventId);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching tasks: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable Long eventId, @PathVariable Long taskId) {
        try {
            Task task = taskService.getTaskByEventAndId(eventId, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long eventId,
                                              @PathVariable Long taskId,
                                              @RequestBody Map<String, Task.TaskStatus> statusUpdate) {
        try {
            Task.TaskStatus newStatus = statusUpdate.get("status");
            if (newStatus == null) {
                return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
            }
            taskService.updateTaskStatus(eventId, taskId, newStatus);
            return new ResponseEntity<>("Task status updated successfully", HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating task status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{taskId}/user")
    public ResponseEntity<?> updateTaskAssignedUser(@PathVariable Long eventId,
                                                    @PathVariable Long taskId,
                                                    @RequestBody List<String> userIds) {
        try {
            // First, validate all user IDs
            for (String userId : userIds) {
                try {
                    Long id = Long.parseLong(userId);
                    userService.findUserById(id);
                } catch (NumberFormatException e) {
                    throw new UserNotExistException("Invalid user ID format: " + userId);
                } catch (UserNotExistException e) {
                    return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
                }
            }
            Long newUserId = Long.parseLong(userIds.get(0));
            taskService.updateTaskAssignedUser(eventId, taskId, newUserId);
            return new ResponseEntity<>("Task assigned user updated successfully", HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating task assigned user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long eventId, @PathVariable Long taskId) {
        try {
            taskService.deleteTask(eventId, taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}