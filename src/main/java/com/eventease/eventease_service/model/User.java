package com.eventease.eventease_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Entity
@Table(name = "user")
@JsonDeserialize(builder = User.Builder.class)
@Getter
@Setter
public class User implements Serializable {
  @Serial
  private static final long serialVersionUID = 100001L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("username")
  private String username;

  @JsonProperty("password")
  @JsonIgnore
  private String password;

  @JsonProperty("email")
  private String email;

  @JsonProperty("phoneNumber")
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @JsonProperty("role")
  private Role role;

  @JsonProperty("createdAt")
  private Timestamp createdAt;

  @JsonProperty("updatedAt")
  private Timestamp updatedAt;

  @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private Set<Event> createdEvents = new HashSet<>();

  // Many-to-Many relationship: a user can attend many events
  @ManyToMany
  @JoinTable(
      name = "event_participants",  // Name of the join table
      joinColumns = @JoinColumn(name = "user_id"),  // Column for the user
      inverseJoinColumns = @JoinColumn(name = "event_id")  // Column for the event
  )
  private Set<Event> attendedEvents = new HashSet<>();

  public User() {};

  public User(Builder builder) {
    this.username = builder.username;
    this.password = builder.password;
  }

  // helper
  public void addCreatedEvent(Event event) {
    event.setHost(this);
    createdEvents.add(event);
  }

  public void removeCreatedEvent(Event event) {
    event.setHost(null);
    createdEvents.remove(event);
  }

  public void attendEvent(Event event) {
    attendedEvents.add(event);
    event.getParticipants().add(this);
  }

  public void leaveEvent(Event event) {
    attendedEvents.remove(event);
    event.getParticipants().remove(this);  // Remove this user from the event's participants
  }

  public static class Builder {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    @JsonIgnore
    private String password;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @JsonProperty("role")
    private Role role;

    @JsonProperty("createdAt")
    private Timestamp createdAt;

    @JsonProperty("updatedAt")
    private Timestamp updatedAt;

    @JsonProperty("createdEvents")
    private Set<Event> createdEvents = new HashSet<>();

    @JsonProperty("attendedEvents")
    private Set<Event> attendedEvents = new HashSet<>();

    public Builder setId(Long id) {
      this.id = id;
      return this;
    }

    public Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder setCreatedEvents(
        Set<Event> createdEvents) {
      this.createdEvents = createdEvents;
      return this;
    }

    public Builder setAttendedEvents(
        Set<Event> attendedEvents) {
      this.attendedEvents = attendedEvents;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }

  public enum Role {
    ELDERLY, CAREGIVER, ORGANIZER, PLANNER
  }
}
