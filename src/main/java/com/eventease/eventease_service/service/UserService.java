package com.eventease.eventease_service.service;

import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void addUser(User user) {
        // Set any additional logic if necessary (e.g., default values)
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Save the user to the database
        userRepository.save(user);
    }


    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
