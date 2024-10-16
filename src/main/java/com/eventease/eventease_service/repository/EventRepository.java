package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.Event;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
  Event findById(long id);

  @Query("SELECT e FROM Event e WHERE e.date BETWEEN :startDate AND :endDate")
  List<Event> findEventsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
