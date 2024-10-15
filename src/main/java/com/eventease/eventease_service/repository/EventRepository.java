package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.Event;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
  Event findById(long id);

  List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
