package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.EventImage;
import com.eventease.eventease_service.repository.EventRepository;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.ImageStorageService;

import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the EventService class.
 * These tests cover various scenarios to ensure the correct functioning of the EventService methods.
 */
@ActiveProfiles("test")
public class EventServiceUnitTest {

  @Mock
  private EventRepository eventRepository;

  @Mock
  private ImageStorageService imageStorageService;

  @InjectMocks
  private EventService eventService;

  private Event testEvent;
  private MockMultipartFile testImage;

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

    testImage = new MockMultipartFile("images", "test-image.jpg", "image/jpeg", "Test Image Content".getBytes());
  }

  /**
   * Test the add method of EventService with images.
   * Verifies that the event is saved and images are stored correctly.
   */
  @Test
  void testAddEventWithImages() {
    when(imageStorageService.save(testImage)).thenReturn("http://image-url.com/test-image.jpg");

    eventService.add(testEvent, new MultipartFile[]{testImage});

    assertEquals(1, testEvent.getImages().size());
    assertEquals("http://image-url.com/test-image.jpg", testEvent.getImages().get(0).getUrl());
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
   * Test the updateEvent method of EventService with new data and images.
   * Verifies that the event is updated correctly and old images are cleared.
   */
  @Test
  void testUpdateEventWithImages() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);
    when(imageStorageService.save(testImage)).thenReturn("http://image-url.com/updated-image.jpg");

    Event updatedEvent = new Event();
    updatedEvent.setName("Updated Event");
    updatedEvent.setDescription("Updated Description");
    updatedEvent.setCapacity(200);
    updatedEvent.setBudget(2000);

    // Use a mutable list for existing images
    List<EventImage> existingImages = new ArrayList<>(
        List.of(new EventImage("http://image-url.com/old-image.jpg", testEvent))
    );
    testEvent.setImages(existingImages);

    eventService.updateEvent(1L, updatedEvent, new MultipartFile[]{testImage});

    // Verify event fields are updated
    assertEquals("Updated Event", testEvent.getName());
    assertEquals("Updated Description", testEvent.getDescription());
    assertEquals(200, testEvent.getCapacity());
    assertEquals(2000, testEvent.getBudget());

    // Verify old images are cleared and new images are added
    assertEquals(1, testEvent.getImages().size());
    assertEquals("http://image-url.com/updated-image.jpg", testEvent.getImages().get(0).getUrl());

    verify(eventRepository, times(1)).save(testEvent);
  }


  /**
   * Test the updateEvent method of EventService when the event does not exist.
   * Verifies that an EventNotExistException is thrown.
   */
  @Test
  void testUpdateEventWhenEventDoesNotExist() {
    when(eventRepository.findById(1L)).thenReturn(null);
    assertThrows(EventNotExistException.class, () -> eventService.updateEvent(1L, new Event(), new MultipartFile[]{}));
  }

  /**
   * Test the updateEvent method of EventService when only some fields are updated.
   * Verifies that only the provided fields are updated while others remain unchanged.
   */
  @Test
  void testUpdateEventPartialUpdate() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);

    // Initialize the images field to an empty list to avoid NullPointerException
    testEvent.setImages(new ArrayList<>());

    Event updatedEvent = new Event();
    updatedEvent.setName("Updated Event");
    updatedEvent.setCapacity(200);

    eventService.updateEvent(1L, updatedEvent, null);

    // Verify only updated fields change
    assertEquals("Updated Event", testEvent.getName());
    assertEquals("Test Description", testEvent.getDescription());
    assertEquals(200, testEvent.getCapacity());
    assertEquals(1000, testEvent.getBudget());

    // Verify images remain unchanged
    assertTrue(testEvent.getImages().isEmpty());

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

  /**
   * Test the findAllEvents method of EventService.
   * Verifies that all events are returned correctly.
   */
  @Test
  void testFindAllEvents() {
    List<Event> events = Arrays.asList(testEvent);
    when(eventRepository.findAll()).thenReturn(events);

    List<Event> result = eventService.findAllEvents();

    assertEquals(events, result);
    verify(eventRepository, times(1)).findAll();
  }

  @Test
  void testAddEventWithoutImages() {
    eventService.add(testEvent, new MultipartFile[]{});

    // Verify no images are added
    assertTrue(testEvent.getImages().isEmpty());
    verify(eventRepository, times(1)).save(testEvent);
  }

  @Test
  void testFindByDateBetweenWithNoResults() {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusDays(7);

    when(eventRepository.findEventsByDateRange(startDate, endDate)).thenReturn(Collections.emptyList());

    List<Event> result = eventService.findByDateBetween(startDate, endDate);

    assertTrue(result.isEmpty());
    verify(eventRepository, times(1)).findEventsByDateRange(startDate, endDate);
  }
  @Test
  void testUpdateEventNoUpdatesProvided() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);

    eventService.updateEvent(1L, new Event(), null);

    // Verify no fields are updated
    assertEquals("Test Event", testEvent.getName());
    assertEquals(100, testEvent.getCapacity());

    verify(eventRepository, times(1)).save(testEvent);
  }

  @Test
  @Transactional(isolation = Isolation.SERIALIZABLE)
  void testDeleteConcurrentModification() {
    when(eventRepository.findById(1L)).thenReturn(testEvent);

    // Simulate concurrent deletion
    doThrow(new RuntimeException("Concurrent modification error")).when(eventRepository).deleteById(1L);

    assertThrows(RuntimeException.class, () -> eventService.delete(1L));
    verify(eventRepository, times(1)).findById(1L);
    verify(eventRepository, times(1)).deleteById(1L);
  }

  @Test
  void testUpdateEventWithLocationDateTime() {
    // Mock the existing event in the repository
    when(eventRepository.findById(1L)).thenReturn(testEvent);

    Event updatedEvent = new Event();
    updatedEvent.setLocation("New Location");
    updatedEvent.setDate(LocalDate.of(2024, 12, 25)); // New date
    updatedEvent.setTime(LocalTime.of(15, 30));       // New time

    eventService.updateEvent(1L, updatedEvent, null);

    // Verify that the fields were updated
    assertEquals("New Location", testEvent.getLocation());
    assertEquals(LocalDate.of(2024, 12, 25), testEvent.getDate());
    assertEquals(LocalTime.of(15, 30), testEvent.getTime());

    // Verify that save was called with the updated event
    verify(eventRepository, times(1)).save(testEvent);
  }

}