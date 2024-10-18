package com.eventease.eventease_service.unit_test.conrtoller;

import com.eventease.eventease_service.controller.TaskController;
import com.eventease.eventease_service.exception.*;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.TaskService;
import com.eventease.eventease_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerUnitTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private TaskController taskController;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

    }

    @Test
    void createTask_Success() throws Exception {
        Long eventId = 1L;
        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(1L);
        task.setAssignedUser(user);

        when(taskService.createTask(eq(eventId), any(Task.class))).thenReturn(task);

        ResponseEntity<?> response = taskController.createTask(eventId, task);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(1L, responseBody.get("taskId"));
        assertEquals(eventId, responseBody.get("eventId"));
        assertEquals(1L, responseBody.get("userId"));
    }

    @Test
    void createTask_EventNotFound() throws Exception {
        Long eventId = 1L;
        Task task = new Task();

        when(taskService.createTask(eq(eventId), any(Task.class))).thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<?> response = taskController.createTask(eventId, task);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Event not found", response.getBody());
    }

    @Test
    void getTasksByEvent_Success() throws Exception {
        Long eventId = 1L;
        List<Task> tasks = Arrays.asList(new Task(), new Task());

        when(taskService.getTasksByEvent(eventId)).thenReturn(tasks);

        ResponseEntity<?> response = taskController.getTasksByEvent(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }

    @Test
    void getTasksByEvent_EventNotFound() throws Exception {
        Long eventId = 1L;

        when(taskService.getTasksByEvent(eventId)).thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<?> response = taskController.getTasksByEvent(eventId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Event not found", response.getBody());
    }

    @Test
    void getTask_Success() throws Exception {
        Long eventId = 1L;
        Long taskId = 1L;
        Task task = new Task();

        when(taskService.getTaskByEventAndId(eventId, taskId)).thenReturn(task);

        ResponseEntity<?> response = taskController.getTask(eventId, taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void getTask_TaskNotFound() throws Exception {
        Long eventId = 1L;
        Long taskId = 1L;

        when(taskService.getTaskByEventAndId(eventId, taskId)).thenThrow(new TaskNotExistException("Task not found"));

        ResponseEntity<?> response = taskController.getTask(eventId, taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found", response.getBody());
    }

    @Test
    void updateTaskStatus_Success() throws Exception {
        Long eventId = 1L;
        Long taskId = 1L;
        Map<String, Task.TaskStatus> statusUpdate = new HashMap<>();
        statusUpdate.put("status", Task.TaskStatus.COMPLETED);

        doNothing().when(taskService).updateTaskStatus(eventId, taskId, Task.TaskStatus.COMPLETED);

        ResponseEntity<?> response = taskController.updateTaskStatus(eventId, taskId, statusUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task status updated successfully", response.getBody());
    }

    @Test
    void updateTaskStatus_MissingStatus() {
        Long eventId = 1L;
        Long taskId = 1L;
        Map<String, Task.TaskStatus> statusUpdate = new HashMap<>();

        ResponseEntity<?> response = taskController.updateTaskStatus(eventId, taskId, statusUpdate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Status is required", response.getBody());
    }

    @Test
    void updateTaskAssignedUser_Success() throws Exception {
        Long eventId = 1L;
        Long taskId = 1L;
        String newUserId = "2";

        when(userService.findUserById(1L)).thenReturn(new User());
        when(userService.findUserById(2L)).thenReturn(new User());
        doNothing().when(taskService).updateTaskAssignedUser(eventId, taskId, 1L);

        ResponseEntity<?> response = taskController.updateTaskAssignedUser(eventId, taskId, newUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task assigned user updated successfully", response.getBody());
    }

    @Test
    void updateTaskAssignedUser_UserNotFound() {
        Long eventId = 1L;
        Long taskId = 1L;
        String newUserId = "2";

        when(userService.findUserById(2L)).thenReturn(new User());
        doNothing().when(taskService).updateTaskAssignedUser(eventId, taskId, 2L);

        ResponseEntity<?> response = taskController.updateTaskAssignedUser(eventId, taskId, newUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task assigned user updated successfully", response.getBody());
    }

    @Test
    void deleteTask_Success() throws Exception {
        Long eventId = 1L;
        Long taskId = 1L;

        doNothing().when(taskService).deleteTask(eventId, taskId);

        ResponseEntity<?> response = taskController.deleteTask(eventId, taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task deleted successfully", response.getBody());
    }

    @Test
    void deleteTask_TaskNotFound() throws Exception {
        Long eventId = 1L;
        Long taskId = 1L;

        doThrow(new TaskNotExistException("Task not found")).when(taskService).deleteTask(eventId, taskId);

        ResponseEntity<?> response = taskController.deleteTask(eventId, taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found", response.getBody());
    }
}