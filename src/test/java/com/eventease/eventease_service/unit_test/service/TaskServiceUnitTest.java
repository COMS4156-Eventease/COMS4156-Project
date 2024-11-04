package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.exception.TaskNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.TaskRepository;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.TaskService;
import com.eventease.eventease_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit tests for the TaskService class.
 * It uses Mockito to mock dependencies and tests various scenarios
 * to ensure the correct behavior of the TaskService methods.
 */
public class TaskServiceUnitTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private UserService userService;

  @Mock
  private EventService eventService;

  @InjectMocks
  private TaskService taskService;

  private Event event;
  private User user;
  private Task task;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    event = new Event();
    event.setId(1L);

    user = new User();
    user.setId(1L);

    task = new Task();
    task.setId(1L);
    task.setEvent(event);
    task.setAssignedUser(user);
  }

  @Test
  void testCreateTask() {
    when(eventService.findById(anyLong())).thenReturn(event);
    when(userService.findUserById(anyLong())).thenReturn(user);
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    Task result = taskService.createTask(1L, 1L, task); // Pass both eventId and userId

    assertNotNull(result);
    assertEquals(task, result);
    verify(taskRepository).save(task);
  }

  @Test
  void testGetTasksByEvent() {
    List<Task> tasks = Arrays.asList(task);
    when(taskRepository.findByEventId(anyLong())).thenReturn(tasks);

    List<Task> result = taskService.getTasksByEvent(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(task, result.get(0));
  }

  @Test
  void testGetTaskId_TaskExists() {
    when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

    Task result = taskService.getTaskById(1L);

    assertNotNull(result);
    assertEquals(task, result);
  }

  @Test
  void testGetTaskByEventAndId_TaskNotExist() {
    when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

    TaskNotExistException exception = assertThrows(TaskNotExistException.class,
            () -> taskService.getTaskById(1L));
    assertEquals("Task not found with ID: 1", exception.getMessage());
  }

  @Test
  void testUpdateTaskStatus_TaskExists() {
    when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

    assertDoesNotThrow(() -> taskService.updateTaskStatus( 1L, Task.TaskStatus.IN_PROGRESS));
    verify(taskRepository).save(task);
  }

  @Test
  void testUpdateTaskStatus_TaskNotExist() {
    when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(TaskNotExistException.class, () -> taskService.updateTaskStatus(1L, Task.TaskStatus.IN_PROGRESS));
  }

  @Test
  void testUpdateTaskAssignedUser_TaskExists() {
    Long taskId = 1L;
    Long userId = 1L;

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userService.findUserById(userId)).thenReturn(user);

    assertDoesNotThrow(() -> taskService.updateTaskAssignedUser(taskId, userId));

    verify(taskRepository).findById(taskId);
    verify(userService).findUserById(userId);
    verify(taskRepository).save(task);
  }

  @Test
  void testUpdateTaskAssignedUser_TaskNotExist() {
    Long taskId = 1L;
    Long userId = 1L;

    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    TaskNotExistException exception = assertThrows(
            TaskNotExistException.class,
            () -> taskService.updateTaskAssignedUser(taskId, userId)
    );

    assertEquals("Task not found with ID: " + taskId, exception.getMessage());

    verify(taskRepository).findById(taskId);
    verify(userService, never()).findUserById(anyLong());
    verify(taskRepository, never()).save(any());
  }

  @Test
  void testUpdateTaskAssignedUser_UserNotExist() {
    Long taskId = 1L;
    Long userId = 1L;

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userService.findUserById(userId)).thenReturn(null);

    UserNotExistException exception = assertThrows(
            UserNotExistException.class,
            () -> taskService.updateTaskAssignedUser(taskId, userId)
    );

    assertEquals("User not found with ID: " + userId, exception.getMessage());

    verify(taskRepository).findById(taskId);
    verify(userService).findUserById(userId);
    verify(taskRepository, never()).save(any());
  }

  @Test
  void testDeleteTask() {
    doNothing().when(taskRepository).deleteTask(anyLong());

    assertDoesNotThrow(() -> taskService.deleteTask(1L));
    verify(taskRepository).deleteTask(1L);
  }

  @Test
  void testGetTasksByUser() {
    List<Task> tasks = Arrays.asList(task);
    when(taskRepository.findByAssignedUserId(anyLong())).thenReturn(tasks);

    List<Task> result = taskService.getTasksByUser(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(task, result.get(0));
  }

  @Test
  void testGetTaskById_TaskExists() {
    when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

    Task result = taskService.getTaskById(1L);

    assertNotNull(result);
    assertEquals(task, result);
  }

  @Test
  void testGetTaskById_TaskNotExist() {
    when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(TaskNotExistException.class, () -> taskService.getTaskById(1L));
  }
}