package com.eventease.eventease_service.exception;

public class TaskNotExistException extends RuntimeException {
  public TaskNotExistException(String message) {
    super(message);
  }
}