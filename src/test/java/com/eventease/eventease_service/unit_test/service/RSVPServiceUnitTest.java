package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.exception.*;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class RSVPServiceUnitTest {

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
    event.setCapacity(1);
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
    event.setCapacity(1);
    User user = new User();
    RSVP rsvp = new RSVP();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.of(rsvp));

    assertThrows(RSVPExistsException.class, () -> rsvpService.createRSVP(eventId, userId, rsvp));
  }

  @Test
  void createRSVP_EventFull() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    event.setCapacity(1);
    event.setRsvpCount(1);
    User user = new User();
    RSVP rsvp = new RSVP();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);

    assertThrows(EventFullException.class, () -> rsvpService.createRSVP(eventId, userId, rsvp));
  }

  @Test
  void createRSVP_OverlappingRSVP() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    event.setCapacity(10);
    User user = new User();
    RSVP rsvp = new RSVP();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.empty());
    when(rsvpRepository.findOverlappingRSVPs(user.getId(), rsvp.getStartTime(), rsvp.getEndTime()))
            .thenReturn(List.of(new RSVP()));

    assertThrows(RSVPOverlapException.class, () -> rsvpService.createRSVP(eventId, userId, rsvp));
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

  @Test
  void updateRSVP_Success() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();
    RSVP rsvp = new RSVP();
    rsvp.setStatus("CONFIRMED");

    Map<String, Object> updates = Map.of(
            "status", "UPDATED",
            "notes", "New notes"
    );

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.of(rsvp));
    when(rsvpRepository.save(any(RSVP.class))).thenReturn(rsvp);

    RSVP result = rsvpService.updateRSVP(eventId, userId, updates);

    assertEquals("UPDATED", result.getStatus());
    assertEquals("New notes", result.getNotes());
    verify(rsvpRepository).save(rsvp);
  }

  @Test
  void updateRSVP_NotFound() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.empty());

    assertThrows(RSVPNotExistException.class, () -> rsvpService.updateRSVP(eventId, userId, Map.of("status", "UPDATED")));
  }


  @Test
  void checkInUser_Success() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    event.setAttendanceCount(0);
    User user = new User();
    RSVP rsvp = new RSVP();
    rsvp.setStatus("CONFIRMED");

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.of(rsvp));
    when(rsvpRepository.save(any(RSVP.class))).thenReturn(rsvp);

    assertDoesNotThrow(() -> rsvpService.checkInUser(eventId, userId));

    assertEquals("CheckedIn", rsvp.getStatus());
    assertEquals(1, event.getAttendanceCount());
    verify(rsvpRepository).save(rsvp);
    verify(eventService).saveEvent(event);
  }


  @Test
  void checkInUser_AlreadyCheckedIn() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();
    RSVP rsvp = new RSVP();
    rsvp.setStatus("CheckedIn");

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.of(rsvp));

    assertThrows(Exception.class, () -> rsvpService.checkInUser(eventId, userId));
  }


  @Test
  void checkInUser_NotFound() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.empty());

    assertThrows(RSVPNotExistException.class, () -> rsvpService.checkInUser(eventId, userId));
  }

  @Test
  void checkInUser_UserNotFound() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();

    when(eventService.findById(1L)).thenReturn(event);
    when(userService.findUserById(1L)).thenReturn(null);

    assertThrows(UserNotExistException.class, () -> rsvpService.checkInUser(eventId, userId));
  }


  @Test
  void checkInUser_EventNotFound() {
    String eventId = "1";
    String userId = "1";
    Event event = new Event();
    User user = new User();

    when(eventService.findById(1L)).thenReturn(null);

    assertThrows(EventNotExistException.class, () -> rsvpService.checkInUser(eventId, userId));
  }




  @Test
  void getAllRSVPsByUser_Success() {
    String userId = "1";
    User user = new User();
    List<RSVP> expectedRSVPs = Arrays.asList(new RSVP(), new RSVP());

    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findAllByUserOrderByEventDate(user)).thenReturn(expectedRSVPs);

    List<RSVP> result = rsvpService.getAllRSVPsByUser(userId);

    assertEquals(expectedRSVPs, result);
    verify(rsvpRepository).findAllByUserOrderByEventDate(user);
  }


  @Test
  void getAllRSVPsByUser_UserNotFound() {
    String userId = "1";

    when(userService.findUserById(1L)).thenReturn(null);

    assertThrows(UserNotExistException.class, () -> rsvpService.getAllRSVPsByUser(userId));
  }


  @Test
  void getCheckedInRSVPsByUser_Success() {
    String userId = "1";
    User user = new User();
    List<RSVP> expectedRSVPs = Arrays.asList(new RSVP(), new RSVP());

    when(userService.findUserById(1L)).thenReturn(user);
    when(rsvpRepository.findAllByUserAndStatusOrderByEventDate(user, "CheckedIn")).thenReturn(expectedRSVPs);

    List<RSVP> result = rsvpService.getCheckedInRSVPsByUser(userId);

    assertEquals(expectedRSVPs, result);
    verify(rsvpRepository).findAllByUserAndStatusOrderByEventDate(user, "CheckedIn");
  }


  @Test
  void getCheckedInRSVPsByUser_UserNotFound() {
    String userId = "1";

    when(userService.findUserById(1L)).thenReturn(null);

    assertThrows(UserNotExistException.class, () -> rsvpService.getCheckedInRSVPsByUser(userId));
  }

}