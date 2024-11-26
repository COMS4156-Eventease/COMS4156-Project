package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.TaskNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TaskService is responsible for managing tasks in the system, including creating, updating, retrieving,
 * and deleting tasks. It also handles task-specific business logic such as verifying events and users.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    /**
     * Creates a new task associated with an event and a user.
     *
     * @param eventId the ID of the event to associate with the task
     * @param userId the ID of the user to associate with the task
     * @param task the task to be created
     * @return the saved task
     */
    @Transactional
    public Task createTask(Long eventId, Long userId, Task task) {
        Event event = eventService.findById(eventId);
        User user = userService.findUserById(userId);

        task.setEvent(event);
        task.setAssignedUser(user);

        return taskRepository.save(task);
    }

    /**
     * Retrieves a list of tasks for a specific event.
     *
     * @param eventId the ID of the event
     * @return a list of tasks associated with the event
     */
    public List<Task> getTasksByEvent(Long eventId) {
        try {
            List<Task> tasks = taskRepository.findByEventId(eventId);
            System.out.println("Fetched tasks: " + tasks);
            return tasks;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching tasks for event ID: " + eventId, e);
        }
    }

    /**
     * Updates the status of a specific task.
     *
     * @param taskId the ID of the task to update
     * @param newStatus the new status of the task
     * @throws TaskNotExistException if the task is not found
     */

    @Transactional
    public void updateTaskStatus(Long taskId, Task.TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId));
        task.setStatus(newStatus);
        taskRepository.save(task);
    }

    @Transactional
    public void updateTaskAssignedUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId));

        // Then check if user exists
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new UserNotExistException("User not found with ID: " + userId);
        }

        task.setAssignedUser(user);
        taskRepository.save(task);
    }


    /**
     * Deletes a specific task by its ID and associated event ID.
     *
     * @param taskId the ID of the task to delete
     */
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId));

        taskRepository.delete(task);
    }

    /**
     * Retrieves a list of tasks assigned to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of tasks assigned to the user
     */
    public List<Task> getTasksByUser(Long userId) {
        userService.findUserById(userId);
        
        return taskRepository.findByAssignedUserId(userId);
    }

    /**
     * Retrieves a specific task by its ID.
     *
     * @param taskId the ID of the task to retrieve
     * @return the task
     * @throws TaskNotExistException if the task is not found
     */
    public Task getTaskById(Long taskId) {
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Invalid task ID");
        }

        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId));
    }
}
