package com.eventease.eventease_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task")
@Getter
@Setter
public class Task implements Serializable {

    @Serial
    private static final long serialVersionUID = 100002L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("name")
    @Column(nullable = false)
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @JsonProperty("dueDate")
    private Timestamp dueDate;

    @JsonProperty("createdAt")
    private Timestamp createdAt;

    @JsonProperty("updatedAt")
    private Timestamp updatedAt;

    // a task is assigned to a specific user (user creates many tasks)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User assignedUser;

    // a task belongs to an event (many tasks for one event)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Task() {};

    public Task(String name, String description, TaskStatus status, Timestamp dueDate, User assignedUser, Event event) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.assignedUser = assignedUser;
        this.event = event;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public enum TaskStatus {
        PENDING,
        COMPLETED,
        IN_PROGRESS,
        CANCELLED
    }
}
