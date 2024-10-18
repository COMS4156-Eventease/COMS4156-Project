package com.eventease.eventease_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RSVPKey {
  private User user;
  private Event event;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RSVPKey rsvpKey = (RSVPKey) o;
    return Objects.equals(user, rsvpKey.user) && Objects.equals(event, rsvpKey.event);
  }


  @Override
  public int hashCode() {
    return Objects.hash(user, event);
  }
}
