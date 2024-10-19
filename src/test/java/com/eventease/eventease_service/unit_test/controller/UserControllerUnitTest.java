package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.UserService;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Unit tests for the UserController class.
 */

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;


    /**
     * Set up the test environment.
     */
    @BeforeEach
    public void setup() {
    }

    private int getTestUserId() {
        AtomicInteger TestUserId = new AtomicInteger(-1);

        userService.getUsersByFilter("John", "Doe", null, null, null).forEach(u -> {
            TestUserId.set(u.getId().intValue());
        });

        assert(TestUserId.get() != -1);

        return TestUserId.get();
    }

    /**
     * Test for adding a new user successfully.
     */
    @Test
    @Order(1)
    public void testAddNewUser() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User saved successfully"));

        // confirm user was added, and store its id
        this.getTestUserId();
    }

    /**
     * Test for getting a user by ID successfully.
     */
    @Test
    @Order(2)
    public void testGetUsersByFilter() throws Exception {
        mockMvc.perform(get("/api/users/list")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk());

    }

    /**
     * Test for getting a user by ID successfully.
     */
    @Test
    @Order(3)
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        int TestUserId = this.getTestUserId();

        mockMvc.perform(patch("/api/users/update/" + TestUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));

    }

    /**
     * Test for deleting a user by ID successfully.
     */
    @Test
    @Order(4)
    public void testDeleteUser() throws Exception {
        int TestUserId = this.getTestUserId();

        mockMvc.perform(delete("/api/users/delete/" + TestUserId))
                .andExpect(status().isOk())  // Expecting status 200
                .andExpect(content().string("User deleted successfully"));  // Expecting success message
    }

}
