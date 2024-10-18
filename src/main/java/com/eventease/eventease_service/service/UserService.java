package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * Service class for managing users in the system.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Adds a new user to the system.
     *
     * @param user The user to be added.
     */
    public void addUser(User user) {
        // Set any additional logic if necessary (e.g., default values)
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Save the user to the database
        userRepository.save(user);
    }

    /**
     * Gets a filtered list of users based on the provided optional parameters.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @param email     The email of the user.
     * @param phone     The phone number of the user.
     * @param role      The role of the user.
     * @return An iterable list of users that match the filter criteria.
     */
    public Iterable<User> getUsersByFilter(
            String firstName, String lastName, String email,
            String phone, User.Role role) {
        return userRepository.findAll((root, query, cb) -> {
            // Create a predicate for filtering based on the provided parameters
            return cb.and(
                    firstName != null ?
                            cb.equal(root.get("firstName"), firstName) :
                            cb.conjunction(),
                    lastName != null ?
                            cb.equal(root.get("lastName"), lastName) :
                            cb.conjunction(),
                    email != null ? cb.equal(root.get("email"), email) :
                            cb.conjunction(),
                    phone != null ? cb.equal(root.get("phoneNumber"), phone) :
                            cb.conjunction(),
                    role != null ? cb.equal(root.get("role"), role) :
                            cb.conjunction()
            );
        });
    }

    /**
     * Updates an existing user with the provided information.
     *
     * @param id          The ID of the user to update.
     * @param updatedUser The updated user information.
     * @return A message indicating the status of the update.
     */
    public String updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Update the fields
            if (updatedUser.getFirstName() != null) {
                existingUser.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                existingUser.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            if (updatedUser.getRole() != null) {
                existingUser.setRole(updatedUser.getRole());
            }
            if (updatedUser.getPassword() != null) {
                existingUser.setPassword(updatedUser.getPassword());
            }

            // Update the timestamp for the update
            existingUser.setUpdatedAt(
                    new Timestamp(System.currentTimeMillis()));

            // Save updated user to the database
            userRepository.save(existingUser);
            return "User updated successfully";
        } else {
            return "User not found";
        }
    }

    /**
     * This method retrieves a user by its ID. If the user is not found, it throws an exception.
     *
     * @param id The ID of the user to find.
     * @return The user with the given ID.
     */
    public User findUserById(long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotExistException("User is not found.");
        }
        return user;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
