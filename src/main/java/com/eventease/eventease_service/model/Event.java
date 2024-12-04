package com.eventease.eventease_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an Event on the EventEase service.
 * This class stores information about the Event, including its id,
 * name, description, location, date, time, organizer ID, capacity and budget.
 */
@Entity
@Table(name = "event")
@JsonDeserialize(builder = Event.Builder.class)
public class Event implements Serializable {

  @Serial
  private static final long serialVersionUID = 100000L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private String location;
  private LocalDate date;
  private LocalTime time;
  private LocalTime endTime;
  private int capacity;
  private int budget;
  private int rsvpCount = 0;
  private int attendanceCount = 0;

  // Many-to-One relationship to represent the host (creator) of the event
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User host;

  // Many-to-many relationship for event participants
  @JsonIgnore
  @ManyToMany
  @JoinTable(
      name = "event_participants",
      joinColumns = @JoinColumn(name = "event_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> participants = new HashSet<User>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<EventImage> images;

  public Event() {

  }

  public Event(Builder builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.location = builder.location;
    this.date = builder.date;
    this.time = builder.time;
    this.endTime = builder.endTime;
    this.host = builder.host;
    this.capacity = builder.capacity;
    this.budget = builder.budget;
    this.images = builder.images;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public LocalTime getEndTime() {
    return this.endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public int getBudget() {
    return budget;
  }

  public void setBudget(int budget) {
    this.budget = budget;
  }

  public User getHost() {
    return host;
  }

  public void setHost(User host) {
    this.host = host;
  }

  public Set<User> getParticipants() {
    return participants;
  }

  public void setParticipants(Set<User> participants) {
    this.participants = participants;
  }

  public List<EventImage> getImages() {
    return images;
  }

  public Event setImages(List<EventImage> images) {
    this.images = images;
    return this;
  }
  
  public void setRsvpCount(int rsvpCount){
    this.rsvpCount = rsvpCount;
  }

  public int getRsvpCount(){
    return this.rsvpCount;
  }

  public void setAttendanceCount(int attendanceCount){
    this.attendanceCount = attendanceCount;
  }

  public int getAttendanceCount(){
    return this.attendanceCount;
  }

  // Add a participant to the event
  public void addParticipant(User user) {
    this.participants.add(user);
  }

  // Remove a participant from the event
  public void removeParticipant(User user) {
    this.participants.remove(user);
  }

  public static class Builder{

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("location")
    private String location;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("time")
    private LocalTime time;

    @JsonProperty("endTime")
    public LocalTime endTime;

    @JsonProperty("capacity")
    private int capacity;

    @JsonProperty("budget")
    private int budget;

    @JsonProperty("host")
    private User host;

    @JsonProperty("participants")
    private Set<User> participants = new HashSet<User>();

    @JsonProperty("images")
    private List<EventImage> images = new ArrayList<EventImage>();

    public Builder setId(Long id) {
      this.id = id;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setLocation(String location) {
      this.location = location;
      return this;
    }

    public Builder setDate(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder setTime(LocalTime time) {
      this.time = time;
      return this;
    }

    public Builder setEndTime(LocalTime endTime) {
      this.endTime = endTime;
      return this;
    }

    public Builder setCapacity(int capacity) {
      this.capacity = capacity;
      return this;
    }

    public Builder setBudget(int budget) {
      this.budget = budget;
      return this;
    }

    public Builder setHost(User host) {
      this.host = host;
      return this;
    }

    public Builder setParticipants(Set<User> participants) {
      this.participants = participants;
      return this;
    }

    public Builder setImages(List<EventImage> images) {
      this.images = images;
      return this;
    }

    public Event build() {
      return new Event(this);
    }
  }
}
