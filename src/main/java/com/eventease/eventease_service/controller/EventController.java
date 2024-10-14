package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.EventRepository;
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

@RestController
public class EventController {
  private EventService eventService;
  private UserService userService;

  @Autowired
  public EventController(EventService eventService, UserService userService) {
    this.eventService = eventService;
    this.userService = userService;
  }

  // Create a new event with details such as name, time, date, location, organizer, capacity, and budget.
  @PostMapping(value = "/events")
  public ResponseEntity<?> addEvent( @RequestParam String name,
      @RequestParam String description,
      @RequestParam String location,
      @RequestParam String date,   // String to handle LocalDate parsing manually
      @RequestParam String time,   // String to handle LocalTime parsing manually
      @RequestParam int capacity,
      @RequestParam int budget,
      @RequestParam Long organizerId) {

    try {
      User organizer = userService.findUserById(organizerId);
      if (organizer == null) {
        return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
      }

      LocalDate eventDate = LocalDate.parse(date);  // Ensure date format is correct (e.g., "2024-10-10")
      LocalTime eventTime = LocalTime.parse(time);

      Event event = new Event.Builder()
          .setName(name)
          .setDescription(description)
          .setLocation(location)
          .setDate(eventDate)
          .setTime(eventTime)
          .setCapacity(capacity)
          .setBudget(budget)
          .setHost(organizer)  // Set the host (organizer)
          .build();

      eventService.add(event);

      Map<String, Object> response = new HashMap<>();
      response.put("organizerId", organizer.getId());
      response.put("eventId", event.getId());

      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Error creating event: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Retrieve details of a specific event by its ID.
  @GetMapping(value = "/events/{eventId}")
  public ResponseEntity<?> getEventById(@PathVariable Long eventId) {
    try {
      // Retrieve the event by ID using the service
      Event event = eventService.findById(eventId);

      // Return the event details if found
      return new ResponseEntity<>(event, HttpStatus.OK);
    } catch (Exception e) {
      // If the event is not found or any other exception occurs
      return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
    }
  }

  // Retrieve a list of events with optional filters (e.g., date range)
  @GetMapping(value = "/events")
  public ResponseEntity<List<Event>> getEvents(
      @RequestParam(value = "startDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(value = "endDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<Event> events = eventService.findByDateBetween(startDate, endDate);
    return new ResponseEntity<>(events, HttpStatus.OK);
  }

  // Delete a specific event.
  @DeleteMapping(value = "/events/{eventId}")
  public ResponseEntity<?> deleteEventById(@PathVariable Long eventId) {
    try {
      eventService.delete(eventId);

      return new ResponseEntity<>("Event deleted successfully.", HttpStatus.OK);
    } catch (EventNotExistException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

  // Update an existing event's details.
  @PatchMapping("/events/{eventId}")
  public ResponseEntity<String> updateEvent(
      @PathVariable Long eventId,
      @RequestBody Event updatedEvent) {

    try {
      eventService.updateEvent(eventId, updatedEvent);
      return new ResponseEntity<>("Event updated successfully", HttpStatus.OK);
    } catch (EventNotExistException e) {
      return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to update event", HttpStatus.BAD_REQUEST);
    }
  }


}
