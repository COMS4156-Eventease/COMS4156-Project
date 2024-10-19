package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.repository.EventRepository;
import com.eventease.eventease_service.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the EventService class.
 * These tests cover various scenarios to ensure the correct functioning of the EventService methods.
 */
public class EventServiceUnitTest {

  @Mock
  private EventRepository eventRepository;

  @InjectMocks
  private EventService eventService;

  private Event testEvent;

  /**
   * Set up method to initialize mocks and create a test event before each test.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testEvent = new Event();
    testEvent.setId(1L);
    testEvent.setName("Test Event");
    testEvent.setDescription("Test Description");
    testEvent.setLocation("Test Location");
    testEvent.setDate(LocalDate.now());
    testEvent.setTime(LocalTime.now());
    testEvent.setCapacity(100);
    testEvent.setBudget(1000);
  }

  /**
   * Test the add method of EventService.
   * Verifies that the save method of EventRepository is called with the correct event.
   */
  @Test
  void testAdd() {
    eventService.add(testEvent);
    verify(eventRepository, times(1)).save(testEvent);
  }

  /**
   * Test the findById method of EventService when the event exists.
   * Verifies that the correct event is returned.
   */
  @Test
  void testFindByIdWhenEventExists() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);
    Event result = eventService.findById(1L);
    assertEquals(testEvent, result);
  }

  /**
   * Test the findById method of EventService when the event does not exist.
   * Verifies that an EventNotExistException is thrown.
   */
  @Test
  void testFindByIdWhenEventDoesNotExist() {
    when(eventRepository.findById(1L)).thenReturn(null);
    assertThrows(EventNotExistException.class, () -> eventService.findById(1L));
  }

  /**
   * Test the findByDateBetween method of EventService.
   * Verifies that the correct list of events is returned.
   */
  @Test
  void testFindByDateBetween() {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusDays(7);
    List<Event> expectedEvents = Arrays.asList(testEvent);
    when(eventRepository.findEventsByDateRange(startDate, endDate)).thenReturn(expectedEvents);

    List<Event> result = eventService.findByDateBetween(startDate, endDate);
    assertEquals(expectedEvents, result);
  }

  /**
   * Test the updateEvent method of EventService when the event exists.
   * Verifies that the event is updated correctly with new values.
   */
  @Test
  void testUpdateEventWhenEventExists() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);

    Event updatedEvent = new Event();
    updatedEvent.setName("Updated Event");
    updatedEvent.setDescription("Updated Description");
    updatedEvent.setCapacity(200);
    updatedEvent.setBudget(2000);

    eventService.updateEvent(1L, updatedEvent);

    assertEquals("Updated Event", testEvent.getName());
    assertEquals("Updated Description", testEvent.getDescription());
    assertEquals(200, testEvent.getCapacity());
    assertEquals(2000.0, testEvent.getBudget());
    verify(eventRepository, times(1)).save(testEvent);
  }

  /**
   * Test the updateEvent method of EventService when the event does not exist.
   * Verifies that an EventNotExistException is thrown.
   */
  @Test
  void testUpdateEventWhenEventDoesNotExist() {
    when(eventRepository.findById(1L)).thenReturn(null);
    assertThrows(EventNotExistException.class, () -> eventService.updateEvent(1L, new Event()));
  }

  /**
   * Test the updateEvent method of EventService when only some fields are updated.
   * Verifies that only the provided fields are updated while others remain unchanged.
   */
  @Test
  void testUpdateEventPartialUpdate() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);

    Event updatedEvent = new Event();
    updatedEvent.setName("Updated Event");
    updatedEvent.setCapacity(200);

    eventService.updateEvent(1L, updatedEvent);

    assertEquals("Updated Event", testEvent.getName());
    assertEquals("Test Description", testEvent.getDescription());
    assertEquals(200, testEvent.getCapacity());
    assertEquals(1000.0, testEvent.getBudget());
    verify(eventRepository, times(1)).save(testEvent);
  }

  /**
   * Test the delete method of EventService when the event exists.
   * Verifies that the deleteById method of EventRepository is called with the correct ID.
   */
  @Test
  void testDeleteWhenEventExists() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);
    eventService.delete(1L);
    verify(eventRepository, times(1)).deleteById(1L);
  }

  /**
   * Test the delete method of EventService when the event does not exist.
   * Verifies that an EventNotExistException is thrown.
   */
  @Test
  void testDeleteWhenEventDoesNotExist() {
    when(eventRepository.findById(1L)).thenReturn(null);
    assertThrows(EventNotExistException.class, () -> eventService.delete(1L));
  }
}