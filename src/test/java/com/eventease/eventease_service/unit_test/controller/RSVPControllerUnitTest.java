package com.eventease.eventease_service.unit_test.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.eventease.eventease_service.controller.RSVPController;
import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.RSVPNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.RSVPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

  @BeforeEach
  public void setUp() {
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

    mockMvc.perform(post("/api/events/1/rsvp/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"Going\",\"notes\":\"Looking forward\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  public void createRSVPEventFailure() throws Exception {
    when(rsvpService.createRSVP(any(String.class), any(String.class), any(RSVP.class)))
            .thenThrow(new EventNotExistException("Event Not Found"));

    mockMvc.perform(post("/api/events/1/rsvp/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"Going\",\"notes\":\"Looking forward\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Event Not Found"));
  }

  @Test
  public void cancelRSVPSuccess() throws Exception {
    mockMvc.perform(delete("/api/events/1/rsvp/cancel/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("RSVP successfully cancelled"));
  }

  @Test
  public void cancelRSVPUserFailure() throws Exception {
    doThrow(new UserNotExistException("User Not Found"))
            .when(rsvpService).cancelRSVP(any(String.class), any(String.class));

    mockMvc.perform(delete("/api/events/1/rsvp/cancel/1"))
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

    mockMvc.perform(get("/api/events/1/attendees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].status").value("Going"));
  }

  @Test
  public void getAttendeesFailure() throws Exception {
    when(rsvpService.getAttendeesByEvent(any(String.class)))
            .thenThrow(new EventNotExistException("Event Not Found"));

    mockMvc.perform(get("/api/events/1/attendees"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Event Not Found"));
  }

  @Test
  public void updateRSVPSuccess() throws Exception {
    RSVP updatedRSVP = new RSVP();
    updatedRSVP.setStatus("Updated");
    updatedRSVP.setNotes("Updated notes");

    when(this.rsvpService.updateRSVP(any(String.class), any(String.class), any()))
            .thenReturn(updatedRSVP);

    mockMvc.perform(patch("/api/events/1/rsvp/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"Updated\",\"notes\":\"Updated notes\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].status").value("Updated"))
            .andExpect(jsonPath("$.data[0].notes").value("Updated notes"));
  }


  @Test
  public void updateRSVPFailure() throws Exception {
    when(rsvpService.updateRSVP(any(String.class), any(String.class), any()))
            .thenThrow(new RSVPNotExistException("RSVP Not Found"));

    mockMvc.perform(patch("/api/events/1/rsvp/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"Updated\",\"notes\":\"Updated notes\"}"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("RSVP Not Found"));
  }


  @Test
  void checkInUserSuccess() throws Exception {
    mockMvc.perform(post("/api/events/1/rsvp/checkin/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User successfully checked in"));
  }

  @Test
  void checkInUserFailure() throws Exception {
    doThrow(new RSVPNotExistException("RSVP Not Found"))
            .when(rsvpService).checkInUser(any(String.class), any(String.class));

    mockMvc.perform(post("/api/events/1/rsvp/checkin/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("RSVP Not Found"));
  }

  @Test
  void getAllRSVPsForUserSuccess() throws Exception {
    when(rsvpService.getAllRSVPsByUser(any(String.class)))
            .thenReturn(Collections.singletonList(rsvp));

    mockMvc.perform(get("/api/events/rsvp/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].status").value("Going"));
  }


  @Test
  void getAllRSVPsForUserFailure() throws Exception {
    when(rsvpService.getAllRSVPsByUser(any(String.class)))
            .thenThrow(new UserNotExistException("User Not Found"));

    mockMvc.perform(get("/api/events/rsvp/user/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("User Not Found"));
  }


  @Test
  void getCheckedInRSVPsForUserSuccess() throws Exception {
    RSVP checkedInRSVP = new RSVP();
    checkedInRSVP.setStatus("CheckedIn");

    when(rsvpService.getCheckedInRSVPsByUser(any(String.class)))
            .thenReturn(Collections.singletonList(checkedInRSVP));

    mockMvc.perform(get("/api/events/rsvp/user/1/checkedin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].status").value("CheckedIn"));
  }

  @Test
  void getCheckedInRSVPsForUserFailure() throws Exception {
    when(rsvpService.getCheckedInRSVPsByUser(any(String.class)))
            .thenThrow(new UserNotExistException("User Not Found"));

    mockMvc.perform(get("/api/events/rsvp/user/1/checkedin"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("User Not Found"));
  }


}
