package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.TaskController;
import com.eventease.eventease_service.exception.*;
import com.eventease.eventease_service.model.Event;
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

        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("name", "Test Task");
        taskDetails.put("description", "Test Description");
        taskDetails.put("status", "PENDING");

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        Task responseTask = new Task();
        responseTask.setId(1L);
        responseTask.setName("Test Task");
        responseTask.setDescription("Test Description");
        responseTask.setStatus(Task.TaskStatus.PENDING);

        Event mockEvent = new Event();
        User mockUser = new User();
        when(eventService.findById(eventId)).thenReturn(mockEvent);
        when(userService.findUserById(userId)).thenReturn(mockUser);
        when(taskService.createTask(eq(eventId), eq(userId), any(Task.class)))
                .thenReturn(responseTask);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(Collections.singletonList(responseTask), responseBody.get("data"));
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

        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("name", "Test Task");
        taskDetails.put("description", "Test Description");
        taskDetails.put("status", "PENDING");

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        when(eventService.findById(eventId))
            .thenThrow(new EventNotExistException("Event not found"));

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
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

        Event mockEvent = new Event();
        mockEvent.setId(1L);

        User mockUser = new User();
        mockUser.setId(1L);

        Task mockTask = new Task();
        mockTask.setId(taskId);
        mockTask.setName("Test Task");
        mockTask.setDescription("Test Description");
        mockTask.setStatus(Task.TaskStatus.PENDING);
        mockTask.setEvent(mockEvent);
        mockTask.setAssignedUser(mockUser);

        when(taskService.getTaskById(taskId)).thenReturn(mockTask);

        ResponseEntity<Map<String, Object>> response = taskController.getTask(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));

        Map<String, Object> taskData = (Map<String, Object>) responseBody.get("data");
        assertEquals(taskId, taskData.get("id"));
        assertEquals("Test Task", taskData.get("name"));
        assertEquals("Test Description", taskData.get("description"));
        assertEquals(Task.TaskStatus.PENDING, taskData.get("status"));
        assertEquals(1L, taskData.get("eventId"));
        assertEquals(1L, taskData.get("assignedUserId"));
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
     * Tests get tasks by event with null event ID.
     */
    @Test
    void getTasksByEvent_NullEventId() {
        Long eventId = null;

        ResponseEntity<Map<String, Object>> response = taskController.getTasksByEvent(eventId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Invalid event ID. Event ID must be a positive number.", responseBody.get("message"));
    }

    /**
     * Tests get tasks by event with invalid (negative) event ID.
     */
    @Test
    void getTasksByEvent_NegativeEventId() {
        Long eventId = -1L;

        ResponseEntity<Map<String, Object>> response = taskController.getTasksByEvent(eventId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Invalid event ID. Event ID must be a positive number.", responseBody.get("message"));
    }

    /**
     * Tests update task status with null status value.
     */
    @Test
    void updateTaskStatus_NullStatusValue() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("status", null);

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertEquals("Status is required", responseBody.get("message"));
    }

    /**
     * Tests update task status with invalid status enum value.
     */
    @Test
    void updateTaskStatus_InvalidStatusValue() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("status", "INVALID_STATUS");

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertTrue(responseBody.get("message").toString().contains("Error updating task status"));
    }

    /**
     * Tests create task with missing task object in request.
     */
    @Test
    void createTask_MissingTaskObject() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> request = new HashMap<>();

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Request must contain a 'task' object", responseBody.get("message"));
    }

    /**
     * Tests create task with invalid task object type.
     */
    @Test
    void createTask_InvalidTaskObjectType() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("task", "not a map");

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Task must be a valid object", responseBody.get("message"));
    }

    /**
     * Tests create task with missing name in task object.
     */
    @Test
    void createTask_MissingTaskName() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("description", "Test Description");

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Task name is required", responseBody.get("message"));
    }

    /**
     * Tests getting tasks for a user with invalid user ID.
     */
    @Test
    void getTasksForUser_InvalidUserId() {
        Long userId = -1L;

        ResponseEntity<Map<String, Object>> response = taskController.getTasksForUser(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Invalid user ID. User ID must be a positive number.", responseBody.get("message"));
    }

    /**
     * Tests successful retrieval of tasks for a user.
     */
    @Test
    void getTasksForUser_Success() {
        Long userId = 1L;
        List<Task> tasks = Arrays.asList(new Task(), new Task());

        when(taskService.getTasksByUser(userId)).thenReturn(tasks);

        ResponseEntity<Map<String, Object>> response = taskController.getTasksForUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(tasks, responseBody.get("data"));
    }

    /**
     * Tests getting tasks for a non-existent user.
     */
    @Test
    void getTasksForUser_UserNotFound() {
        Long userId = 1L;

        when(taskService.getTasksByUser(userId)).thenThrow(new UserNotExistException("User not found"));

        ResponseEntity<Map<String, Object>> response = taskController.getTasksForUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertEquals("User not found", responseBody.get("message"));
    }

    /**
     * Tests update task status when task is not found.
     */
    @Test
    void updateTaskStatus_TaskNotFound() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("status", "COMPLETED");

        doThrow(new TaskNotExistException("Task not found"))
                .when(taskService).updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertEquals("Task not found", responseBody.get("message"));
    }

    /**
     * Tests update task status with invalid enum value.
     */
    @Test
    void updateTaskStatus_InvalidEnumValue() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("status", "INVALID_STATUS");

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertTrue(responseBody.get("message").toString().contains("Error updating task status"));
    }

    /**
     * Tests update task assigned user with missing userId in request.
     */
    @Test
    void updateTaskAssignedUser_MissingUserId() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskAssignedUser(taskId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertTrue(responseBody.get("message").toString().contains("Error updating assigned user"));
    }

    /**
     * Tests update task assigned user with non-numeric userId.
     */
    @Test
    void updateTaskAssignedUser_NonNumericUserId() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("userId", "not-a-number");

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskAssignedUser(taskId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertTrue(responseBody.get("message").toString().contains("Error updating assigned user"));
    }

    /**
     * Tests update task assigned user when task is not found.
     */
    @Test
    void updateTaskAssignedUser_TaskNotFound() {
        Long taskId = 1L;
        Long newUserId = 2L;
        Map<String, Object> request = new HashMap<>();
        request.put("userId", newUserId);

        when(userService.findUserById(newUserId)).thenReturn(new User());
        doThrow(new TaskNotExistException("Task not found"))
                .when(taskService).updateTaskAssignedUser(taskId, newUserId);

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskAssignedUser(taskId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertEquals("Task not found", responseBody.get("message"));
        verify(userService).findUserById(newUserId);
    }

    /**
     * Tests for general exception in task status update.
     */
    @Test
    void updateTaskStatus_GeneralException() {
        Long taskId = 1L;
        Map<String, Object> request = new HashMap<>();
        request.put("status", "COMPLETED");

        doThrow(new RuntimeException("Unexpected error"))
                .when(taskService).updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);

        ResponseEntity<Map<String, Object>> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNull(responseBody.get("data"));
        assertTrue(responseBody.get("message").toString().contains("Error updating task status"));
    }

    /**
     * Tests create task with name key present but null value.
     */
    @Test
    void createTask_NullTaskName() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("name", null);
        taskDetails.put("description", "Test Description");

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Task name is required", responseBody.get("message"));
    }

    /**
     * Tests create task with empty task name.
     */
    @Test
    void createTask_EmptyTaskName() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("name", "   ");
        taskDetails.put("description", "Test Description");

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Task name is required", responseBody.get("message"));
    }

    /**
     * Tests getting tasks for user with null userId.
     */
    @Test
    void getTasksForUser_NullUserId() {
        Long userId = null;

        ResponseEntity<Map<String, Object>> response = taskController.getTasksForUser(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Invalid user ID. User ID must be a positive number.", responseBody.get("message"));
    }

    /**
     * Tests create task with minimal valid task details (just name).
     */
    @Test
    void createTask_MinimalValidTask() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("name", "Test Task");

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        Task responseTask = new Task();
        responseTask.setId(1L);
        responseTask.setName("Test Task");
        responseTask.setDescription("");
        responseTask.setStatus(Task.TaskStatus.PENDING);

        Event mockEvent = new Event();
        User mockUser = new User();
        when(eventService.findById(eventId)).thenReturn(mockEvent);
        when(userService.findUserById(userId)).thenReturn(mockUser);
        when(taskService.createTask(eq(eventId), eq(userId), any(Task.class)))
                .thenReturn(responseTask);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(Collections.singletonList(responseTask), responseBody.get("data"));
    }

    /**
     * Tests create task with invalid status in request.
     */
    @Test
    void createTask_InvalidStatus() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("name", "Test Task");
        taskDetails.put("status", "INVALID_STATUS");

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        Event mockEvent = new Event();
        User mockUser = new User();
        when(eventService.findById(eventId)).thenReturn(mockEvent);
        when(userService.findUserById(userId)).thenReturn(mockUser);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Invalid task status: INVALID_STATUS", responseBody.get("message"));
    }

    /**
     * Tests create task with explicit null status (should default to PENDING).
     */
    @Test
    void createTask_NullStatus() {
        Long eventId = 1L;
        Long userId = 1L;
        Map<String, Object> taskDetails = new HashMap<>();
        taskDetails.put("name", "Test Task");
        taskDetails.put("status", null);

        Map<String, Object> request = new HashMap<>();
        request.put("task", taskDetails);

        Task responseTask = new Task();
        responseTask.setId(1L);
        responseTask.setName("Test Task");
        responseTask.setStatus(Task.TaskStatus.PENDING);

        Event mockEvent = new Event();
        User mockUser = new User();
        when(eventService.findById(eventId)).thenReturn(mockEvent);
        when(userService.findUserById(userId)).thenReturn(mockUser);
        when(taskService.createTask(eq(eventId), eq(userId), any(Task.class)))
                .thenReturn(responseTask);

        ResponseEntity<Map<String, Object>> response = taskController.createTask(eventId, userId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(Collections.singletonList(responseTask), responseBody.get("data"));
    }
}
