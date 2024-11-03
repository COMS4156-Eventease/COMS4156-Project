package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.RSVPExistsException;
import com.eventease.eventease_service.exception.RSVPNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.service.RSVPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The contrller class contains the RSVP management endpoints
 */
@RequestMapping("/api/events/{eventId}/")
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
    Map<String, Object> response = new HashMap<>();
    try {
      RSVP createdRSVP = rsvpService.createRSVP(eventId, userId, rsvp);

      List<RSVP> dataList = new ArrayList<>();
      dataList.add(createdRSVP);
      response.put("success", true);
      response.put("data", dataList);

      return new ResponseEntity<>(response, HttpStatus.CREATED);

    } catch (EventNotExistException | UserNotExistException error) {

      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);

    } catch (RSVPExistsException error) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", e.getMessage());

      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
    Map<String, Object> response = new HashMap<>();
    try {
      List<RSVP> attendees = rsvpService.getAttendeesByEvent(eventId);

      response.put("success", true);
      response.put("data", attendees);


      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (EventNotExistException error){

      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    } catch (Exception error) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
    Map<String, Object> response = new HashMap<>();
    try{
      rsvpService.cancelRSVP(eventId, userId);

      response.put("success", true);
      response.put("data", new ArrayList<>());
      response.put("message", "RSVP successfully cancelled");

      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (EventNotExistException | UserNotExistException | RSVPNotExistException error) {

      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    } catch (Exception error) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }
  }

}
