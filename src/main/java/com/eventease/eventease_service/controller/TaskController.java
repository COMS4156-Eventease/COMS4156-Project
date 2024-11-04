package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.*;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.service.TaskService;
import com.eventease.eventease_service.service.UserService;
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

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@RequestParam Long eventId,
                                                          @RequestParam Long userId,
                                                          @Valid @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Task task = (Task) request.get("task");
            Task createdTask = taskService.createTask(eventId, userId, task);
            response.put("success", true);
            response.put("data", Collections.singletonList(createdTask));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (TaskNotExistException | EventNotExistException e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", "Error creating task: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getTasksByEvent(@PathVariable Long eventId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskService.getTasksByEvent(eventId);
            response.put("success", true);
            response.put("data", tasks);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", "Error fetching tasks: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable Long taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Task task = taskService.getTaskById(taskId);  // Modify service method to get by taskId only
            response.put("success", true);
            response.put("data", Collections.singletonList(task));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", "Error fetching task: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Task.TaskStatus newStatus = Task.TaskStatus.valueOf(request.get("status").toString());
            if (newStatus == null) {
                response.put("success", false);
                response.put("data", Collections.emptyList());
                response.put("message", "Status is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            taskService.updateTaskStatus(taskId, newStatus);  // Modify service method
            response.put("success", true);
            response.put("data", Collections.singletonList("Task status updated successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
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
                response.put("data", Collections.emptyList());
                response.put("message", "User not found with ID: " + newUserId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            taskService.updateTaskAssignedUser(taskId, newUserId);  // Modify service method
            response.put("success", true);
            response.put("data", Collections.singletonList("Task assigned user updated successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", "Error updating assigned user: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            taskService.deleteTask(taskId);  // Modify service method
            response.put("success", true);
            response.put("data", Collections.singletonList("Task deleted successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TaskNotExistException e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", Collections.emptyList());
            response.put("message", "Error deleting task: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}