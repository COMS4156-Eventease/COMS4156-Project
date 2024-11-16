package com.eventease.eventease_service.model;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class for login requests
 */
@Setter
@Getter
public class LoginRequest {
  // Getters and setters
  private String username;
  private String password;

  // Default constructor
  public LoginRequest() {
  }

  // Constructor with all fields
  public LoginRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }

}
