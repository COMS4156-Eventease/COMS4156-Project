package com.eventease.eventease_service.unit_test.controller;

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
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TaskController class.
 * The class uses JUnit and Mockito to test different scenarios in TaskController's methods.
 */
@ActiveProfiles("test")
class TaskControllerUnitTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private TaskController taskController;

    /**
     * Initializes the mocks and injects them into the TaskController before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the successful creation of a task given valid parameters.
     *
     * @throws Exception if an error occurs during task creation
     */
    @Test
    void createTask_Success() throws Exception {
        Long eventId = 1L;
        Long userId = 1L;
        Task task = new Task();
        Map<String, Object> request = new HashMap<>();
        request.put("task", task);

        when(taskService.createTask(eq(eventId), eq(userId), any(Task.class))).thenReturn(task);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(Collections.singletonList(task), responseBody.get("data"));
    }

    /**
     * Tests the case where the event for which the task is created does not exist.
     *
     * @throws Exception if an error occurs during task creation
     */
    @Test
    void createTask_EventNotFound() throws Exception {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("task", new Task());

        when(taskService.createTask(eq(eventId), eq(userId), any(Task.class)))
                .thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals(null, responseBody.get("data"));
        assertEquals("Event not found", responseBody.get("message"));
    }

    /**
     * Tests the successful retrieval of all tasks for a specific event.
     *
     * @throws Exception if an error occurs during task retrieval
     */
    @Test
    void getTasksByEvent_Success() throws Exception {
        Long eventId = 1L;
        List<Task> tasks = Arrays.asList(new Task(), new Task());

        when(taskService.getTasksByEvent(eventId)).thenReturn(tasks);

        ResponseEntity<Map<String, Object>> response = taskController.getTasksByEvent(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(tasks, responseBody.get("data"));
    }

    /**
     * Tests the case where the event for which tasks are being retrieved does not exist.
     *
     * @throws Exception if an error occurs during task retrieval
     */
    @Test
    void getTasksByEvent_EventNotFound() throws Exception {
        Long eventId = 1L;

        when(taskService.getTasksByEvent(eventId))
                .thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<Map<String, Object>> response = taskController.getTasksByEvent(eventId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals(null, responseBody.get("data"));
        assertEquals("Event not found", responseBody.get("message"));
    }

    /**
     * Tests the successful retrieval of a specific task for a given event ID.
     *
     * @throws Exception if an error occurs during task retrieval
     */
    @Test
    void getTask_Success() throws Exception {
        Long taskId = 1L;
        Task task = new Task();

        when(taskService.getTaskById(taskId)).thenReturn(task);

        ResponseEntity<Map<String, Object>> response = taskController.getTask(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(task, responseBody.get("data"));
    }
    /**
     * Tests the case where the task ID being retrieved does not exist.
     *
     * @throws Exception if an error occurs during task retrieval
     */
    @Test
    void getTask_TaskNotFound() throws Exception {
        Long taskId = 1L;

        when(taskService.getTaskById(taskId))
                .thenThrow(new TaskNotExistException("Task not found"));

        ResponseEntity<Map<String, Object>> response = taskController.getTask(taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals(null, responseBody.get("data"));
        assertEquals("Task not found", responseBody.get("message"));
    }

    /**
     * Tests the successful updating of a task's status given valid Event and Task ID.
     *
     * @throws Exception if an error occurs during task status update
     */
    @Test
    void updateTaskStatus_Success() throws Exception {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("status", "COMPLETED");

        doNothing().when(taskService).updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(Collections.singletonList("Task status updated successfully"), responseBody.get("data"));
    }

    /**
     * Tests the case where the status is missing from the update request.
     */
    @Test
    void updateTaskStatus_MissingStatus() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals(null, responseBody.get("data"));
        assertEquals("Status is required", responseBody.get("message"));
    }

    /**
     * Tests the successful updating of the assigned user of a task given valid Event and Task ID.
     *
     * @throws Exception if an error occurs during the user assignment update
     */
    @Test
    void updateTaskAssignedUser_Success() throws Exception {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("userId", 2L);

        when(userService.findUserById(2L)).thenReturn(new User());
        doNothing().when(taskService).updateTaskAssignedUser(taskId, 2L);

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskAssignedUser(taskId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(Collections.singletonList("Task assigned user updated successfully"), responseBody.get("data"));
    }

    /**
     * Tests the case where the user ID to be assigned a specific task does not exist,
     * given a valid Event and Task ID.
     */
    @Test
    void updateTaskAssignedUser_UserNotFound() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("userId", 100L);

        doThrow(new UserNotExistException("User not found"))
                .when(userService).findUserById(100L);

        ResponseEntity<Map<String, Object>> response =
                taskController.updateTaskAssignedUser(taskId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals(null, responseBody.get("data"));
        assertEquals("User not found with ID: 100", responseBody.get("message"));
    }

    /**
     * Tests the successful deletion of a task.
     *
     * @throws Exception if an error occurs during task deletion
     */
    @Test
    void deleteTask_Success() throws Exception {
        Long taskId = 1L;

        doNothing().when(taskService).deleteTask(taskId);

        ResponseEntity<Map<String, Object>> response = taskController.deleteTask(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(Collections.singletonList("Task deleted successfully"), responseBody.get("data"));
    }

    /**
     * Tests the case where the task to be deleted does not exist given
     * a valid event ID but invalid task ID.
     *
     * @throws Exception if an error occurs during task deletion
     */
    @Test
    void deleteTask_TaskNotFound() throws Exception {
        Long taskId = 1L;

        doThrow(new TaskNotExistException("Task not found"))
                .when(taskService).deleteTask(taskId);

        ResponseEntity<Map<String, Object>> response = taskController.deleteTask(taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals(null, responseBody.get("data"));
        assertEquals("Task not found", responseBody.get("message"));
    }
}
