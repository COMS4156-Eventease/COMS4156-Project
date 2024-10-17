package com.eventease.eventease_service.unit_test;

import com.eventease.eventease_service.controller.TaskController;
import com.eventease.eventease_service.exception.TaskNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.Task.TaskStatus;
import com.eventease.eventease_service.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerMockMvcTest {

    private static MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private static ObjectMapper objectMapper;

    private static Task task;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        task = new Task();
        task.setId(1L);
        task.setName("Sample Task");
        task.setDescription("Task Description");
        task.setStatus(TaskStatus.PENDING);
        task.setDueDate(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    public void createTaskSuccess() throws Exception {
        when(taskService.createTask(any(String.class), any(String.class), any(Task.class))).thenReturn(task);

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(post("/events/1/tasks")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value(task.getId()))
                .andExpect(jsonPath("$.eventId").value("1"))
                .andExpect(jsonPath("$.userId").value("1"));
    }

    @Test
    public void createTaskUserNotFoundFailure() throws Exception {
        when(taskService.createTask(any(String.class), any(String.class), any(Task.class))).thenThrow(new UserNotExistException("User Not Found"));

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(post("/events/1/tasks")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User Not Found"));
    }

    @Test
    public void getTasksByEventSuccess() throws Exception {
        List<Task> tasks = Collections.singletonList(task);
        when(taskService.getTasksByEvent(any(String.class))).thenReturn(tasks);

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(get("/events/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(task.getId()))
                .andExpect(jsonPath("$[0].name").value(task.getName()));
    }

    @Test
    public void getTasksByEventFailure() throws Exception {
        when(taskService.getTasksByEvent(any(String.class))).thenThrow(new RuntimeException("Error occurred"));

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(get("/events/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while fetching tasks"));
    }

    @Test
    public void updateTaskSuccess() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(patch("/events/1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task updated successfully"));
    }

    @Test
    public void updateTaskNotFoundFailure() throws Exception {
        doThrow(new TaskNotExistException("Task Not Found")).when(taskService).updateTask(anyLong(), any(Task.class));

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(patch("/events/1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task Not Found"));
    }

    @Test
    public void deleteTaskSuccess() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(delete("/events/1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted successfully"));
    }

    @Test
    public void deleteTaskNotFoundFailure() throws Exception {
        doThrow(new TaskNotExistException("Task Not Found")).when(taskService).deleteTask(anyLong());

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        mockMvc.perform(delete("/events/1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task Not Found"));
    }
}
