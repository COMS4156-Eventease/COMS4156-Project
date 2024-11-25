package com.eventease.eventease_service.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RSVP")
@IdClass(RSVPKey.class)
public class RSVP {
  @Id
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Id
  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  private String status;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private String notes;

  private boolean reminderSent;

  private String eventRole;

}
