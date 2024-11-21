package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.*;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.TaskService;
import com.eventease.eventease_service.service.UserService;
import com.eventease.eventease_service.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskController is a REST controller that handles task-related operations for events.
 * This controller supports creating, updating, retrieving,
 * and deleting tasks associated with a specific event.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@RequestParam Long eventId,
                                                          @RequestParam Long userId,
                                                          @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!request.containsKey("task")) {
                throw new IllegalArgumentException("Request must contain a 'task' object");
            }

            Object taskObj = request.get("task");
            if (!(taskObj instanceof Map)) {
                throw new IllegalArgumentException("Task must be a valid object");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> taskMap = (Map<String, Object>) taskObj;

            if (!taskMap.containsKey("name") || taskMap.get("name") == null ||
                    taskMap.get("name").toString().trim().isEmpty()) {
                throw new IllegalArgumentException("Task name is required");
            }

            Event event = eventService.findById(eventId);
            User assignedUser = userService.findUserById(userId);

            Task task = new Task();
            task.setName(taskMap.get("name").toString().trim());
            task.setDescription(taskMap.get("description") != null
                    ? taskMap.get("description").toString()
                    : "");

            // Handle status - default to PENDING if not provided
            String statusStr = taskMap.get("status") != null
                    ? taskMap.get("status").toString().toUpperCase()
                    : "PENDING";
            try {
                task.setStatus(Task.TaskStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid task status: " + statusStr);
            }

            task.setEvent(event);
            task.setAssignedUser(assignedUser);

            Task createdTask = taskService.createTask(eventId, userId, task);

            response.put("success", true);
            response.put("data", Collections.singletonList(createdTask));
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (EventNotExistException | UserNotExistException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getTasksByEvent(@PathVariable Long eventId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (eventId == null || eventId <= 0) {
                response.put("success", false);
                response.put("message", "Invalid event ID. Event ID must be a positive number.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Event event = eventService.findById(eventId);
            
            List<Task> tasks = taskService.getTasksByEvent(eventId);
            response.put("success", true);
            response.put("data", tasks);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", "Error fetching tasks: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable Long taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Task task = taskService.getTaskById(taskId);

            Map<String, Object> taskData = new HashMap<>();
            taskData.put("id", task.getId());
            taskData.put("name", task.getName());
            taskData.put("description", task.getDescription());
            taskData.put("status", task.getStatus());

            taskData.put("eventId", task.getEvent().getId());
            taskData.put("assignedUserId", task.getAssignedUser().getId());

            response.put("success", true);
            response.put("data", taskData);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving task: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        if (!request.containsKey("status") || request.get("status") == null) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", "Status is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Task.TaskStatus newStatus = Task.TaskStatus.valueOf(request.get("status").toString());
            if (newStatus == null) {
                response.put("success", false);
                response.put("data", null);
                response.put("message", "Status is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            taskService.updateTaskStatus(taskId, newStatus);
            response.put("success", true);
            response.put("data", Collections.singletonList("Task status updated successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", "Error updating task status: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{taskId}/user")
    public ResponseEntity<Map<String, Object>> updateTaskAssignedUser(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long newUserId = Long.parseLong(request.get("userId").toString());
            try {
                userService.findUserById(newUserId);
            } catch (UserNotExistException e) {
                response.put("success", false);
                response.put("data", null);
                response.put("message", "User not found with ID: " + newUserId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            taskService.updateTaskAssignedUser(taskId, newUserId);
            response.put("success", true);
            response.put("data", Collections.singletonList("Task assigned user updated successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", "Error updating assigned user: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            taskService.deleteTask(taskId);
            response.put("success", true);
            response.put("data", Collections.singletonList("Task deleted successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", "Error deleting task: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}