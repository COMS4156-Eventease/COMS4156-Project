package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.*;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.RSVPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RSVPService {

  @Autowired
  private RSVPRepository rsvpRepository;

  @Autowired
  UserService userService;

  @Autowired
  EventService eventService;

  public RSVP createRSVP(String eventId, String userId, RSVP rsvp) {
    Event event = eventService.findById(Long.parseLong(eventId));
    User user = userService.findUserById(Long.parseLong(userId));

    // check if the event is full;
    if (event.getRsvpCount() >= event.getCapacity()) {
      throw new EventFullException("Event is already at full capacity");
    }

    // check duplicate RSVP
    Optional<RSVP> rsvpCheck= rsvpRepository.findByUserAndEvent(user, event);
    if(rsvpCheck.isPresent()) {
      throw new RSVPExistsException("RSVP Already Exists");
    }
    rsvp.setEvent(event);
    rsvp.setUser(user);
    event.setRsvpCount(event.getRsvpCount() + 1);
    return rsvpRepository.save(rsvp);
  }


  public List<RSVP> getAttendeesByEvent(String eventId) {
    Event event = eventService.findById(Long.parseLong(eventId));
    return rsvpRepository.findByEvent(event);
  }


  public void cancelRSVP(String eventId, String userId) {
    Event event = eventService.findById(Long.parseLong(eventId));
    User user = userService.findUserById(Long.parseLong(userId));
    Optional<RSVP> optionalRSVP = rsvpRepository.findByUserAndEvent(user, event);
    if (optionalRSVP.isPresent()) {
      RSVP rsvp = optionalRSVP.get();
      rsvpRepository.delete(rsvp);
      event.setRsvpCount(event.getRsvpCount() - 1);
    } else {
      throw new RSVPNotExistException("RSVP not found");
    }
  }

  public RSVP updateRSVP(String eventId, String userId, Map<String, Object> rsvpUpdates){

    Event event = eventService.findById(Long.parseLong(eventId));
    User user = userService.findUserById(Long.parseLong(userId));

    Optional<RSVP> optionalRSVP = rsvpRepository.findByUserAndEvent(user, event);
    if (optionalRSVP.isEmpty()) {
      throw new RSVPNotExistException("RSVP does not exist for this event and user");
    }

    RSVP rsvp = optionalRSVP.get();

    if (rsvpUpdates.containsKey("status")) {
      rsvp.setStatus((String) rsvpUpdates.get("status"));
    }
    if (rsvpUpdates.containsKey("notes")) {
      rsvp.setNotes((String) rsvpUpdates.get("notes"));
    }
    if (rsvpUpdates.containsKey("reminderSent")) {
      rsvp.setReminderSent((Boolean) rsvpUpdates.get("reminderSent"));
    }
    if (rsvpUpdates.containsKey("eventRole")) {
      rsvp.setEventRole((String) rsvpUpdates.get("eventRole"));
    }

    return rsvpRepository.save(rsvp);
  }

  public void checkInUser(String eventId, String userId) {
    Event event = eventService.findById(Long.parseLong(eventId));
    if (event == null) {
      throw new EventNotExistException("Event does not exist.");
    }

    User user = userService.findUserById(Long.parseLong(userId));
    if(user == null) {
      throw new UserNotExistException("User does not exist.");
    }

    RSVP rsvp = rsvpRepository.findByUserAndEvent(user, event)
            .orElseThrow(() -> new RSVPNotExistException("No RSVP found for this user at the event."));

    // Check if the user is already checked in
    if (rsvp.getStatus().equals("CheckedIn")) {
      throw new IllegalArgumentException("User has already been checked in.");
    }

    rsvp.setStatus("CheckedIn");
    rsvpRepository.save(rsvp);

    event.setAttendanceCount(event.getAttendanceCount() + 1);
    eventService.saveEvent(event);
  }

  public List<RSVP> getAllRSVPsByUser(String userId) {
    User user = userService.findUserById(Long.parseLong(userId));
    if (user == null) {
      throw new UserNotExistException("User does not exist.");
    }
    return rsvpRepository.findAllByUserOrderByEventDate(user);
  }

  public List<RSVP> getCheckedInRSVPsByUser(String userId) {
    User user = userService.findUserById(Long.parseLong(userId));
    if (user == null) {
      throw new UserNotExistException("User does not exist.");
    }
    return rsvpRepository.findAllByUserAndStatusOrderByEventDate(user, "CheckedIn");
  }
}
