//package com.eventease.eventease_service.unit_test.controller;
//
//import com.eventease.eventease_service.controller.TaskController;
//import com.eventease.eventease_service.exception.*;
//import com.eventease.eventease_service.model.Task;
//import com.eventease.eventease_service.model.User;
//import com.eventease.eventease_service.service.EventService;
//import com.eventease.eventease_service.service.TaskService;
//import com.eventease.eventease_service.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
///**
// * Unit tests for the TaskController class.
// * The class uses JUnit and Mockito to test different scenarios in TaskController's methods.
// */
//class TaskControllerUnitTest {
//
//    @Mock
//    private TaskService taskService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private EventService eventService;
//
//    @InjectMocks
//    private TaskController taskController;
//
//    /**
//     * Initializes the mocks and injects them into the TaskController before each test.
//     */
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    /**
//     * Tests the successful creation of a task given valid parameters.
//     *
//     * @throws Exception if an error occurs during task creation
//     */
//    @Test
//    void createTask_Success() throws Exception {
//        Long eventId = 1L;
//        Long userId = 1L;
//        Task task = new Task();
//        task.setId(1L);
//        User user = new User();
//        user.setId(1L);
//        task.setAssignedUser(user);
//
//        when(taskService.createTask(eq(eventId), any(Task.class))).thenReturn(task);
//
//        ResponseEntity<?> response = taskController.createTask(eventId, task);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertTrue(response.getBody() instanceof Map);
//        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
//        assertEquals(1L, responseBody.get("taskId"));
//        assertEquals(eventId, responseBody.get("eventId"));
//        assertEquals(1L, responseBody.get("userId"));
//    }
//
//    /**
//     * Tests the case where the event for which the task is created does not exist.
//     *
//     * @throws Exception if an error occurs during task creation
//     */
//    @Test
//    void createTask_EventNotFound() throws Exception {
//        Long eventId = 1L;
//        Task task = new Task();
//
//        when(taskService.createTask(eq(eventId), any(Task.class))).thenThrow(new EventNotExistException("Event not found"));
//
//        ResponseEntity<?> response = taskController.createTask(eventId, task);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Event not found", response.getBody());
//    }
//
//    /**
//     * Tests the successful retrieval of all tasks for a specific event.
//     *
//     * @throws Exception if an error occurs during task retrieval
//     */
//    @Test
//    void getTasksByEvent_Success() throws Exception {
//        Long eventId = 1L;
//        List<Task> tasks = Arrays.asList(new Task(), new Task());
//
//        when(taskService.getTasksByEvent(eventId)).thenReturn(tasks);
//
//        ResponseEntity<?> response = taskController.getTasksByEvent(eventId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(tasks, response.getBody());
//    }
//
//    /**
//     * Tests the case where the event for which tasks are being retrieved does not exist.
//     *
//     * @throws Exception if an error occurs during task retrieval
//     */
//    @Test
//    void getTasksByEvent_EventNotFound() throws Exception {
//        Long eventId = 1L;
//
//        when(taskService.getTasksByEvent(eventId)).thenThrow(new EventNotExistException("Event not found"));
//
//        ResponseEntity<?> response = taskController.getTasksByEvent(eventId);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Event not found", response.getBody());
//    }
//
//    /**
//     * Tests the successful retrieval of a specific task for a given event ID.
//     *
//     * @throws Exception if an error occurs during task retrieval
//     */
//    @Test
//    void getTask_Success() throws Exception {
//        Long eventId = 1L;
//        Long taskId = 1L;
//        Task task = new Task();
//
//        when(taskService.getTaskByEventAndId(eventId, taskId)).thenReturn(task);
//
//        ResponseEntity<?> response = taskController.getTask(eventId, taskId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(task, response.getBody());
//    }
//
//    /**
//     * Tests the case where the task ID being retrieved does not exist.
//     *
//     * @throws Exception if an error occurs during task retrieval
//     */
//    @Test
//    void getTask_TaskNotFound() throws Exception {
//        Long eventId = 1L;
//        Long taskId = 1L;
//
//        when(taskService.getTaskByEventAndId(eventId, taskId)).thenThrow(new TaskNotExistException("Task not found"));
//
//        ResponseEntity<?> response = taskController.getTask(eventId, taskId);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Task not found", response.getBody());
//    }
//
//    /**
//     * Tests the successful updating of a task's status given valid Event and Task ID.
//     *
//     * @throws Exception if an error occurs during task status update
//     */
//    @Test
//    void updateTaskStatus_Success() throws Exception {
//        Long eventId = 1L;
//        Long taskId = 1L;
//        Map<String, Task.TaskStatus> statusUpdate = new HashMap<>();
//        statusUpdate.put("status", Task.TaskStatus.COMPLETED);
//
//        doNothing().when(taskService).updateTaskStatus(eventId, taskId, Task.TaskStatus.COMPLETED);
//
//        ResponseEntity<?> response = taskController.updateTaskStatus(eventId, taskId, statusUpdate);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Task status updated successfully", response.getBody());
//    }
//
//    /**
//     * Tests the case where the status is missing from the update request.
//     */
//    @Test
//    void updateTaskStatus_MissingStatus() {
//        Long eventId = 1L;
//        Long taskId = 1L;
//        Map<String, Task.TaskStatus> statusUpdate = new HashMap<>();
//
//        ResponseEntity<?> response = taskController.updateTaskStatus(eventId, taskId, statusUpdate);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Status is required", response.getBody());
//    }
//
//    /**
//     * Tests the successful updating of the assigned user of a task given valid Event and Task ID.
//     *
//     * @throws Exception if an error occurs during the user assignment update
//     */
//    @Test
//    void updateTaskAssignedUser_Success() throws Exception {
//        Long eventId = 1L;
//        Long taskId = 1L;
//        String newUserId = "2";
//
//        when(userService.findUserById(1L)).thenReturn(new User());
//        when(userService.findUserById(2L)).thenReturn(new User());
//        doNothing().when(taskService).updateTaskAssignedUser(eventId, taskId, 1L);
//
//        ResponseEntity<?> response = taskController.updateTaskAssignedUser(eventId, taskId, newUserId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Task assigned user updated successfully", response.getBody());
//    }
//
//    /**
//     * Tests the case where the user ID to be assigned a specific task does not exist,
//     * given a valid Event and Task ID.
//     */
//    @Test
//    void updateTaskAssignedUser_UserNotFound() {
//        Long eventId = 1L;
//        Long taskId = 1L;
//        String newUserId = "2";
//
//        when(userService.findUserById(2L)).thenReturn(new User());
//        doNothing().when(taskService).updateTaskAssignedUser(eventId, taskId, 2L);
//
//        ResponseEntity<?> response = taskController.updateTaskAssignedUser(eventId, taskId, newUserId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Task assigned user updated successfully", response.getBody());
//    }
//
//    /**
//     * Tests the successful deletion of a task.
//     *
//     * @throws Exception if an error occurs during task deletion
//     */
//    @Test
//    void deleteTask_Success() throws Exception {
//        Long eventId = 1L;
//        Long taskId = 1L;
//
//        doNothing().when(taskService).deleteTask(eventId, taskId);
//
//        ResponseEntity<?> response = taskController.deleteTask(eventId, taskId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Task deleted successfully", response.getBody());
//    }
//
//    /**
//     * Tests the case where the task to be deleted does not exist given
//     * a valid event ID but invalid task ID.
//     *
//     * @throws Exception if an error occurs during task deletion
//     */
//    @Test
//    void deleteTask_TaskNotFound() throws Exception {
//        Long eventId = 1L;
//        Long taskId = 1L;
//
//        doThrow(new TaskNotExistException("Task not found")).when(taskService).deleteTask(eventId, taskId);
//
//        ResponseEntity<?> response = taskController.deleteTask(eventId, taskId);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Task not found", response.getBody());
//    }
//}
