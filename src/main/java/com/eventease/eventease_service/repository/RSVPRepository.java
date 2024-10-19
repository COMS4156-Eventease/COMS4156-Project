package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.RSVPKey;
import com.eventease.eventease_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, RSVPKey> {

  Optional<RSVP> findByUserAndEvent(User user, Event event);

  List<RSVP> findByEvent(Event event);

  List<RSVP> findByUser(User user);
}
