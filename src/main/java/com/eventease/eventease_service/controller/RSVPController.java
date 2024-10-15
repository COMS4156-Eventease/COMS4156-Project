package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.repository.RSVPRepository;
import jakarta.persistence.Embedded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/events/{eventId}/")
@RestController
public class RSVPController {
  @Autowired
  RSVPRepository rsvpRepository;

  @RequestMapping(value = "/rsvp", method = RequestMethod.POST)
  public ResponseEntity<?> createRSVP(@PathVariable String eventId, @RequestBody RSVP rsvp) {
    return rsvpRepository.createRSVP(eventId, rsvp);
  }

  @RequestMapping(value = "/attendees", method = RequestMethod.GET)
  public RSVP getAttendee(@PathVariable String eventId) {
    // to do
  }

  @RequestMapping(value = "/rsvp/{userId}", method = RequestMethod.DELETE)
  public RSVP cancelRSVP(@PathVariable String eventId, @PathVariable String userId) {
    // to do
  }




}
