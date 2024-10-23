package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.TaskNotExistException;
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
     * @param task the task to be created
     * @return the saved task
     */
    public Task createTask(Long eventId, Task task) {
        Long userId = task.getId();
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
        return taskRepository.findByEventId(eventId);
    }

    /**
     * Retrieves a specific task by its ID and associated event ID.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to retrieve
     * @return the task
     * @throws TaskNotExistException if the task is not found
     */
    public Task getTaskByEventAndId(Long eventId, Long taskId) {
        return taskRepository.findByIdAndEventId(taskId, eventId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId + " for event ID: " + eventId));
    }

    /**
     * Updates the status of a specific task.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to update
     * @param status the new status of the task
     * @throws TaskNotExistException if the task is not found
     */
    public void updateTaskStatus(Long eventId, Long taskId, Task.TaskStatus status) {
        int updatedRows = taskRepository.updateTaskStatus(taskId, eventId, status);
        if (updatedRows == 0) {
            throw new TaskNotExistException("Task not found with ID: " + taskId + " for event ID: " + eventId);
        }
    }

    /**
     * Updates the user assigned to a specific task.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to update
     * @param userId the ID of the new assigned user
     * @throws TaskNotExistException if the task is not found
     */
    public void updateTaskAssignedUser(Long eventId, Long taskId, Long userId) {
        userService.findUserById(userId); // Verify user exists
        int updatedRows = taskRepository.updateTaskAssignedUser(taskId, eventId, userId);
        if (updatedRows == 0) {
            throw new TaskNotExistException("Task not found with ID: " + taskId + " for event ID: " + eventId);
        }
    }

    /**
     * Deletes a specific task by its ID and associated event ID.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to delete
     */
    public void deleteTask(Long eventId, Long taskId) {
        taskRepository.deleteByIdAndEventId(taskId, eventId);
    }

    /**
     * Retrieves a list of tasks assigned to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of tasks assigned to the user
     */
    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    /**
     * Retrieves a specific task by its ID.
     *
     * @param taskId the ID of the task to retrieve
     * @return the task
     * @throws TaskNotExistException if the task is not found
     */
    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotExistException("Task not found with ID: " + taskId));
    }
}
