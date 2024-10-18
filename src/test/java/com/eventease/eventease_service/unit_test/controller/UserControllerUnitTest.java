package com.eventease.eventease_service.unit_test.controller;

import com.eventease.eventease_service.controller.UserController;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerUnitTest {

    private MockMvc mockMvc;

   
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testAddNewUser() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        doNothing().when(userService).addUser(any(User.class));

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User saved successfully"));

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    public void testGetUsersByFilter() throws Exception {
        when(userService.getUsersByFilter(anyString(), anyString(), anyString(), anyString(), any(User.Role.class)))
                .thenReturn(new ArrayList<>());  // Assuming returning empty list

        mockMvc.perform(get("/api/users/list")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsersByFilter(eq("John"), eq("Doe"), isNull(), isNull(), isNull());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn("User updated successfully");

        mockMvc.perform(patch("/api/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/delete/1"))
                .andExpect(status().isOk())  // Expecting status 200
                .andExpect(content().string("User deleted successfully"));  // Expecting success message

        verify(userService, times(1)).deleteUser(1L);
    }

}
