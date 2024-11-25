package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.*;
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
@RequestMapping("/api/events/")
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
  @RequestMapping(value = "{eventId}/rsvp/{userId}", method = RequestMethod.POST)
  public ResponseEntity<?> createRSVP(@PathVariable String eventId, @PathVariable String userId, @RequestBody RSVP rsvp) {
    Map<String, Object> response = new HashMap<>();
    try {
      RSVP createdRSVP = rsvpService.createRSVP(eventId, userId, rsvp);

      List<RSVP> dataList = new ArrayList<>();
      dataList.add(createdRSVP);
      response.put("success", true);
      response.put("data", dataList);

      return new ResponseEntity<>(response, HttpStatus.CREATED);

    } catch (EventNotExistException | UserNotExistException | RSVPOverlapException error) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    } catch (RSVPExistsException error) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.CONFLICT);

    } catch (Exception e) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", e.getMessage());

      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Endpoint for getting all the RSVPs for an event
   * This method handles GET requests to retrieve the list of RSVPs to an event;
   *
   * @param eventId                 the ID of the event
   *
   * @return                          a ResponseEntity with successful message
   *                                  or an error message if the event is not found
   */
  @RequestMapping(value = "{eventId}/attendees", method = RequestMethod.GET)
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
  @DeleteMapping(value = "{eventId}/rsvp/cancel/{userId}")
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

  /**
   * Endpoint for updating an RSVP for a user to an event.
   * This method handles PATCH requests to partially update an RSVP.
   *
   * @param eventId                 the ID of the event
   * @param userId                  the ID of the user making the RSVP
   * @param rsvpUpdates             the RSVP fields to update
   *
   * @return                        a ResponseEntity with the updated RSVP or an error message if the RSVP does not exist
   */
  @RequestMapping(value = "{eventId}/rsvp/{userId}", method = RequestMethod.PATCH)
  public ResponseEntity<?> updateRSVP(@PathVariable String eventId, @PathVariable String userId, @RequestBody Map<String, Object> rsvpUpdates) {
    Map<String, Object> response = new HashMap<>();
    try {
      RSVP updatedRSVP = rsvpService.updateRSVP(eventId, userId, rsvpUpdates);

      List<RSVP> dataList = new ArrayList<>();
      dataList.add(updatedRSVP);
      response.put("success", true);
      response.put("data", dataList);

      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (EventNotExistException | UserNotExistException | RSVPNotExistException error) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    } catch (Exception e) {
      response.put("success", false);
      response.put("data", new ArrayList<>());
      response.put("message", e.getMessage());

      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Endpoint for checking in a user to an event
   * This method handles POST requests to check in a user to an event;
   *
   * @param eventId                 the ID of the event
   * @param userId                  the ID of the user being checked in
   *
   * @return                        a ResponseEntity with a success message
   *                                or an error message if the RSVP does not exist
   */
  @RequestMapping(value = "{eventId}/rsvp/checkin/{userId}", method = RequestMethod.POST)
  public ResponseEntity<?> checkInUser(@PathVariable String eventId, @PathVariable String userId) {
    Map<String, Object> response = new HashMap<>();
    try {
      // Check-in logic in RSVPService
      rsvpService.checkInUser(eventId, userId);

      response.put("success", true);
      response.put("message", "User successfully checked in");

      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (EventNotExistException | UserNotExistException | RSVPNotExistException error) {
      response.put("success", false);
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    } catch (Exception error) {
      response.put("success", false);
      response.put("message", error.getMessage());

      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Endpoint for retrieving all RSVPs for a specific user.
   * This method handles GET requests to fetch all RSVPs (both checked-in and not checked-in) for a user.
   *
   * @param userId The ID of the user whose RSVPs are to be retrieved.
   * @return A ResponseEntity containing a list of RSVPs for the user, sorted by date in ascending order.
   *         Returns an error message if the user does not exist.
   */
  @RequestMapping(value = "/rsvp/user/{userId}", method = RequestMethod.GET)
  public ResponseEntity<?> getAllRSVPsForUser(@PathVariable String userId) {
    Map<String, Object> response = new HashMap<>();
    try {
      List<RSVP> rsvps = rsvpService.getAllRSVPsByUser(userId);

      response.put("success", true);
      response.put("data", rsvps);

      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (UserNotExistException error) {
      response.put("success", false);
      response.put("message", error.getMessage());
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    } catch (Exception error) {
      response.put("success", false);
      response.put("message", error.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Endpoint for retrieving all checked-in RSVPs for a specific user.
   * This method processes GET requests to fetch RSVPs where the user has checked in.
   *
   * @param userId The ID of the user whose checked-in RSVPs are to be retrieved.
   * @return A ResponseEntity containing a list of checked-in RSVPs for the user, sorted by date in ascending order.
   *         Returns an error message if the user does not exist.
   */
  @RequestMapping(value = "/rsvp/user/{userId}/checkedin", method = RequestMethod.GET)
  public ResponseEntity<?> getCheckedInRSVPsForUser(@PathVariable String userId) {
    Map<String, Object> response = new HashMap<>();
    try {
      List<RSVP> checkedInRSVPs = rsvpService.getCheckedInRSVPsByUser(userId);

      response.put("success", true);
      response.put("data", checkedInRSVPs);

      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (UserNotExistException error) {
      response.put("success", false);
      response.put("message", error.getMessage());
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    } catch (Exception error) {
      response.put("success", false);
      response.put("message", error.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
