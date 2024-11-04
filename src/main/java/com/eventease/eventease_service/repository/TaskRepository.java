package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.Task;
import com.eventease.eventease_service.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findById(Long id);

    List<Task> findByEventId(Long eventId);


    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :taskId AND t.event.id = :eventId")
    int updateTaskStatus(Long taskId, Long eventId, Task.TaskStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.assignedUser = :user WHERE t.id = :taskId AND t.event.id = :eventId")
    int updateTaskAssignedUser( Long taskId, User user);


    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.id = :taskId AND t.event.id = :eventId")
    void deleteTask(Long taskId);

    List<Task> findByAssignedUserId(Long userId);
}