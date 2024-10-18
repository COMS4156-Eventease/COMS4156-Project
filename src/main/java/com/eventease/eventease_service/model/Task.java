package com.eventease.eventease_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * The Task entity represents a task associated with an event.
 * It contains fields such as task name, description, status, due date, associated event, and assigned user.
 * It also contains functions to retrieve and update the respective fields.
 */
@Entity
@Table(name = "tasks")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the task. This field is mandatory.
     */
    @NotBlank(message = "Task name is required")
    @Column(nullable = false)
    private String name;

    /**
     * The description of the task. This field is optional.
     */
    private String description;

    /**
     * The status of the task. This field is mandatory and uses the {@link TaskStatus} enum.
     */
    @NotNull(message = "Task status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /**
     * The due date of the task. This field is optional and currently not accessed by the TaskController.
     */
    private LocalDateTime dueDate;

    /**
     * The event associated with this task. This field is mandatory and references the {@link Event} entity.
     */
    @NotNull(message = "Event is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * The user assigned to this specific task. This field is mandatory and references the {@link User} entity.
     */
    @NotNull(message = "Assigned user is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id", nullable = false)
    private User assignedUser;

    /**
     * Enum representing the possible statuses of a task.
     */
    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
