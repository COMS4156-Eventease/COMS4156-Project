package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.RSVPRepository;
import com.eventease.eventease_service.service.EventService;
import com.eventease.eventease_service.service.RSVPService;
import com.eventease.eventease_service.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the RSVPService class.
 */
public class RSVPServiceUnitTest {
  @Mock
  private RSVPRepository rsvpRepository;

  @Mock
  private UserService userService;

  @Mock
  private EventService eventService;

  @InjectMocks
  private RSVPService rsvpService;

  private User user;
  private Event event;
  private RSVP rsvp;

  /**
   * Set up the test environment.
   */
  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new User();
    user.setId(1L);
    user.setFirstName("John");
    user.setLastName("Doe");

    event = new Event();
    event.setId(1L);
    event.setName("Event 1");
    event.setHost(user);

    rsvp = new RSVP();
    rsvp.setUser(user);
    rsvp.setEvent(event);
    rsvp.setStatus("CONFIRMED");
  }

  /**
   * Test for creating an RSVP successfully.
   */
  @Test
  public void testCreateRSVP_success() {
    when(userService.findUserById(1L)).thenReturn(user);
    when(eventService.findById(1L)).thenReturn(event);
    when(rsvpRepository.findByUserAndEvent(user, event)).thenReturn(Optional.empty());
    when(rsvpRepository.save(any(RSVP.class))).thenReturn(rsvp);

    RSVP createdRSVP = rsvpService.createRSVP("1", "1", rsvp);

    assertNotNull(createdRSVP);
    assertEquals(user, createdRSVP.getUser());
    assertEquals(event, createdRSVP.getEvent());
  }

}
