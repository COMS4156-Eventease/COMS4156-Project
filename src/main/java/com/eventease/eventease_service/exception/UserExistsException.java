package com.eventease.eventease_service.exception;

public class UserExistsException extends RuntimeException{
  public UserExistsException(String message) {
    super(message);
  }
}
