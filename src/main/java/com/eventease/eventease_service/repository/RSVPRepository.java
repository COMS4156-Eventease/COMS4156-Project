package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.RSVPId;
import com.eventease.eventease_service.service.RSVPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, RSVPId> {
  @Autowired RSVPService rsvpService;
}
