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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
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
    event.setId(123L);
    event.setName("Event Title");

    when(userService.findUserById(1L)).thenReturn(organizer);
    doNothing().when(eventService).add(any(Event.class));

    mockMvc.perform(post("/api/events?organizerId=1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"Event Title\", \"description\": \"Event description\", \"location\": \"123 Venue St.\", \"date\": \"2024-11-15\", \"time\": \"10:30\", \"capacity\": 100, \"budget\": 1200 }"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.organizerId").value(1));
  }

  @Test
  public void addEventFailTest() throws Exception {
    when(userService.findUserById(1L)).thenThrow(new UserNotExistException("User is not found"));

    mockMvc.perform(post("/api/events?organizerId=1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"Event Title\", \"description\": \"Event description\", \"location\": \"123 Venue St.\", \"date\": \"2024-11-15\", \"time\": \"10:30\", \"capacity\": 100, \"budget\": 1200 }"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Organizer not found"));
  }

  @Test
  public void getEventByIdSuccessTest() throws Exception {
    Event event = new Event();
    event.setId(123L);
    event.setName("Event Title");

    when(eventService.findById(123L)).thenReturn(event);

    mockMvc.perform(get("/api/events/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(123))
        .andExpect(jsonPath("$.name").value("Event Title"));
  }

  @Test
  public void getEventByIdFailTest() throws Exception {
    when(eventService.findById(123L)).thenThrow(new EventNotExistException("Event not found"));


    mockMvc.perform(get("/api/events/123"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Event not found"));
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
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));
  }

  // Test for failing to retrieve events within a date range
  @Test
  public void getEventsFailTest() throws Exception {
    when(eventService.findByDateBetween(LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30))).thenReturn(
        Collections.emptyList());

    mockMvc.perform(get("/api/events?startDate=2024-11-01&endDate=2024-11-30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  public void updateEventSuccessTest() throws Exception {
    Event existingEvent = new Event();
    existingEvent.setId(123L);
    existingEvent.setName("Old Event");

    Event updatedEvent = new Event();
    updatedEvent.setName("Updated Event");

    when(eventService.findById(123L)).thenReturn(existingEvent);
    doNothing().when(eventService).updateEvent(eq(123L), any(Event.class));

    mockMvc.perform(patch("/api/events/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"Updated Event\", \"capacity\": 200 }"))
        .andExpect(status().isOk())
        .andExpect(content().string("Event updated successfully"));
  }

  // Test for failing to update an event (event not found)
  @Test
  public void updateEventFailTest() throws Exception {
    when(eventService.findById(123L)).thenThrow(new EventNotExistException("Event not found"));

    mockMvc.perform(patch("/api/events/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"Updated Event\", \"capacity\": 200 }"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Event not found"));
  }
}
