package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.TaskNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events/{eventId}/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@PathVariable String eventId,
                                        @RequestParam String userId,
                                        @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(eventId, userId, task);
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", createdTask.getId());
            response.put("eventId", eventId);
            response.put("userId", userId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotExistException | TaskNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve all tasks associated with a specific event.
     */
    @GetMapping
    public ResponseEntity<?> getTasksByEvent(@PathVariable String eventId) {
        try {
            List<Task> tasks = taskService.getTasksByEvent(eventId);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while fetching tasks", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update a task's status or assigned user.
     */
    @PatchMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable long taskId,
                                        @RequestBody Task updatedTask) {
        try {
            taskService.updateTask(taskId, updatedTask);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        } catch (TaskNotExistException | UserNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating task", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a specific task.
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable long taskId) {
        try {
            taskService.deleteTask(taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (TaskNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting task", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
