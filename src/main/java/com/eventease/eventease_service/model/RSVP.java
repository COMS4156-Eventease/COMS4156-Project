package com.eventease.eventease_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RSVP")
public class RSVP {

  @EmbeddedId
  private RSVPId id;

  private String status;

  private LocalDateTime timestamp;

  private String notes;

  private boolean reminderSent;

  private String eventRole;


}
