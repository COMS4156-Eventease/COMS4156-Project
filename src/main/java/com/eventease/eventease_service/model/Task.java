package com.eventease.eventease_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "task")
public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 100012L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private TaskStatus status = TaskStatus.UNASSIGNED; // ["UNASSIGNED", "PENDING", "COMPLETED"]

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User assignedUser;
    // private List<User> assignedUsers;

    public enum TaskStatus {
        UNASSIGNED,
        PENDING,
        COMPLETED
    }

    public Task() {};

    public Task(String name, TaskStatus status, Event event, User assignedUser) {
        this.name = name;
        this.status = status;
        this.event = event;
        this.assignedUser = assignedUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    // can assign multiple users?
    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }
}