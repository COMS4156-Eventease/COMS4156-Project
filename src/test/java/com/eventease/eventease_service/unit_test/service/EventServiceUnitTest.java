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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceUnitTest {

  @InjectMocks
  private EventService eventService;

  @Mock
  private EventRepository eventRepository;

  private Event mockEvent;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    // Initialize a mock event for testing
    mockEvent = new Event.Builder()
        .setId(1L)
        .setName("Test Event")
        .setDescription("Test Description")
        .setLocation("Test Location")
        .setDate(LocalDate.of(2024, 11, 10))
        .setTime(LocalDate.of(2024, 11, 10).atTime(10, 30).toLocalTime())
        .setCapacity(100)
        .setBudget(1200)
        .build();
  }

  @Test
  public void testAddEvent() {
    // Act
    eventService.add(mockEvent);

    // Verify that the save method is called in the repository
    verify(eventRepository, times(1)).save(mockEvent);
  }

  @Test
  public void testFindById_Success() {
    // Arrange
    when(eventRepository.findById(1L)).thenReturn(mockEvent);

    // Act
    Event event = eventService.findById(1L);

    // Assert
    assertNotNull(event);
    assertEquals(mockEvent.getId(), event.getId());
    verify(eventRepository, times(1)).findById(1L);
  }

  @Test
  public void testFindById_Fail() {
    // Arrange
    when(eventRepository.findById(999L)).thenReturn(null);

    // Act & Assert
    assertThrows(EventNotExistException.class, () -> {
      eventService.findById(999L);
    });

    verify(eventRepository, times(1)).findById(999L);
  }

  @Test
  public void testFindByDateBetween_Success() {
    // Arrange
    when(eventRepository.findEventsByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Collections.singletonList(mockEvent));

    // Act
    List<Event> events = eventService.findByDateBetween(LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30));

    // Assert
    assertNotNull(events);
    assertFalse(events.isEmpty());
    assertEquals(1, events.size());
    verify(eventRepository, times(1)).findEventsByDateRange(any(LocalDate.class), any(LocalDate.class));
  }

  @Test
  public void testFindByDateBetween_NoEvents() {
    // Arrange
    when(eventRepository.findEventsByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Collections.emptyList());

    // Act
    List<Event> events = eventService.findByDateBetween(LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30));

    // Assert
    assertNotNull(events);
    assertTrue(events.isEmpty());
    verify(eventRepository, times(1)).findEventsByDateRange(any(LocalDate.class), any(LocalDate.class));
  }

  @Test
  public void testUpdateEvent_Success() {
    // Arrange
    when(eventRepository.findById(1L)).thenReturn(mockEvent);

    Event updatedEvent = new Event.Builder()
        .setName("Updated Event Name")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setCapacity(150)
        .setBudget(1500)
        .build();

    // Act
    eventService.updateEvent(1L, updatedEvent);

    // Assert
    assertEquals("Updated Event Name", mockEvent.getName());
    assertEquals("Updated Description", mockEvent.getDescription());
    verify(eventRepository, times(1)).findById(1L);
    verify(eventRepository, times(1)).save(mockEvent);
  }

  @Test
  public void testUpdateEvent_NotFound() {
    // Arrange
    when(eventRepository.findById(999L)).thenReturn(null);

    // Act & Assert
    assertThrows(EventNotExistException.class, () -> {
      eventService.updateEvent(999L, mockEvent);
    });

    verify(eventRepository, times(1)).findById(999L);
  }

  @Test
  public void testDeleteEvent_Success() {
    // Arrange
    when(eventRepository.findById(1L)).thenReturn(mockEvent);

    // Act
    eventService.delete(1L);

    // Assert
    verify(eventRepository, times(1)).findById(1L);
    verify(eventRepository, times(1)).deleteById(1L);
  }

  @Test
  public void testDeleteEvent_NotFound() {
    // Arrange
    when(eventRepository.findById(999L)).thenReturn(null);

    // Act & Assert
    assertThrows(EventNotExistException.class, () -> {
      eventService.delete(999L);
    });

    verify(eventRepository, times(1)).findById(999L);
  }
}