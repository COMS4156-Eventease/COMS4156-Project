package com.eventease.eventease_service.exception;

public class EventFullException extends RuntimeException {
  public EventFullException(String message) {
    super(message);
  }
}