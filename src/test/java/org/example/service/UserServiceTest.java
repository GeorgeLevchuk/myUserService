package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.example.exception.UserException;
import org.example.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user successfully with valid data")
    void shouldCreateUserSuccessfully() {
        // Given
        User expectedUser = new User("John Doe", "john@example.com", 30);
        expectedUser.setId(1L);
        expectedUser.setCreatedAt(LocalDateTime.now());

        when(userDao.save(any(User.class))).thenReturn(expectedUser);

        // When
        User result = userService.createUser("John Doe", "john@example.com", 30);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw validation exception when creating user with invalid email")
    void shouldThrowValidationExceptionForInvalidEmail() {
        // When & Then
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("John Doe", "invalid-email", 30);
        });

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw validation exception when creating user with invalid age")
    void shouldThrowValidationExceptionForInvalidAge() {
        // When & Then
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("John Doe", "john@example.com", -5);
        });

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        User expectedUser = new User("John Doe", "john@example.com", 30);
        expectedUser.setId(1L);

        when(userDao.findById(1L)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("John Doe", result.get().getName());

        verify(userDao, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void shouldReturnEmptyWhenUserNotFoundById() {
        // Given
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(userDao, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw validation exception when getting user with invalid id")
    void shouldThrowValidationExceptionForInvalidId() {
        // When & Then
        assertThrows(UserException.ValidationException.class, () -> {
            userService.getUserById(-1L);
        });

        verify(userDao, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Given
        User user1 = new User("User 1", "user1@example.com", 25);
        User user2 = new User("User 2", "user2@example.com", 30);
        List<User> expectedUsers = List.of(user1, user2);

        when(userDao.findAll()).thenReturn(expectedUsers);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User existingUser = new User("Old Name", "old@example.com", 25);
        existingUser.setId(1L);

        User updatedUser = new User("New Name", "new@example.com", 30);
        updatedUser.setId(1L);

        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(1L, "New Name", "new@example.com", 30);

        // Then
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userDao, times(1)).findById(1L);
        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserException.EntityNotFoundException.class, () -> {
            userService.updateUser(999L, "New Name", "new@example.com", 30);
        });

        verify(userDao, times(1)).findById(999L);
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        doNothing().when(userDao).delete(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userDao, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void shouldGetUserByEmailSuccessfully() {
        // Given
        User expectedUser = new User("John Doe", "john@example.com", 30);
        expectedUser.setId(1L);

        when(userDao.findByEmail("john@example.com")).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> result = userService.getUserByEmail("john@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());

        verify(userDao, times(1)).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should search users by name successfully")
    void shouldSearchUsersByNameSuccessfully() {
        // Given
        User user1 = new User("Alice Smith", "alice@example.com", 25);
        User user2 = new User("Bob Smith", "bob@example.com", 30);
        List<User> expectedUsers = List.of(user1, user2);

        when(userDao.findByName("Smith")).thenReturn(expectedUsers);

        // When
        List<User> result = userService.searchUsersByName("Smith");

        // Then
        assertEquals(2, result.size());
        verify(userDao, times(1)).findByName("Smith");
    }

    @Test
    @DisplayName("Should propagate DAO exceptions")
    void shouldPropagateDaoExceptions() {
        // Given
        when(userDao.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        verify(userDao, times(1)).findAll();
    }
}