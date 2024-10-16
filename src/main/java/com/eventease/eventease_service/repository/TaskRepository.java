package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findById(long id);

    List<Task> findByAssignedUserId(long userId);

    List<Task> findByEventId(long eventId);

    //List<Task> findByStatus(Task.TaskStatus status);
    //List<Task> findByDueDateBefore(java.sql.Timestamp dueDate);
}