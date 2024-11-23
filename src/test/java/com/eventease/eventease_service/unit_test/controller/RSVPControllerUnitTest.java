package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.RSVPController;
import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.RSVPService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RSVPController.class)
@ActiveProfiles("test")
public class RSVPControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RSVPService rsvpService;

  private static RSVP rsvp;

  @BeforeAll
  public static void setUp() {
    Event event = new Event();
    event.setId(1L);

    User user = new User();
    user.setId(1L);

    rsvp = new RSVP(user, event, "Going", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
            "Looking forward", false, "Guest");
  }

  @Test
  public void createRSVPSuccess() throws Exception {
    when(rsvpService.createRSVP(any(String.class), any(String.class), any(RSVP.class)))
            .thenReturn(rsvp);

    mockMvc.perform(post("/1/rsvp/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"Going\",\"notes\":\"Looking forward\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  public void createRSVPEventFailure() throws Exception {
    when(rsvpService.createRSVP(any(String.class), any(String.class), any(RSVP.class)))
            .thenThrow(new EventNotExistException("Event Not Found"));

    mockMvc.perform(post("/1/rsvp/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"Going\",\"notes\":\"Looking forward\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Event Not Found"));
  }

  @Test
  public void cancelRSVPSuccess() throws Exception {
    mockMvc.perform(delete("/1/rsvp/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("RSVP successfully cancelled"));
  }

  @Test
  public void cancelRSVPUserFailure() throws Exception {
    doThrow(new UserNotExistException("User Not Found"))
            .when(rsvpService).cancelRSVP(any(String.class), any(String.class));

    mockMvc.perform(delete("/1/rsvp/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("User Not Found"));
  }

  @Test
  public void getAttendeesSuccess() throws Exception {
    List<RSVP> attendees = new ArrayList<>();
    attendees.add(rsvp);

    when(rsvpService.getAttendeesByEvent(any(String.class)))
            .thenReturn(attendees);

    mockMvc.perform(get("/1/attendees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].status").value("Going"));
  }

  @Test
  public void getAttendeesFailure() throws Exception {
    when(rsvpService.getAttendeesByEvent(any(String.class)))
            .thenThrow(new EventNotExistException("Event Not Found"));

    mockMvc.perform(get("/1/attendees"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Event Not Found"));
  }
}
