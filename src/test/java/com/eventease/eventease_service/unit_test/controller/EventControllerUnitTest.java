package com.eventease.eventease_service.unit_test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eventease.eventease_service.controller.EventController;
import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.EventRepository;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(EventController.class)
public class EventControllerUnitTest {

  private MockMvc mockMvc;

  @Mock
  private EventService eventService;

  @Mock
  private EventRepository eventRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private EventController eventController;

  private static final String BASE_URL = "/api/events";
  private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE;

  private static final String EVENT_REQUEST_BODY = """
        {
            "name": "Event Title",
            "description": "Event description",
            "location": "123 Venue St.",
            "date": "2024-11-15",
            "time": "10:30",
            "capacity": 100,
            "budget": 1200
        }
        """;

  private static final String UPDATE_EVENT_REQUEST_BODY = """
        {
            "name": "Updated Event",
            "capacity": 200
        }
        """;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
  }

  @Test
  public void addEventSuccessTest() throws Exception {
    User organizer = createTestUser(1L);
    Event event = createTestEvent(123L, "Event Title");

    when(userService.findUserById(1L)).thenReturn(organizer);
    doNothing().when(eventService).add(any(Event.class));

    mockMvc.perform(post(BASE_URL + "?organizerId=1")
            .contentType(CONTENT_TYPE)
            .content(EVENT_REQUEST_BODY))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.organizerId").value(1));
  }

  @Test
  public void addEventFailTest() throws Exception {
    when(userService.findUserById(1L))
        .thenThrow(new UserNotExistException("User is not found"));

    mockMvc.perform(post(BASE_URL + "?organizerId=1")
            .contentType(CONTENT_TYPE)
            .content(EVENT_REQUEST_BODY))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Organizer not found"));
  }

  @Test
  public void getEventByIdSuccessTest() throws Exception {
    Event event = createTestEvent(123L, "Event Title");
    when(eventService.findById(123L)).thenReturn(event);

    mockMvc.perform(get(BASE_URL + "/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(123))
        .andExpect(jsonPath("$.name").value("Event Title"));
  }

  @Test
  public void getEventByIdFailTest() throws Exception {
    when(eventService.findById(123L))
        .thenThrow(new EventNotExistException("Event not found"));

    mockMvc.perform(get(BASE_URL + "/123"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Event not found"));
  }

  @Test
  public void getEventsSuccessTest() throws Exception {
    List<Event> events = Arrays.asList(
        createTestEvent(1L, "Event 1", LocalDate.of(2024, 11, 15)),
        createTestEvent(2L, "Event 2", LocalDate.of(2024, 11, 20))
    );

    when(eventService.findByDateBetween(
        LocalDate.of(2024, 11, 1),
        LocalDate.of(2024, 11, 30)
    )).thenReturn(events);

    mockMvc.perform(get(BASE_URL + "?startDate=2024-11-01&endDate=2024-11-30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));
  }

  @Test
  public void getEventsFailTest() throws Exception {
    when(eventService.findByDateBetween(
        LocalDate.of(2024, 11, 1),
        LocalDate.of(2024, 11, 30)
    )).thenReturn(Collections.emptyList());

    mockMvc.perform(get(BASE_URL + "?startDate=2024-11-01&endDate=2024-11-30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  public void updateEventSuccessTest() throws Exception {
    Event existingEvent = createTestEvent(123L, "Old Event");
    when(eventService.findById(123L)).thenReturn(existingEvent);
    doNothing().when(eventService).updateEvent(eq(123L), any(Event.class));

    mockMvc.perform(patch(BASE_URL + "/123")
            .contentType(CONTENT_TYPE)
            .content(UPDATE_EVENT_REQUEST_BODY))
        .andExpect(status().isOk())
        .andExpect(content().string("Event updated successfully"));
  }

  @Test
  public void updateEventFailTest() throws Exception {
    when(eventService.findById(123L))
        .thenThrow(new EventNotExistException("Event not found"));

    mockMvc.perform(patch(BASE_URL + "/123")
            .contentType(CONTENT_TYPE)
            .content(UPDATE_EVENT_REQUEST_BODY))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Event not found"));
  }

  private User createTestUser(Long id) {
    User user = new User();
    user.setId(id);
    return user;
  }

  private Event createTestEvent(Long id, String name) {
    Event event = new Event();
    event.setId(id);
    event.setName(name);
    return event;
  }

  private Event createTestEvent(Long id, String name, LocalDate date) {
    Event event = createTestEvent(id, name);
    event.setDate(date);
    return event;
  }
}