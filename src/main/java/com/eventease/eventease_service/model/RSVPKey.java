package com.eventease.eventease_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RSVPKey {
  private Long user_id;
  private Long event_id;
}
