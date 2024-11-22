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
import java.util.ArrayList;
import java.util.Collections;
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
  private final EventService eventService;
  private final UserService userService;

  @Autowired
  public EventController(EventService eventService, UserService userService) {
    this.eventService = eventService;
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> addEvent(
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
    Map<String, Object> response = new HashMap<>();
    try {
      User organizer = userService.findUserById(organizerId);
      if (organizer == null) {
        response.put("success", false);
        response.put("data", Collections.emptyList());
        response.put("message", "Organizer not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      Event event = new Event.Builder()
          .setName(name)
          .setTime(LocalTime.parse(time))
          .setDate(LocalDate.parse(date))
          .setLocation(location)
          .setDescription(description)
          .setCapacity(Integer.parseInt(capacity))
          .setBudget(Integer.parseInt(budget))
          .setHost(organizer)
          .build();

      eventService.add(event, images);

      response.put("success", true);
      response.put("data", List.of(Map.of("organizerId", organizer.getId(), "eventId", event.getId())));
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (UserNotExistException e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "Organizer not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (GCSUploadException e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "GCP error");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "Error creating event: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping(value = "/{eventId}")
  public ResponseEntity<Map<String, Object>> getEventById(@PathVariable Long eventId) {
    Map<String, Object> response = new HashMap<>();
    try {
      Event event = eventService.findById(eventId);
      response.put("success", true);
      response.put("data", List.of(event));
      return ResponseEntity.ok(response);
    } catch (EventNotExistException e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "Event not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "An unexpected error occurred");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getEvents(
      @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {
    Map<String, Object> response = new HashMap<>();
    try {
      List<Event> events = eventService.findByDateBetween(startDate, endDate);
      response.put("success", true);
      response.put("data", events);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "Error retrieving events: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping("/all")
  public ResponseEntity<Map<String, Object>> getAllEvents() {
    Map<String, Object> response = new HashMap<>();
    try {
      // Retrieve all events using the eventService
      List<Event> events = eventService.findAllEvents();

      // Construct the success response
      response.put("success", true);
      response.put("data", events);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      // Construct the failure response
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "Failed to fetch events: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping(value = "/{eventId}")
  public ResponseEntity<Map<String, Object>> deleteEventById(@PathVariable Long eventId) {
    Map<String, Object> response = new HashMap<>();
    try {
      eventService.delete(eventId);
      response.put("success", true);
      response.put("data", Collections.emptyList());
      return ResponseEntity.ok(response);
    } catch (EventNotExistException e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

  @PatchMapping("{eventId}")
  public ResponseEntity<Map<String, Object>> updateEvent(
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
    Map<String, Object> response = new HashMap<>();
    try {
      Event existingEvent = eventService.findById(eventId);

      Event updatedEvent = new Event.Builder()
          .setName(name != null ? name : existingEvent.getName())
          .setTime(time != null ? LocalTime.parse(time) : existingEvent.getTime())
          .setDate(date != null ? LocalDate.parse(date) : existingEvent.getDate())
          .setLocation(location != null ? location : existingEvent.getLocation())
          .setDescription(description != null ? description : existingEvent.getDescription())
          .setCapacity(capacity != null ? Integer.parseInt(capacity) : existingEvent.getCapacity())
          .setBudget(budget != null ? Integer.parseInt(budget) : existingEvent.getBudget())
          .setHost(existingEvent.getHost())
          .setParticipants(existingEvent.getParticipants())
          .build();

      eventService.updateEvent(eventId, updatedEvent, images);

      response.put("success", true);
      response.put("data", Collections.emptyList()); // Always an empty array
      return ResponseEntity.ok(response);
    } catch (EventNotExistException e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "Event not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("data", Collections.emptyList());
      response.put("message", "Failed to update event: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }
}

