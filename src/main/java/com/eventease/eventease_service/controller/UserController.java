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


    @GetMapping("/all")
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
