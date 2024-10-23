//package com.eventease.eventease_service.unit_test.service;
//
//import com.eventease.eventease_service.exception.TaskNotExistException;
//import com.eventease.eventease_service.model.Event;
//import com.eventease.eventease_service.model.Task;
//import com.eventease.eventease_service.model.User;
//import com.eventease.eventease_service.repository.TaskRepository;
//import com.eventease.eventease_service.service.EventService;
//import com.eventease.eventease_service.service.TaskService;
//import com.eventease.eventease_service.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * This class contains unit tests for the TaskService class.
// * It uses Mockito to mock dependencies and tests various scenarios
// * to ensure the correct behavior of the TaskService methods.
// */
//@SpringBootTest
//public class TaskServiceUnitTest {
//
//  @Mock
//  private TaskRepository taskRepository;
//
//  @Mock
//  private UserService userService;
//
//  @Mock
//  private EventService eventService;
//
//  @InjectMocks
//  private TaskService taskService;
//
//  private Event event;
//  private User user;
//  private Task task;
//
//  /**
//   * Sets up the test environment before each test method.
//   */
//  @BeforeEach
//  void setUp() {
//    event = new Event();
//    event.setId(1L);
//
//    user = new User();
//    user.setId(1L);
//
//    task = new Task();
//    task.setId(1L);
//    task.setEvent(event);
//    task.setAssignedUser(user);
//  }
//
//  /**
//   * Tests the createTask method of TaskService.
//   */
//  @Test
//  void testCreateTask() {
//    when(eventService.findById(anyLong())).thenReturn(event);
//    when(userService.findUserById(anyLong())).thenReturn(user);
//    when(taskRepository.save(any(Task.class))).thenReturn(task);
//
//    Task result = taskService.createTask(1L, task);
//
//    assertNotNull(result);
//    assertEquals(task, result);
//    verify(taskRepository).save(task);
//  }
//
//  /**
//   * Tests the getTasksByEvent method of TaskService.
//   */
//  @Test
//  void testGetTasksByEvent() {
//    List<Task> tasks = Arrays.asList(task);
//    when(taskRepository.findByEventId(anyLong())).thenReturn(tasks);
//
//    List<Task> result = taskService.getTasksByEvent(1L);
//
//    assertNotNull(result);
//    assertEquals(1, result.size());
//    assertEquals(task, result.get(0));
//  }
//
//  /**
//   * Tests the getTaskByEventAndId method of TaskService when the task exists.
//   */
//  @Test
//  void testGetTaskByEventAndId_TaskExists() {
//    when(taskRepository.findByIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(task));
//
//    Task result = taskService.getTaskByEventAndId(1L, 1L);
//
//    assertNotNull(result);
//    assertEquals(task, result);
//  }
//
//  /**
//   * Tests the getTaskByEventAndId method of TaskService when the task does not exist.
//   */
//  @Test
//  void testGetTaskByEventAndId_TaskNotExist() {
//    when(taskRepository.findByIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.empty());
//
//    assertThrows(TaskNotExistException.class, () -> taskService.getTaskByEventAndId(1L, 1L));
//  }
//
//  /**
//   * Tests the updateTaskStatus method of TaskService when the task exists.
//   */
//  @Test
//  void testUpdateTaskStatus_TaskExists() {
//    when(taskRepository.updateTaskStatus(anyLong(), anyLong(), any(Task.TaskStatus.class))).thenReturn(1);
//
//    assertDoesNotThrow(() -> taskService.updateTaskStatus(1L, 1L, Task.TaskStatus.IN_PROGRESS));
//  }
//
//  /**
//   * Tests the updateTaskStatus method of TaskService when the task does not exist.
//   */
//  @Test
//  void testUpdateTaskStatus_TaskNotExist() {
//    when(taskRepository.updateTaskStatus(anyLong(), anyLong(), any(Task.TaskStatus.class))).thenReturn(0);
//
//    assertThrows(TaskNotExistException.class, () -> taskService.updateTaskStatus(1L, 1L, Task.TaskStatus.IN_PROGRESS));
//  }
//
//  /**
//   * Tests the updateTaskAssignedUser method of TaskService when the task exists.
//   */
//  @Test
//  void testUpdateTaskAssignedUser_TaskExists() {
//    when(userService.findUserById(anyLong())).thenReturn(user);
//    when(taskRepository.updateTaskAssignedUser(anyLong(), anyLong(), anyLong())).thenReturn(1);
//
//    assertDoesNotThrow(() -> taskService.updateTaskAssignedUser(1L, 1L, 1L));
//  }
//
//  /**
//   * Tests the updateTaskAssignedUser method of TaskService when the task does not exist.
//   */
//  @Test
//  void testUpdateTaskAssignedUser_TaskNotExist() {
//    when(userService.findUserById(anyLong())).thenReturn(user);
//    when(taskRepository.updateTaskAssignedUser(anyLong(), anyLong(), anyLong())).thenReturn(0);
//
//    assertThrows(TaskNotExistException.class, () -> taskService.updateTaskAssignedUser(1L, 1L, 1L));
//  }
//
//  /**
//   * Tests the deleteTask method of TaskService.
//   */
//  @Test
//  void testDeleteTask() {
//    doNothing().when(taskRepository).deleteByIdAndEventId(anyLong(), anyLong());
//
//    assertDoesNotThrow(() -> taskService.deleteTask(1L, 1L));
//    verify(taskRepository).deleteByIdAndEventId(1L, 1L);
//  }
//
//  /**
//   * Tests the getTasksByUser method of TaskService.
//   */
//  @Test
//  void testGetTasksByUser() {
//    List<Task> tasks = Arrays.asList(task);
//    when(taskRepository.findByAssignedUserId(anyLong())).thenReturn(tasks);
//
//    List<Task> result = taskService.getTasksByUser(1L);
//
//    assertNotNull(result);
//    assertEquals(1, result.size());
//    assertEquals(task, result.get(0));
//  }
//
//  /**
//   * Tests the findTaskById method of TaskService when the task exists.
//   */
//  @Test
//  void testFindTaskById_TaskExists() {
//    when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
//
//    Task result = taskService.findTaskById(1L);
//
//    assertNotNull(result);
//    assertEquals(task, result);
//  }
//
//  /**
//   * Tests the findTaskById method of TaskService when the task does not exist.
//   */
//  @Test
//  void testFindTaskById_TaskNotExist() {
//    when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//    assertThrows(TaskNotExistException.class, () -> taskService.findTaskById(1L));
//  }
//}