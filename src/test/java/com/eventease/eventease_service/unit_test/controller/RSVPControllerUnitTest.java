package com.eventease.eventease_service.unit_test.controller;


import com.eventease.eventease_service.controller.RSVPController;
import com.eventease.eventease_service.exception.EventNotExistException;
import com.eventease.eventease_service.exception.RSVPNotExistException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.RSVPService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the RSVPController class.
 */
@SpringBootTest
public class RSVPControllerUnitTest {
  @InjectMocks
  RSVPController rsvpController;

  @Mock
  RSVPService rsvpService;

  private static RSVP rsvp;

  /**
   * Set up the test environment.
   */
  @BeforeAll
  public static void setUp() {
    Event event = new Event();
    event.setId(1L);

    User user = new User();
    user.setId(1L);

    rsvp = new RSVP(user, event, "Going", LocalDateTime.now(), "Looking forward", false, "Guest");
  }

  /**
   * Test for creating an RSVP successfully.
   */
  @Test
  public void createRSVPSuccess() {
    when(rsvpService.createRSVP(any(String.class), any(String.class), any(RSVP.class))).thenReturn(rsvp);
    ResponseEntity<?> response = rsvpController.createRSVP("1", "1", rsvp);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  /**
   * Test for creating an RSVP when the event does not exist.
   */
  @Test
  public void createRSVPEventFailure()  {
    when(rsvpService.createRSVP(any(String.class), any(String.class), any(RSVP.class))).thenThrow(new EventNotExistException("Event Not Found"));
    ResponseEntity<?> response = rsvpController.createRSVP("1", "1", rsvp);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Event Not Found", response.getBody());
  }

  /**
   * Test for creating an RSVP when the user does not exist.
   */
  @Test
  public void createRSVPUserFailure() {
    when(rsvpService.createRSVP(any(String.class), any(String.class), any(RSVP.class))).thenThrow(new UserNotExistException("User Not Found"));
    ResponseEntity<?> response = rsvpController.createRSVP("1", "1", rsvp);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User Not Found", response.getBody());
  }

  /**
   * Test for getting attendees successfully.
   */
  @Test
  public void getAttendeesSuccess() {
    List<RSVP> attendees = new ArrayList<>();
    attendees.add(rsvp);

    when(rsvpService.getAttendeesByEvent(any(String.class))).thenReturn(attendees);
    ResponseEntity<?> response = rsvpController.getAttendee("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void getAttendeesFailure() {
    when(rsvpService.getAttendeesByEvent(any(String.class))).thenThrow(new EventNotExistException("Event Not Found"));
    ResponseEntity<?> response = rsvpController.getAttendee("1");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Event Not Found", response.getBody());
  }

  /**
   * Test for cancelling an RSVP successfully.
   */
  @Test
  public void cancelRSVPSuccess() {
    ResponseEntity<?> response = rsvpController.cancelRSVP("1", "1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("RSVP successfully cancelled", response.getBody());
  }

  /**
   * Test for cancelling an RSVP when the user does not exist.
   */
  @Test
  public void cancelRSVPUserFailure() {
    doThrow(new UserNotExistException("User Not Found")).when(rsvpService).cancelRSVP(any(String.class), any(String.class));
    ResponseEntity<?> response = rsvpController.cancelRSVP("1", "1");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User Not Found", response.getBody());
  }

  /**
   * Test for cancelling an RSVP when the event does not exist.
   */
  @Test
  public void cancelRSVPEventFailure() {
    doThrow(new EventNotExistException("Event Not Found")).when(rsvpService).cancelRSVP(any(String.class), any(String.class));
    ResponseEntity<?> response = rsvpController.cancelRSVP("1", "1");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Event Not Found", response.getBody());
  }

  /**
   * Test for cancelling an RSVP when the RSVP does not exist.
   */
  @Test
  public void cancelRSVPRSVPFailure() {
    doThrow(new RSVPNotExistException("RSVP Not Found")).when(rsvpService).cancelRSVP(any(String.class), any(String.class));
    ResponseEntity<?> response = rsvpController.cancelRSVP("1", "1");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("RSVP Not Found", response.getBody());
  }
}
