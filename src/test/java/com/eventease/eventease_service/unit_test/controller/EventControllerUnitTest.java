package com.eventease.eventease_service.unit_test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eventease.eventease_service.controller.EventController;
import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.GCSUploadException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.EventRepository;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.UserService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(EventController.class)
public class EventControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EventService eventService;

  @MockBean
  private EventRepository eventRepository;

  @MockBean
  private UserService userService;


  @Test
  public void addEventSuccessTest() throws Exception {
    User organizer = new User(); // Create a dummy user
    organizer.setId(1L);

    Event event = new Event();
    event.setId(123L); // Set the expected event ID
    event.setName("Event Title");

    when(userService.findUserById(1L)).thenReturn(organizer);

    doAnswer(invocation -> {
      Event e = invocation.getArgument(0);
      e.setId(123L); // Set event ID to simulate the generated ID after saving
      return null;
    }).when(eventService).add(any(Event.class), any(MultipartFile[].class));

    MockMultipartFile image = new MockMultipartFile("images", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());

    mockMvc.perform(multipart("/api/events")
            .file(image)
            .param("organizerId", "1")
            .param("name", "Event Title")
            .param("description", "Event description")
            .param("location", "123 Venue St.")
            .param("date", "2024-11-15")
            .param("time", "10:30")
            .param("capacity", "100")
            .param("budget", "1200")
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].organizerId").value(1))
        .andExpect(jsonPath("$.data[0].eventId").value(123));
  }

  @Test
  public void addEventOrganizerNotFoundTest() throws Exception {
    // Mocking userService to return null for organizer
    when(userService.findUserById(1L)).thenReturn(null);

    MockMultipartFile image = new MockMultipartFile("images", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());

    mockMvc.perform(multipart("/api/events")
            .file(image)
            .param("organizerId", "1")
            .param("name", "Event Title")
            .param("description", "Event description")
            .param("location", "123 Venue St.")
            .param("date", "2024-11-15")
            .param("time", "10:30")
            .param("capacity", "100")
            .param("budget", "1200")
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Organizer not found"));
  }

  @Test
  public void addEventFailTest() throws Exception {
    when(userService.findUserById(1L)).thenThrow(new UserNotExistException("User is not found"));

    MockMultipartFile image = new MockMultipartFile("images", "dummy-image.jpg", MediaType.IMAGE_JPEG_VALUE, "Dummy Image Content".getBytes());

    mockMvc.perform(multipart("/api/events")
            .file(image)
            .param("organizerId", "1")
            .param("name", "Event Title")
            .param("description", "Event description")
            .param("location", "123 Venue St.")
            .param("date", "2024-11-15")
            .param("time", "10:30")
            .param("capacity", "100")
            .param("budget", "1200")
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Organizer not found"));
  }

  @Test
  public void getEventByIdSuccessTest() throws Exception {
    Event event = new Event();
    event.setId(123L);
    event.setName("Event Title");

    when(eventService.findById(123L)).thenReturn(event);

    mockMvc.perform(get("/api/events/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].id").value(123))
        .andExpect(jsonPath("$.data[0].name").value("Event Title"));
  }
  @Test
  public void getEventsUnexpectedErrorTest() throws Exception {
    // Simulate an unexpected exception when findByDateBetween is called
    when(eventService.findByDateBetween(any(LocalDate.class), any(LocalDate.class)))
        .thenThrow(new RuntimeException("Unexpected error occurred"));

    mockMvc.perform(get("/api/events")
            .param("startDate", "2024-11-01")
            .param("endDate", "2024-11-30")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Error retrieving events: Unexpected error occurred"));
  }


  @Test
  public void addEventGCSUploadExceptionTest() throws Exception {
    User organizer = new User();
    organizer.setId(1L);

    when(userService.findUserById(1L)).thenReturn(organizer);

    // Mocking eventService to throw GCSUploadException
    doThrow(new GCSUploadException("Failed to upload to GCP")).when(eventService).add(any(Event.class), any(MultipartFile[].class));

    MockMultipartFile image = new MockMultipartFile("images", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());

    mockMvc.perform(multipart("/api/events")
            .file(image)
            .param("organizerId", "1")
            .param("name", "Event Title")
            .param("description", "Event description")
            .param("location", "123 Venue St.")
            .param("date", "2024-11-15")
            .param("time", "10:30")
            .param("capacity", "100")
            .param("budget", "1200")
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("GCP error"));
  }

  @Test
  public void addEventGeneralExceptionTest() throws Exception {
    User organizer = new User();
    organizer.setId(1L);

    when(userService.findUserById(1L)).thenReturn(organizer);

    // Mocking eventService to throw a general exception
    doThrow(new RuntimeException("Unexpected error")).when(eventService).add(any(Event.class), any(MultipartFile[].class));

    MockMultipartFile image = new MockMultipartFile("images", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());

    mockMvc.perform(multipart("/api/events")
            .file(image)
            .param("organizerId", "1")
            .param("name", "Event Title")
            .param("description", "Event description")
            .param("location", "123 Venue St.")
            .param("date", "2024-11-15")
            .param("time", "10:30")
            .param("capacity", "100")
            .param("budget", "1200")
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Error creating event: Unexpected error"));
  }


  @Test
  public void getEventByIdFailTest() throws Exception {
    when(eventService.findById(123L)).thenThrow(new EventNotExistException("Event not found"));

    mockMvc.perform(get("/api/events/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Event not found"));
  }

  @Test
  public void getEventByIdGeneralExceptionTest() throws Exception {
    // Mocking eventService to throw a general exception
    when(eventService.findById(123L)).thenThrow(new RuntimeException("Unexpected error"));

    mockMvc.perform(get("/api/events/123"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
  }


  @Test
  public void getEventsSuccessTest() throws Exception {
    Event event1 = new Event();
    event1.setId(1L);
    event1.setName("Event 1");
    event1.setDate(LocalDate.of(2024, 11, 15));

    Event event2 = new Event();
    event2.setId(2L);
    event2.setName("Event 2");
    event2.setDate(LocalDate.of(2024, 11, 20));

    List<Event> events = Arrays.asList(event1, event2);

    when(eventService.findByDateBetween(LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30))).thenReturn(events);

    mockMvc.perform(get("/api/events?startDate=2024-11-01&endDate=2024-11-30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[1].id").value(2));
  }


  // Test for failing to retrieve events within a date range
  @Test
  public void getEventsFailTest() throws Exception {
    when(eventService.findByDateBetween(LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30))).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/events?startDate=2024-11-01&endDate=2024-11-30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  public void getAllEventsSuccessTest() throws Exception {
    // Mocking the eventService to return a list of events
    Event event1 = new Event();
    event1.setId(1L);
    event1.setName("Event 1");

    Event event2 = new Event();
    event2.setId(2L);
    event2.setName("Event 2");

    List<Event> events = Arrays.asList(event1, event2);
    when(eventService.findAllEvents()).thenReturn(events);

    mockMvc.perform(get("/api/events/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].name").value("Event 1"))
        .andExpect(jsonPath("$.data[1].id").value(2))
        .andExpect(jsonPath("$.data[1].name").value("Event 2"));
  }

  @Test
  public void getAllEventsFailureTest() throws Exception {
    // Mocking the eventService to throw an exception
    when(eventService.findAllEvents()).thenThrow(new RuntimeException("Unexpected error"));

    mockMvc.perform(get("/api/events/all"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Failed to fetch events: Unexpected error"));
  }

  @Test
  public void deleteEventByIdSuccessTest() throws Exception {
    // Mocking the eventService to perform successful deletion
    doNothing().when(eventService).delete(123L);

    mockMvc.perform(delete("/api/events/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  public void deleteEventByIdFailureTest() throws Exception {
    // Mocking the eventService to throw EventNotExistException
    doThrow(new EventNotExistException("Event not found")).when(eventService).delete(123L);

    mockMvc.perform(delete("/api/events/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Event not found"));
  }

  @Test
  public void updateEventAllParametersProvidedTest() throws Exception {
    // Mocking existing event
    Event existingEvent = new Event();
    existingEvent.setId(123L);
    existingEvent.setName("Old Name");
    existingEvent.setTime(LocalTime.of(10, 0));
    existingEvent.setDate(LocalDate.of(2024, 12, 10));
    existingEvent.setLocation("Old Location");
    existingEvent.setDescription("Old Description");
    existingEvent.setCapacity(50);
    existingEvent.setBudget(500);

    when(eventService.findById(123L)).thenReturn(existingEvent);

    // Ensure the updateEvent method allows the operation
    doNothing().when(eventService).updateEvent(eq(123L), any(Event.class), any(MultipartFile[].class));

    // Perform the patch request
    mockMvc.perform(multipart("/api/events/123")
            .param("name", "New Name")
            .param("time", "12:30")
            .param("date", "2024-12-15")
            .param("location", "New Location")
            .param("description", "New Description")
            .param("capacity", "100")
            .param("budget", "1000")
            .with(request -> { request.setMethod("PATCH"); return request; }) // Set the method to PATCH
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    // Capture the arguments passed to updateEvent
    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    ArgumentCaptor<MultipartFile[]> fileCaptor = ArgumentCaptor.forClass(MultipartFile[].class);
    verify(eventService).updateEvent(eq(123L), eventCaptor.capture(), fileCaptor.capture());

    // Assert the Event properties
    Event updatedEvent = eventCaptor.getValue();
    assertEquals("New Name", updatedEvent.getName());
    assertEquals(LocalTime.of(12, 30), updatedEvent.getTime());
    assertEquals(LocalDate.of(2024, 12, 15), updatedEvent.getDate());
    assertEquals("New Location", updatedEvent.getLocation());
    assertEquals("New Description", updatedEvent.getDescription());
    assertEquals(100, updatedEvent.getCapacity());
    assertEquals(1000, updatedEvent.getBudget());

    // Assert the files are correctly handled (null or empty array)
    assertNull(fileCaptor.getValue());
  }







  // Test for failing to update an event (event not found)
  @Test
  public void updateEventFailTest() throws Exception {
    when(eventService.findById(123L)).thenThrow(new EventNotExistException("Event not found"));

    mockMvc.perform(patch("/api/events/123")
            .param("name", "Updated Event")
            .param("capacity", "200")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Event not found"));
  }

  @Test
  public void updateEventExceptionTest() throws Exception {
    // Mocking `eventService.findById` to return a valid event
    Event existingEvent = new Event();
    existingEvent.setId(123L);
    existingEvent.setName("Existing Event");
    when(eventService.findById(123L)).thenReturn(existingEvent);

    // Mocking `eventService.updateEvent` to throw a generic exception
    doThrow(new RuntimeException("Unexpected error occurred")).when(eventService).updateEvent(eq(123L), any(Event.class), any(MultipartFile[].class));

    MockMultipartFile image = new MockMultipartFile("images", "updated-image.jpg", MediaType.IMAGE_JPEG_VALUE, "Updated Image Content".getBytes());

    mockMvc.perform(multipart("/api/events/123")
            .file(image)
            .param("name", "Updated Event")
            .param("capacity", "200")
            .with(request -> { request.setMethod("PATCH"); return request; }) // Set method to PATCH
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest()) // Expecting BAD_REQUEST status
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.message").value("Failed to update event: Unexpected error occurred"));
  }

  @Test
  public void updateEventPartialUpdateTest() throws Exception {
    // Mocking existing event
    Event existingEvent = new Event();
    existingEvent.setId(123L);
    existingEvent.setName("Old Name");
    existingEvent.setTime(LocalTime.of(10, 0));
    existingEvent.setDate(LocalDate.of(2024, 12, 10));
    existingEvent.setLocation("Old Location");
    existingEvent.setDescription("Old Description");
    existingEvent.setCapacity(50);
    existingEvent.setBudget(500);

    when(eventService.findById(123L)).thenReturn(existingEvent);

    // Ensure updateEvent does not throw exceptions
    doNothing().when(eventService).updateEvent(eq(123L), any(Event.class), nullable(MultipartFile[].class));

    // Perform the patch request with only the name being updated
    mockMvc.perform(multipart("/api/events/123")
            .param("name", "New Name")
            .with(request -> {
              request.setMethod("PATCH"); // Explicitly set the method to PATCH
              return request;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    // Capture the argument passed to updateEvent
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
    verify(eventService).updateEvent(eq(123L), captor.capture(), nullable(MultipartFile[].class));

    Event updatedEvent = captor.getValue();
    // Assert that the name is updated, while the other fields remain unchanged
    assertEquals("New Name", updatedEvent.getName());
    assertEquals(LocalTime.of(10, 0), updatedEvent.getTime()); // Should remain unchanged
    assertEquals(LocalDate.of(2024, 12, 10), updatedEvent.getDate()); // Should remain unchanged
    assertEquals("Old Location", updatedEvent.getLocation()); // Should remain unchanged
    assertEquals("Old Description", updatedEvent.getDescription()); // Should remain unchanged
    assertEquals(50, updatedEvent.getCapacity()); // Should remain unchanged
    assertEquals(500, updatedEvent.getBudget()); // Should remain unchanged
  }
}
