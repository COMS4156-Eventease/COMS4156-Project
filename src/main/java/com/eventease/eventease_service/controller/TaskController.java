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

/**
 * TaskController is a REST controller that handles task-related operations for events.
 * This controller supports creating, updating, retrieving,
 * and deleting tasks associated with a specific event.
 */
@RestController
@RequestMapping("/api/events/{eventId}/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    /**
     * Creates a new task for a specific event with the given task parameters.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param task the task to be created
     * @return a response entity containing the created task information or an error message
     */
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

    /**
     * Retrieves a list of tasks for a specific event given the event ID.
     *
     * @param eventId the ID of the event
     * @return a response entity containing the list of tasks or an error message
     */
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

    /**
     * Retrieves a specific task by its ID for a given event.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to be retrieved
     * @return a response entity containing the task details or an error message
     */
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

    /**
     * Updates the status of a specific task for a given event.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to be updated
     * @param statusUpdate a map containing the new status of the task
     * @return a response entity indicating success or failure
     */
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

    /**
     * Updates the assigned user of a specific task for a given event.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to be updated
     * @param userId the ID of the new user to be assigned
     * @return a response entity indicating success or failure
     */
    @PatchMapping("/{taskId}/user")
    public ResponseEntity<?> updateTaskAssignedUser(@PathVariable Long eventId,
                                                    @PathVariable Long taskId,
                                                    @RequestBody String userId) {
        try {
            Long newUserId = Long.parseLong(userId);
            try {
                userService.findUserById(newUserId);
            } catch (UserNotExistException e) {
                return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
            }
            taskService.updateTaskAssignedUser(eventId, taskId, newUserId);
            return new ResponseEntity<>("Task assigned user updated successfully", HttpStatus.OK);
        } catch (TaskNotExistException | EventNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a specific task by its task ID for a given event.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to be deleted
     * @return a response entity indicating success or failure
     */
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
