package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.TaskNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;


    public Task createTask(String eventId, String userId, Task task) {
        Event event = eventService.findById(Long.parseLong(eventId));
        User user = userService.findUserById(Long.parseLong(userId));
        task.setEvent(event);
        task.setAssignedUser(user);

        return taskRepository.save(task);
    }

    public List<Task> getTasksByEvent(String eventId) {
        Event event = eventService.findById(Long.parseLong(eventId));
        return taskRepository.findByEventId(event.getId());
    }

    public List<Task> getTasksByUser(String userId) {
        User user = userService.findUserById(Long.parseLong(userId));
        return taskRepository.findByAssignedUserId(user.getId());
    }

    public Task findTaskById(long taskId) {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new TaskNotExistException("Task not found with ID: " + taskId);
        }
        return task;
    }

    public String updateTask(long taskId, Task updatedTask) {
        Task existingTask = findTaskById(taskId);

        if (updatedTask.getName() != null) existingTask.setName(updatedTask.getName());
        if (updatedTask.getDescription() != null) existingTask.setDescription(updatedTask.getDescription());
        if (updatedTask.getStatus() != null) existingTask.setStatus(updatedTask.getStatus());
        if (updatedTask.getDueDate() != null) existingTask.setDueDate(updatedTask.getDueDate());

        taskRepository.save(existingTask);
        return "Task updated successfully";
    }

    public String assignTaskToUser(long taskId, String userId) {
        Task task = findTaskById(taskId);
        User user = userService.findUserById(Long.parseLong(userId));

        if (task.getAssignedUser() != null && task.getAssignedUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Task is already assigned to this user");
        }

        task.setAssignedUser(user);
        taskRepository.save(task);
        return "Task assigned to user successfully";
    }

    public String assignTaskToEvent(long taskId, long eventId) {
        Task task = findTaskById(taskId);
        Event event = eventService.findById(eventId);


        if (task.getEvent() != null && task.getEvent().getId().equals(event.getId())) {
            throw new IllegalStateException("Task is already assigned to this event");
        }

        task.setEvent(event);
        taskRepository.save(task);
        return "Task assigned to event successfully";
    }

    public String deleteTask(long taskId) {
        Task task = findTaskById(taskId);
        taskRepository.delete(task);
        return "Task deleted successfully";
    }
}
