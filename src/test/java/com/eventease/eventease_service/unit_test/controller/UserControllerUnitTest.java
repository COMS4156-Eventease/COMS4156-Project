package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.UserController;
import com.eventease.eventease_service.exception.UserNotExistException;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Unit tests for the UserController class.
 */

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        // confirm user was added, and store its id
        this.getTestUserId();
    }

    /**
     * Test for getting a user by ID successfully.
     */
    @Test
    @Order(2)
    public void testGetUserById() throws Exception {
        int testUserId = this.getTestUserId(); // Assuming a user with this ID exists

        mockMvc.perform(get("/api/users/" + testUserId))
                .andExpect(status().isOk()); // Expecting HTTP 200 OK
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
                .andExpect(status().isOk());

    }

    /**
     * Test for deleting a user by ID successfully.
     */
    @Test
    @Order(4)
    void testDeleteUserSuccess() throws Exception {
        Long testUserId = 1L;
        doNothing().when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/users/delete/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @Order(5)
    void testDeleteUserNotExist() throws Exception {
        Long testUserId = 1L;

        doThrow(new UserNotExistException("User does not exist")).when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/users/delete/" + testUserId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("User does not exist"));
    }

    @Test
    @Order(6)
    void testDeleteUserInternalServerError() throws Exception {
        Long testUserId = 1L;

        doThrow(new RuntimeException("Unexpected error"))
                .when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/users/delete/" + testUserId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Unexpected error"));
    }
}

