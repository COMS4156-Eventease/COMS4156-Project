package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.exception.RSVPExistsException;
import com.eventease.eventease_service.exception.RSVPNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.RSVPRepository;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.RSVPService;
import com.eventease.eventease_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RSVPServiceTest {

  @Mock
  private RSVPRepository rsvpRepository;

  @Mock
  private UserService userService;

  @Mock
  private EventService eventService;

  @InjectMocks
  private RSVPService rsvpService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createRSVP_Success() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();
    RSVP rsvp = new RSVP();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.empty());
    when(rsvpRepository.save(any(RSVP.class))).thenReturn(rsvp);

    RSVP result = rsvpService.createRSVP(eventId, userId, rsvp);

    assertNotNull(result);
    assertEquals(event, rsvp.getEvent());
    assertEquals(user, rsvp.getUser());
    verify(rsvpRepository).save(rsvp);
  }

  @Test
  void createRSVP_AlreadyExists() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();
    RSVP rsvp = new RSVP();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.of(rsvp));

    assertThrows(RSVPExistsException.class, () -> rsvpService.createRSVP(eventId, userId, rsvp));
  }

  @Test
  void getAttendeesByEvent_Success() {
    String eventId = "1";
    Event event = new Event();
    List<RSVP> expectedRSVPs = Arrays.asList(new RSVP(), new RSVP());

    when(eventService.findById(1L)).thenReturn(event);
    when(rsvpRepository.findByEvent(event)).thenReturn(expectedRSVPs);

    List<RSVP> result = rsvpService.getAttendeesByEvent(eventId);

    assertEquals(expectedRSVPs, result);
    verify(rsvpRepository).findByEvent(event);
  }

  @Test
  void cancelRSVP_Success() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();
    RSVP rsvp = new RSVP();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.of(rsvp));

    assertDoesNotThrow(() -> rsvpService.cancelRSVP(eventId, userId));
    verify(rsvpRepository).delete(rsvp);
  }

  @Test
  void cancelRSVP_NotFound() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.empty());

    assertThrows(RSVPNotExistException.class, () -> rsvpService.cancelRSVP(eventId, userId));
  }
}