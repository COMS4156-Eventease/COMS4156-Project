package com.eventease.eventease_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "event_image")
public class EventImage implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  private String url;

  @ManyToOne
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  public EventImage() {}

  public EventImage(String url, Event event) {
    this.url = url;
    this.event = event;
  }

  public String getUrl() {
    return url;
  }

  public EventImage setUrl(String url) {
    this.url = url;
    return this;
  }

  public Event getEvent() {
    return event;
  }

  public EventImage setEvent(Event event) {
    this.event = event;
    return this;
  }
}
