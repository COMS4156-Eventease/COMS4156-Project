package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
import com.eventease.eventease_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        userService.addUser(user);

        assertNotNull(user.getCreatedAt());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetUsersByFilter() {
        userService.getUsersByFilter("John", "Doe", null, null, null);

        // Verify if the repository findAll method is called
        verify(userRepository, times(1)).findAll(any());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        // Assuming findById returns null when the user is not found
        when(userRepository.findById(1L)).thenReturn(null);

        String result = userService.updateUser(1L, new User());

        assertEquals("User not found", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testFindUserById_UserExists() {
        User user = new User();
        user.setId(1L);
        Optional<User> userOptional = Optional.of(user);
        when(userRepository.findById(1L)).thenReturn(userOptional);

        User result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testFindUserById_UserNotFound() {
        // Assuming findById returns null when the user is not found
        when(userRepository.findById(1L)).thenThrow(new UserNotExistException("User not found"));

        assertThrows(UserNotExistException.class, () -> {
            userService.findUserById(1L);
        });
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
