package com.eventease.eventease_service.service;

import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

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

    public Iterable<User> getUsersByFilter(
            String firstName, String lastName, String email,
            String phone, User.Role role)
    {
        return userRepository.findAll((root, query, cb) -> {
            // Create a predicate for filtering based on the provided parameters
            return cb.and(
                    firstName != null ? cb.equal(root.get("firstName"), firstName) : cb.conjunction(),
                    lastName != null ? cb.equal(root.get("lastName"), lastName) : cb.conjunction(),
                    email != null ? cb.equal(root.get("email"), email) : cb.conjunction(),
                    phone != null ? cb.equal(root.get("phoneNumber"), phone) : cb.conjunction(),
                    role != null ? cb.equal(root.get("role"), role) : cb.conjunction()
            );
        });
    }

    public String updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Update the fields
            if (updatedUser.getFirstName() != null) existingUser.setFirstName(updatedUser.getFirstName());
            if (updatedUser.getLastName() != null) existingUser.setLastName(updatedUser.getLastName());
            if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
            if (updatedUser.getPhoneNumber() != null) existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            if (updatedUser.getRole() != null) existingUser.setRole(updatedUser.getRole());
            if (updatedUser.getPassword() != null) existingUser.setPassword(updatedUser.getPassword());
            if (updatedUser.getPreferences() != null) existingUser.setPreferences(updatedUser.getPreferences());
            if (updatedUser.getAccessibilityMode() != null) existingUser.setAccessibilityMode(updatedUser.getAccessibilityMode());
            if (updatedUser.getRsvpNotifications() != null) existingUser.setRsvpNotifications(updatedUser.getRsvpNotifications());
            if (updatedUser.getSmsEnabled() != null) existingUser.setSmsEnabled(updatedUser.getSmsEnabled());
            if (updatedUser.getEmailEnabled() != null) existingUser.setEmailEnabled(updatedUser.getEmailEnabled());

            // Update the timestamp for the update
            existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // Save updated user to the database
            userRepository.save(existingUser);
            return "User updated successfully";
        } else {
            return "User not found";
        }
    }
}
