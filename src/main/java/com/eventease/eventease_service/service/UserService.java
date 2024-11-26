package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.UserExistsException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User addUser(User user) {
        // check if user already exists
        if(userRepository.findById(user.getId()).isPresent()){
            throw new UserExistsException("User already exists");
        }

        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        User addedUser = userRepository.save(user);
        return addedUser;
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

            // Update the timestamp for the update
            existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // Save updated user to the database
            userRepository.save(existingUser);
            return "User updated successfully";
        } else {
            return "User not found";
        }
    }
  // This method retrieves a user by its ID. If the user is not found, it throws an exception
  public User findUserById(long id) {
      Optional<User> user = userRepository.findById(id);  // This line should be updated to use Optional
      if (!user.isPresent()) {
          throw new UserNotExistException("User is not found.");
      }
      return user.get();
  }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
  }

}
