package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.GCSUploadException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/events")
public class EventController {
  private EventService eventService;
  private UserService userService;

  @Autowired
  public EventController(EventService eventService, UserService userService) {
    this.eventService = eventService;
    this.userService = userService;
  }

  // Create a new event with details such as name, time, date, location, organizer, capacity, and budget.
  @PostMapping
  public ResponseEntity<?> addEvent(
      @RequestParam("organizerId") Long organizerId,
      @RequestParam("name") String name,
      @RequestParam("time") String time,
      @RequestParam("date") String date,
      @RequestParam("location") String location,
      @RequestParam("description") String description,
      @RequestParam("capacity") String capacity,
      @RequestParam("budget") String budget,
      @RequestParam("images") MultipartFile[] images
  ) {
    try {
      User organizer = userService.findUserById(organizerId);
      if (organizer == null) {
        return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
      }
      Event event = new Event.Builder().setName(name)
          .setTime(LocalTime.parse(time))
          .setDate(LocalDate.parse(date))
          .setLocation(location)
          .setDescription(description)
          .setCapacity(Integer.parseInt(capacity))
          .setBudget(Integer.parseInt(budget))
          .setHost(organizer)
          .build();

      eventService.add(event, images);

      Map<String, Object> response = new HashMap<>();
      response.put("organizerId", organizer.getId());
      response.put("eventId", event.getId());

      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (UserNotExistException e) {
      return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
    } catch (GCSUploadException e) {
      return new ResponseEntity<>("GCP error", HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      return new ResponseEntity<>("Error creating event: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Retrieve details of a specific event by its ID.
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
  @GetMapping
  public ResponseEntity<List<Event>> getEvents(
      @RequestParam(value = "startDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(value = "endDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<Event> events = eventService.findByDateBetween(startDate, endDate);
    return new ResponseEntity<>(events, HttpStatus.OK);
  }

  // Delete a specific event.
  @DeleteMapping(value = "/{eventId}")
  public ResponseEntity<?> deleteEventById(@PathVariable Long eventId) {
    try {
      eventService.delete(eventId);

      return new ResponseEntity<>("Event deleted successfully.", HttpStatus.OK);
    } catch (EventNotExistException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

  // Update an existing event's details.
  @PatchMapping("{eventId}")
  public ResponseEntity<String> updateEvent(
      @PathVariable Long eventId,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "time", required = false) String time,
      @RequestParam(value = "date", required = false) String date,
      @RequestParam(value = "location", required = false) String location,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "capacity", required = false) String capacity,
      @RequestParam(value = "budget", required = false) String budget,
      @RequestParam(value = "images", required = false) MultipartFile[] images
  ) {
    try {

      Event existingEvent = eventService.findById(eventId);
      if (existingEvent == null) {
        return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
      }

      // Create a new Event object to hold updates
      Event updatedEvent = new Event.Builder()
          .setName(name != null ? name : existingEvent.getName())
          .setTime(time != null ? LocalTime.parse(time) : existingEvent.getTime())
          .setDate(date != null ? LocalDate.parse(date) : existingEvent.getDate())
          .setLocation(location != null ? location : existingEvent.getLocation())
          .setDescription(description != null ? description : existingEvent.getDescription())
          .setCapacity(capacity != null ? Integer.parseInt(capacity) : existingEvent.getCapacity())
          .setBudget(budget != null ? Integer.parseInt(budget) : existingEvent.getBudget())
          .setHost(existingEvent.getHost()) // Retain original host
          .setParticipants(existingEvent.getParticipants()) // Retain original participants
          .build();

      // Pass updatedEvent and images to the eventService for saving
      eventService.updateEvent(eventId, updatedEvent, images);

      return new ResponseEntity<>("Event updated successfully", HttpStatus.OK);
    } catch (EventNotExistException e) {
      return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to update event: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
