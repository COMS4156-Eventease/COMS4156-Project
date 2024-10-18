package com.eventease.eventease_service.unit_test.repository;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.EventRepository;
import com.eventease.eventease_service.repository.RSVPRepository;
import com.eventease.eventease_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RSVPRepositoryUnitTest {
  @Autowired
  private RSVPRepository rsvpRepository;

  private User user;
  private Event event;
  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    User user = new User.Builder()
            .setUsername("benjohnson")
            .setPassword("password")
            .build();
    user = userRepository.save(user);
    this.user = user;

    Event event = new Event();
    event.setName("Spring Boot Workshop");
    event.setHost(user);
    event = eventRepository.save(event);
    this.event = event;


    RSVP rsvp = new RSVP();
    rsvp.setUser(user);
    rsvp.setEvent(event);
    rsvp.setStatus("CONFIRMED");
    rsvp.setTimestamp(LocalDateTime.now());
    rsvp.setNotes("Looking forward to the event!");
    rsvp.setReminderSent(false);
    rsvp.setEventRole("Attendee");
    rsvpRepository.save(rsvp);
  }

  @Test
  public void testFindByUserAndEventSuccess() {
    System.out.println(user.toString());
    Optional<RSVP> foundRSVP = rsvpRepository.findByUserAndEvent(user, event);
    assertTrue(foundRSVP.isPresent());
    assertTrue(foundRSVP.get().getStatus().equals("CONFIRMED"));
  }

  @Test
  public void testFindByUserAndEventFailure() {
    Event event2 = new Event();
    event2.setId(2L);
    Optional<RSVP> foundRSVP = rsvpRepository.findByUserAndEvent(user, event2);
    assertFalse(foundRSVP.isPresent());
  }

  @Test
  public void testFindByEventSuccess() {
    List<RSVP> foundRSVP = rsvpRepository.findByEvent(event);
    assertTrue(foundRSVP.size() == 1);
  }

  @Test
  public void testFindByEventFailure() {
    Event event2 = new Event();
    event2.setId(2L);
    List<RSVP> foundRSVP = rsvpRepository.findByEvent(event2);
    assertTrue(foundRSVP.size() == 0);
  }




}
