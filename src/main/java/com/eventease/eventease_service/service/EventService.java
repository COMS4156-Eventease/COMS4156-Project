package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.repository.EventRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

@Service
public class EventService {

  private final EventRepository eventRepository;

  // @Autowired is used to inject dependencies automatically by Spring
  @Autowired
  public EventService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  // Saves a new event to the database
  public void add(Event event) {
    eventRepository.save(event);
  }

  // Finds an event by its ID and throws an exception if it doesn't exist
  public Event findById(long id) {
    Event event = eventRepository.findById(id);
    if (event == null) {
      throw new EventNotExistException("Event not found");
    }
    return event;
  }

  // @Transactional with readOnly = true marks this method as transactional, optimized for read operations
  @Transactional(readOnly = true)
  public List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate) {
    List<Event> events = eventRepository.findEventsByDateRange(startDate, endDate);
    return events;
  }

  // Updates an existing event using the Builder pattern to ensure immutability
  public void updateEvent(long id, Event updatedEvent) {
    Event existingEvent = eventRepository.findById(id);
    if (existingEvent == null) {
      throw new EventNotExistException("Event not found");
    }

    // Update fields of the existing event in place
    existingEvent.setName(updatedEvent.getName() != null ? updatedEvent.getName() : existingEvent.getName());
    existingEvent.setDescription(updatedEvent.getDescription() != null ? updatedEvent.getDescription() : existingEvent.getDescription());
    existingEvent.setLocation(updatedEvent.getLocation() != null ? updatedEvent.getLocation() : existingEvent.getLocation());
    existingEvent.setDate(updatedEvent.getDate() != null ? updatedEvent.getDate() : existingEvent.getDate());
    existingEvent.setTime(updatedEvent.getTime() != null ? updatedEvent.getTime() : existingEvent.getTime());
    existingEvent.setCapacity(updatedEvent.getCapacity() > 0 ? updatedEvent.getCapacity() : existingEvent.getCapacity());
    existingEvent.setBudget(updatedEvent.getBudget() > 0 ? updatedEvent.getBudget() : existingEvent.getBudget());
    // Retain the original host and participants
    existingEvent.setHost(existingEvent.getHost());
    existingEvent.setParticipants(existingEvent.getParticipants());

    // Save the updated event back to the repository
    eventRepository.save(existingEvent);
  }

  // @Transactional with Isolation.SERIALIZABLE ensures the highest level of isolation
  // to avoid concurrency issues during the delete operation
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void delete(long id) {
    Event event = eventRepository.findById(id);
    if (event == null) {
      throw new EventNotExistException("Event doesn't exist");
    }

    // Delete the event by its ID
    eventRepository.deleteById(id);
  }
}

