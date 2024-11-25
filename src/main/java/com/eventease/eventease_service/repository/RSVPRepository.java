package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.RSVPKey;
import com.eventease.eventease_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, RSVPKey> {

  Optional<RSVP> findByUserAndEvent(User user, Event event);

  List<RSVP> findByEvent(Event event);
  
  List<RSVP> findAllByUserOrderByEventDate(User user);

  List<RSVP> findAllByUserAndStatusOrderByEventDate(User user, String status);

  @Query("SELECT r FROM RSVP r WHERE r.user.id = :userId AND " +
          "((:startTime BETWEEN r.startTime AND r.endTime) OR " +
          "(:endTime BETWEEN r.startTime AND r.endTime) OR " +
          "(r.startTime BETWEEN :startTime AND :endTime))")
  List<RSVP> findOverlappingRSVPs(@Param("userId") Long userId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);
}
