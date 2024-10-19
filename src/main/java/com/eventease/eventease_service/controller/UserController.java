package com.eventease.eventease_service.controller;


import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public String addNewUser(@RequestBody User user) {
        userService.addUser(user); // Assuming userService is handling the logic of saving the User object
        return "User saved successfully";
    }

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


    @PatchMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}
