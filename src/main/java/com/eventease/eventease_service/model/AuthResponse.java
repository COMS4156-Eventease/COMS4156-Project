package com.eventease.eventease_service.model;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class for authentication response containing JWT token
 */
@Setter
@Getter
public class AuthResponse {
  // Getters and setters
  private String token;
  private String type = "Bearer";

  // Default constructor
  public AuthResponse() {
  }

  // Constructor with token
  public AuthResponse(String token) {
    this.token = token;
  }

  /**
   * Returns the full token with type (e.g., "Bearer eyJ0...")
   * @return The full token string
   */
  public String getFullToken() {
    return type + " " + token;
  }
}
