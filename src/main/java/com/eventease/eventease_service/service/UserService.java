package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private UserRepository userRepository;

  // @Autowired is used to inject UserRepository automatically
  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // This method retrieves a user by its ID. If the user is not found, it throws an exception
  public User findUserById(long id) {
    User user = userRepository.findById(id);
    if (user == null) {
      throw new UserNotExistException("User is not found.");
    }
    return user;
  }
}
