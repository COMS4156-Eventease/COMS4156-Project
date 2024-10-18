package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.UserService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling HTTP requests related to events.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
  private EventService eventService;
  private UserService userService;

  /**
   * Constructor for the EventController class.
   * @param eventService The service class for handling event-related operations.
   * @param userService The service class for handling user-related operations.
   */
  @Autowired
  public EventController(EventService eventService, UserService userService) {
    this.eventService = eventService;
    this.userService = userService;
  }

  /**
   * Add a new event to the system.
   * @param organizerId The ID of the user who is the organizer of the event.
   * @param event The event object containing details of the event.
   * @return
   */
  @PostMapping
  public ResponseEntity<?> addEvent(
      @RequestParam Long organizerId,
      @RequestBody Event event
  ) {
    try {
      User organizer = userService.findUserById(organizerId);
      if (organizer == null) {
        return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
      }

      event.setHost(organizer);

      eventService.add(event);

      Map<String, Object> response = new HashMap<>();
      response.put("organizerId", organizer.getId());
      response.put("eventId", event.getId());

      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (UserNotExistException e) {
      return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("Error creating event: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retrieve details of a specific event by its ID.
   * @param eventId The ID of the event to retrieve.
   * @return The event details if found, or an error message if not found.
   */
  @GetMapping(value = "/{eventId}")
  public ResponseEntity<?> getEventById(@PathVariable Long eventId) {
    try {
      // Retrieve the event by ID using the service
      Event event = eventService.findById(eventId);
//      if (event == null) {
//        return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
//      }
      // Return the event details if found
      return new ResponseEntity<>(event, HttpStatus.OK);
    } catch (EventNotExistException e) {
      // If the event is not found
      return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      // Handle any other unexpected exceptions
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Retrieve a list of events with optional filters (e.g., date range)

  /**
   * Retrieve a list of events within a specified date range.
   * @param startDate The start date of the date range.
   * @param endDate The end date of the date range.
   * @return A list of events within the specified date range.
   */
  @GetMapping
  public ResponseEntity<List<Event>> getEvents(
      @RequestParam(value = "startDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(value = "endDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<Event> events = eventService.findByDateBetween(startDate, endDate);
    return new ResponseEntity<>(events, HttpStatus.OK);
  }

  /**
   * Delete an event by its ID.
   * @param eventId The ID of the event to delete.
   * @return A response indicating the success or failure of the delete operation.
   */
  @DeleteMapping(value = "/{eventId}")
  public ResponseEntity<?> deleteEventById(@PathVariable Long eventId) {
    try {
      eventService.delete(eventId);

      return new ResponseEntity<>("Event deleted successfully.", HttpStatus.OK);
    } catch (EventNotExistException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Update an existing event's details.
   * @param eventId The ID of the event to update.
   * @param updatedEvent The updated event object with new details.
   * @return A response indicating the success or failure of the update operation.
   */
  @PatchMapping("{eventId}")
  public ResponseEntity<String> updateEvent(
      @PathVariable Long eventId,
      @RequestBody Event updatedEvent) {

    try {
      Event existingEvent = eventService.findById(eventId);
//      if (existingEvent == null) {
//        return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
//      }

      eventService.updateEvent(eventId, updatedEvent);
      return new ResponseEntity<>("Event updated successfully", HttpStatus.OK);
    } catch (EventNotExistException e) {
      return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to update event", HttpStatus.BAD_REQUEST);
    }
  }


}
