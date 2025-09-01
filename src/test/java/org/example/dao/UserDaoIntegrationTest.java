package org.example.dao;

import org.example.model.User;
import org.example.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("UserDao Integration Tests")
class UserDaoIntegrationTest extends BaseDaoTest {

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Given
        User user = new User("John Doe", "john@example.com", 30);

        // When
        User savedUser = userDao.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals(30, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should find user by id")
    void shouldFindUserById() {
        // Given
        User savedUser = createTestUser("test@example.com");

        // When
        Optional<User> foundUser = userDao.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("Test User", foundUser.get().getName());
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void shouldReturnEmptyWhenUserNotFoundById() {
        // When
        Optional<User> foundUser = userDao.findById(999L);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        // Given
        createTestUser("user1@example.com");
        createTestUser("user2@example.com");

        // When
        List<User> users = userDao.findAll();

        // Then
        assertEquals(2, users.size());
        assertThat(users, hasItem(hasProperty("email", equalTo("user1@example.com"))));
        assertThat(users, hasItem(hasProperty("email", equalTo("user2@example.com"))));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User savedUser = createTestUser("original@example.com");

        // When
        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@example.com");
        savedUser.setAge(35);
        User updatedUser = userDao.update(savedUser);

        // Then
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(35, updatedUser.getAge());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        User savedUser = createTestUser("delete@example.com");

        // When
        userDao.delete(savedUser.getId());

        // Then
        Optional<User> deletedUser = userDao.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        createTestUser("find@example.com");

        // When
        Optional<User> foundUser = userDao.findByEmail("find@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("find@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should find users by name pattern")
    void shouldFindUsersByNamePattern() {
        // Given
        User user1 = new User("Alice Smith", "alice@example.com", 25);
        User user2 = new User("Bob Smith", "bob@example.com", 30);
        userDao.save(user1);
        userDao.save(user2);

        // When
        List<User> users = userDao.findByName("Smith");

        // Then
        assertEquals(2, users.size());
        assertThat(users, hasItem(hasProperty("name", equalTo("Alice Smith"))));
        assertThat(users, hasItem(hasProperty("name", equalTo("Bob Smith"))));
    }

    @Test
    @DisplayName("Should throw exception when saving user with duplicate email")
    void shouldThrowExceptionWhenSavingUserWithDuplicateEmail() {
        // Given
        createTestUser("duplicate@example.com");

        // When & Then
        User duplicateUser = new User("Another User", "duplicate@example.com", 40);

        assertThrows(UserException.ConstraintViolationException.class, () -> {
            userDao.save(duplicateUser);
        });
    }

    @Test
    @DisplayName("Should handle transaction rollback on error")
    void shouldHandleTransactionRollbackOnError() {
        // Given
        User validUser = new User("Valid User", "valid@example.com", 25);

        // When & Then - Try to save user with invalid data that should cause rollback
        User invalidUser = new User("", "invalid-email", -5);

        assertThrows(Exception.class, () -> {
            userDao.save(invalidUser);
        });

        // Verify that valid user can still be saved (transaction was rolled back)
        User savedUser = userDao.save(validUser);
        assertNotNull(savedUser.getId());
    }
}