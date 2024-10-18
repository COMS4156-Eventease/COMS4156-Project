package com.eventease.eventease_service.controller;


import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * The controller class for handling user management endpoints
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint for adding a new user
     * @param user The user object to be added
     * @return A message indicating the user was saved successfully
     */
    @PostMapping("/add")
    public String addNewUser(@RequestBody User user) {
        userService.addUser(user); // Assuming userService is handling the logic of saving the User object
        return "User saved successfully";
    }

    /**
     * Endpoint for getting a list of users
     * @return A list of all users
     */
    @GetMapping("/list")
    public Iterable<User> getUsersByFilter(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) User.Role role)
    {
        return userService.getUsersByFilter(firstName, lastName, email, phone, role);
    }

    /**
     * Endpoint for updating a user
     * @param id The ID of the user to be updated
     * @param user The updated user object
     * @return A message indicating the user was updated successfully
     */
    @PatchMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    /**
     * Endpoint for deleting a user
     * @param id The ID of the user to be deleted
     * @return A message indicating the user was deleted successfully
     */
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}
