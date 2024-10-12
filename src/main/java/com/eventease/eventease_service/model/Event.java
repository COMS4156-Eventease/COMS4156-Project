package com.eventease.eventease_service.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an Event on the EventEase service.
 * This class stores information about the Event, including its id,
 * name, description, location, date, time, organizer ID, capacity and budget.
 */
public class Event implements Serializable {
  @Serial
  private static final long serialVersionUID = 100000L;

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int eventId;

  private String name;
  private String description;
  private String location;
  private String date;
  private String time;
  private Organizer organizer;
  private int capacity;
  private int budget;

  /**
   * Constructs a new Event object with the given parameters.
   *
   * @param name        The name of the event.
   * @param description A description of the event.
   * @param location    The location of the event.
   * @param date        The date of the event (in String format, should ideally be LocalDate).
   * @param time        The time of the event (in String format, should ideally be LocalTime).
   * @param organizer   The organizer for this event.
   * @param capacity    The capacity for the event (number of people allowed to attend).
   * @param budget      The budget allocated for the event.
   */
  public Event(String name, String description, String location, String date, String time,
      Organizer organizer, int capacity, int budget) {
    this.name = name;
    this.description = description;
    this.location = location;
    this.date = date;
    this.time = time;
    this.organizer = organizer;
    this.capacity = capacity;
    this.budget = budget;
  }


}
