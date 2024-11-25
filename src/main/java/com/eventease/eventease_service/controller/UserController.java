package com.eventease.eventease_service.controller;


import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try{
            user = userService.addUser(user);
            response.put("success", true);
            response.put("data", user);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotExistException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }  catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.findUserById(id);
            response.put("success", true);
            response.put("data", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotExistException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUsersByFilter(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) User.Role role)
    {
        Map<String, Object> response = new HashMap<>();
        try{
            Iterable<User> users = userService.getUsersByFilter(firstName, lastName, email, phone, role);
            response.put("success", true);
            response.put("data", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch ( Error error) {
            response.put("success", false);
            response.put("error", error.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try{
            userService.updateUser(id, user);
            response.put("success", true);
            response.put("data", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotExistException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try{
            userService.deleteUser(id);
            response.put("success", true);
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotExistException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
