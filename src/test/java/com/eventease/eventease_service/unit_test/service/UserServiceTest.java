package com.eventease.eventease_service.unit_test.service;

import com.eventease.eventease_service.exception.UserExistsException;
import com.eventease.eventease_service.exception.UserNotExistException;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.UserRepository;
import com.eventease.eventease_service.service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhoneNumber("1234567890");
        testUser.setRole(User.Role.ELDERLY);
        testUser.setPassword("password");
    }

    @Test
    public void testAddUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.addUser(testUser);

        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void testAddUser_UserExists() {
        when(userRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(testUser));

        assertThrows(UserExistsException.class, () -> userService.addUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetUsersByFilter_AllParameters() {
        userService.getUsersByFilter("John", "Doe", "john@example.com", "1234567890", User.Role.ELDERLY);
        verify(userRepository, times(1)).findAll(any());
    }

    @Test
    public void testGetUsersByFilter_NullParameters() {
        userService.getUsersByFilter(null, null, null, null, null);
        verify(userRepository, times(1)).findAll(any());
    }

    @Test
    public void testUpdateUser_Success() {
        User updatedUser = new User();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("jane.smith@example.com");
        updatedUser.setPhoneNumber("0987654321");
        updatedUser.setRole(User.Role.CAREGIVER);
        updatedUser.setPassword("newpassword");

        when(userRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = userService.updateUser(1L, updatedUser);

        assertEquals("User updated successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_PartialUpdate() {
        User partialUpdate = new User();
        partialUpdate.setFirstName("Jane");

        when(userRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = userService.updateUser(1L, partialUpdate);

        assertEquals("User updated successfully", result);
        assertEquals("Jane", testUser.getFirstName());
        assertEquals("Doe", testUser.getLastName()); // Original value should remain
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        String result = userService.updateUser(1L, testUser);

        assertEquals("User not found", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testFindUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    public void testFindUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotExistException.class, () -> userService.findUserById(1L));
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        doThrow(new UserNotExistException("User not found")).when(userRepository).deleteById(1L);

        assertThrows(UserNotExistException.class, () -> userService.deleteUser(1L));
    }

    @Test
    public void testGetUsersByFilter_AllParametersPresent() {
        // Capture the Specification passed to findAll
        userService.getUsersByFilter("John", "Doe", "john@example.com", "1234567890", User.Role.ELDERLY);

        verify(userRepository).findAll(argThat(specification -> {
            Root<User> root = mock(Root.class);
            CriteriaQuery<?> query = mock(CriteriaQuery.class);
            CriteriaBuilder cb = mock(CriteriaBuilder.class);

            // Mock the behavior of getting path
            when(root.get("firstName")).thenReturn(mock(Path.class));
            when(root.get("lastName")).thenReturn(mock(Path.class));
            when(root.get("email")).thenReturn(mock(Path.class));
            when(root.get("phoneNumber")).thenReturn(mock(Path.class));
            when(root.get("role")).thenReturn(mock(Path.class));

            // Mock the behavior of criteria builder
            when(cb.equal(any(), any())).thenReturn(mock(Predicate.class));
            when(cb.and(any())).thenReturn(mock(Predicate.class));

            specification.toPredicate(root, query, cb);

            // Verify all fields are used in the predicate
            verify(root).get("firstName");
            verify(root).get("lastName");
            verify(root).get("email");
            verify(root).get("phoneNumber");
            verify(root).get("role");

            return true;
        }));
    }

    @Test
    public void testGetUsersByFilter_AllParametersNull() {
        userService.getUsersByFilter(null, null, null, null, null);

        verify(userRepository).findAll(argThat(specification -> {
            Root<User> root = mock(Root.class);
            CriteriaQuery<?> query = mock(CriteriaQuery.class);
            CriteriaBuilder cb = mock(CriteriaBuilder.class);

            when(cb.conjunction()).thenReturn(mock(Predicate.class));
            when(cb.and(any())).thenReturn(mock(Predicate.class));

            specification.toPredicate(root, query, cb);

            // Verify conjunction is called for each null parameter
            verify(cb, times(5)).conjunction();
            verify(root, never()).get(anyString());

            return true;
        }));
    }

    @Test
    public void testGetUsersByFilter_MixedParameters() {
        userService.getUsersByFilter("John", null, "john@example.com", null, User.Role.ELDERLY);

        verify(userRepository).findAll(argThat(specification -> {
            Root<User> root = mock(Root.class);
            CriteriaQuery<?> query = mock(CriteriaQuery.class);
            CriteriaBuilder cb = mock(CriteriaBuilder.class);

            // Mock the behavior of getting path
            when(root.get("firstName")).thenReturn(mock(Path.class));
            when(root.get("email")).thenReturn(mock(Path.class));
            when(root.get("role")).thenReturn(mock(Path.class));

            // Mock the behavior of criteria builder
            when(cb.equal(any(), any())).thenReturn(mock(Predicate.class));
            when(cb.conjunction()).thenReturn(mock(Predicate.class));
            when(cb.and(any())).thenReturn(mock(Predicate.class));

            specification.toPredicate(root, query, cb);

            // Verify only non-null fields are used
            verify(root).get("firstName");
            verify(root, never()).get("lastName");
            verify(root).get("email");
            verify(root, never()).get("phoneNumber");
            verify(root).get("role");

            // Verify conjunction is called for null parameters
            verify(cb, times(2)).conjunction();

            return true;
        }));
    }

    @Test
    public void testGetUsersByFilter_SingleParameter() {
        userService.getUsersByFilter("John", null, null, null, null);

        verify(userRepository).findAll(argThat(specification -> {
            Root<User> root = mock(Root.class);
            CriteriaQuery<?> query = mock(CriteriaQuery.class);
            CriteriaBuilder cb = mock(CriteriaBuilder.class);

            when(root.get("firstName")).thenReturn(mock(Path.class));
            when(cb.equal(any(), any())).thenReturn(mock(Predicate.class));
            when(cb.conjunction()).thenReturn(mock(Predicate.class));
            when(cb.and(any())).thenReturn(mock(Predicate.class));

            specification.toPredicate(root, query, cb);

            // Verify only firstName is used
            verify(root).get("firstName");
            verify(root, never()).get("lastName");
            verify(root, never()).get("email");
            verify(root, never()).get("phoneNumber");
            verify(root, never()).get("role");

            // Verify conjunction is called for other parameters
            verify(cb, times(4)).conjunction();

            return true;
        }));
    }
}