package com.eventease.eventease_service.service;

import com.eventease.eventease_service.model.RSVP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class RSVPService {
  @Autowired
  UserService userService;

  @Autowired
  EventService eventService;

  public ResponseEntity<?> createRSVP(String eventId, RSVP rsvp){
    ResponseEntity<?> eventResponse = eventService. ;// to do
    if(eventResponse.getStatusCode() != HttpStatus.OK){
      ResponseEntity<?> responseEntity = new ResponseEntity<>("Event Not Found", HttpStatus.NOT_FOUND);
      return responseEntity;
    }
    ResponseEntity<?> userResponse = userService. ;
    if(eventResponse.getStatusCode() != HttpStatus.OK){
      ResponseEntity<?> responseEntity = new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
      return responseEntity;
    }
  }

}
