package com.eventease.eventease_service.controller;

import com.eventease.eventease_service.model.AuthResponse;
import com.eventease.eventease_service.model.LoginRequest;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.security.JwtUtil;
import com.eventease.eventease_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Authenticates user and generates JWT token
   * @param loginRequest Contains username and password
   * @return JWT token if authentication successful
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    // Special case for test user (remove in prod!)
    if ("test".equals(loginRequest.getUsername()) &&
        "test".equals(loginRequest.getPassword())) {
      String token = jwtUtil.generateToken("test", "CAREGIVER");
      return ResponseEntity.ok(new AuthResponse(token));
    }

    User user = userService.findByUsername(loginRequest.getUsername());
    if (user != null && passwordEncoder.matches(
        loginRequest.getPassword(), user.getPassword())) {
      String token = jwtUtil.generateToken(
          user.getUsername(), user.getRole().name());
      return ResponseEntity.ok(new AuthResponse(token));
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}
