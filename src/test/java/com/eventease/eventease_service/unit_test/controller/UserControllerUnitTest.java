package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
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

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
    }

    private int getTestUserId() {
        AtomicInteger TestUserId = new AtomicInteger(-1);

        userRepository.findAll().forEach(user -> {
            if (user.getFirstName().equals("John") && user.getLastName().equals("Doe")) {
                TestUserId.set(Math.toIntExact(user.getId()));
            }
        });

        assert(TestUserId.get() != -1);

        return TestUserId.get();
    }

    @Test
    @Order(1)
    public void testAddNewUser() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

//        doNothing().when(userService).addUser(any(User.class));

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User saved successfully"));

        // confirm user was added, and store its id
        this.getTestUserId();

//        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    @Order(2)
    public void testGetUsersByFilter() throws Exception {
//        when(userService.getUsersByFilter(anyString(), anyString(), anyString(), anyString(), any(User.Role.class)))
//                .thenReturn(new ArrayList<>());  // Assuming returning empty list

        mockMvc.perform(get("/api/users/list")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk());

//        verify(userService, times(1)).getUsersByFilter(eq("John"), eq("Doe"), isNull(), isNull(), isNull());
    }

    @Test
    @Order(3)
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        int TestUserId = this.getTestUserId();

//        when(userService.updateUser(eq(1L), any(User.class))).thenReturn("User updated successfully");

        mockMvc.perform(patch("/api/users/update/" + TestUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));

//        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @Order(4)
    public void testDeleteUser() throws Exception {
//        doNothing().when(userService).deleteUser(1L);

        int TestUserId = this.getTestUserId();

        mockMvc.perform(delete("/api/users/delete/" + TestUserId))
                .andExpect(status().isOk())  // Expecting status 200
                .andExpect(content().string("User deleted successfully"));  // Expecting success message

//        verify(userService, times(1)).deleteUser(1L);
    }

}
