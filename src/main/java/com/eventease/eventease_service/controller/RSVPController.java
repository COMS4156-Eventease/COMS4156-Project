package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.RSVPNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.repository.RSVPRepository;
import com.eventease.eventease_service.service.RSVPService;
import jakarta.persistence.Embedded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The contrller class contains the RSVP management endpoints
 */
@RequestMapping("/events/{eventId}/")
@RestController
public class RSVPController {
  @Autowired
  private RSVPService rsvpService;

  /**
   * Endpoint for creating an RSVP for a user to an event
   * This method handles POST requests to create the RSVP for a user to an event;
   *
   * @param eventId                 the ID of the event
   * @param userId                  the ID of the user making the RSVP
   *
   * @return                          a ResponseEntity with successful message
   *                                  or an error message if the event is not found
   *                                  or an error message if the user is not found
   */
  @RequestMapping(value = "/rsvp/{userId}", method = RequestMethod.POST)
  public ResponseEntity<?> createRSVP(@PathVariable String eventId, @PathVariable String userId, @RequestBody RSVP rsvp) {
    try {
      RSVP createdRSVP = rsvpService.createRSVP(eventId, userId, rsvp);
      return new ResponseEntity<>(createdRSVP, HttpStatus.CREATED);
    } catch (EventNotExistException | UserNotExistException error) {
      return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Endpoint for creating an RSVP for a user to an event
   * This method handles GET requests to retrieve the list of RSVPs to an event;
   *
   * @param eventId                 the ID of the event
   *
   * @return                          a ResponseEntity with successful message
   *                                  or an error message if the event is not found
   */
  @RequestMapping(value = "/attendees", method = RequestMethod.GET)
  public ResponseEntity<?> getAttendee(@PathVariable String eventId) {
    try {
      List<RSVP> attendees = rsvpService.getAttendeesByEvent(eventId);
      return new ResponseEntity<>(attendees, HttpStatus.OK);
    } catch (EventNotExistException error){
      return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Endpoint for delete an RSVP for a user to an event
   * This method handles DELETE requests to delete an RSVP of a user to an event;
   *
   * @param eventId                 the ID of the event
   * @param userId                  the ID of the user making the RSVP
   *
   * @return                          a ResponseEntity with successful message
   *                                  or an error message if the event is not found
   */
  @RequestMapping(value = "/rsvp/cancel/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> cancelRSVP(@PathVariable String eventId, @PathVariable String userId) {
    try{
      rsvpService.cancelRSVP(eventId, userId);
      return new ResponseEntity<>("RSVP successfully cancelled", HttpStatus.OK);
    } catch (RSVPNotExistException error) {
      return new ResponseEntity<>("RSVP Not Found", HttpStatus.NOT_FOUND);
    } catch (EventNotExistException | UserNotExistException error){
      return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

}
