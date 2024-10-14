package com.eventease.eventease_service.exception;

public class EventNotExistException extends RuntimeException {
  public EventNotExistException(String message) {
    super(message);
  }
}
