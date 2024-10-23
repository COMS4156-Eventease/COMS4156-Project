package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.EventNotExistException;
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
     * @param task the task to be created
     * @return the saved task
     */
    public Task createTask(Long eventId, Long userId, Task task) {
        // Fetch the event using the event ID
        Event event = eventService.findById(eventId);
        if (event == null) {
            throw new EventNotExistException("Event with ID " + eventId + " does not exist.");
        }

        // Fetch the user using the user ID
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new UserNotExistException("User with ID " + userId + " does not exist.");
        }

        // Set the fetched event and user to the task
        task.setEvent(event);
        task.setAssignedUser(user);

        // Save and return the task
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

    @Transactional
    public void updateTaskStatus(Long eventId, Long taskId, Task.TaskStatus newStatus) throws TaskNotExistException, EventNotExistException {        // Fetch the task using the task ID
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotExistException("Task not found"));

        // Optionally, validate the event ID if necessary
        if (!task.getEvent().getId().equals(eventId)) {
            throw new EventNotExistException("Event ID does not match the task's event");
        }

        // Update the status of the task
        task.setStatus(newStatus);

        // Save the updated task
        taskRepository.save(task);
    }


/**
     * Updates the user assigned to a specific task.
     *
     * @param eventId the ID of the event to which the task belongs
     * @param taskId the ID of the task to update
     * @param userId the ID of the new assigned user
     * @throws TaskNotExistException if the task is not found
     */
    @Transactional
    public void updateTaskAssignedUser(Long eventId, Long taskId, Long userId) throws TaskNotExistException, EventNotExistException, UserNotExistException {
        // Fetch the user entity
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new UserNotExistException("User not found");
        }

        // Update the task's assigned user
        int rowsUpdated = taskRepository.updateTaskAssignedUser(taskId, eventId, user);
        if (rowsUpdated == 0) {
            throw new TaskNotExistException("Task or Event not found");
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
