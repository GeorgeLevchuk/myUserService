package org.example.service;

import org.example.exception.UserException;
import org.example.UserService;
import org.example.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Validation Tests")
class UserServiceValidationTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should validate null name")
    void shouldValidateNullName() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser(null, "test@example.com", 25);
        });
    }

    @Test
    @DisplayName("Should validate empty name")
    void shouldValidateEmptyName() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("", "test@example.com", 25);
        });
    }

    @Test
    @DisplayName("Should validate null email")
    void shouldValidateNullEmail() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("Test User", null, 25);
        });
    }

    @Test
    @DisplayName("Should validate empty email")
    void shouldValidateEmptyEmail() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("Test User", "", 25);
        });
    }

    @Test
    @DisplayName("Should validate invalid email format")
    void shouldValidateInvalidEmailFormat() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("Test User", "invalid-email", 25);
        });
    }

    @Test
    @DisplayName("Should validate null age")
    void shouldValidateNullAge() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("Test User", "test@example.com", null);
        });
    }

    @Test
    @DisplayName("Should validate zero age")
    void shouldValidateZeroAge() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("Test User", "test@example.com", 0);
        });
    }

    @Test
    @DisplayName("Should validate negative age")
    void shouldValidateNegativeAge() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("Test User", "test@example.com", -5);
        });
    }

    @Test
    @DisplayName("Should validate too high age")
    void shouldValidateTooHighAge() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.createUser("Test User", "test@example.com", 151);
        });
    }

    @Test
    @DisplayName("Should validate invalid user id for get")
    void shouldValidateInvalidUserIdForGet() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.getUserById(-1L);
        });
    }

    @Test
    @DisplayName("Should validate invalid user id for update")
    void shouldValidateInvalidUserIdForUpdate() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.updateUser(-1L, "Name", "email@example.com", 25);
        });
    }

    @Test
    @DisplayName("Should validate invalid user id for delete")
    void shouldValidateInvalidUserIdForDelete() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.deleteUser(-1L);
        });
    }

    @Test
    @DisplayName("Should validate empty name for search")
    void shouldValidateEmptyNameForSearch() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.searchUsersByName("");
        });
    }

    @Test
    @DisplayName("Should validate null name for search")
    void shouldValidateNullNameForSearch() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.searchUsersByName(null);
        });
    }

    @Test
    @DisplayName("Should validate empty email for search")
    void shouldValidateEmptyEmailForSearch() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.getUserByEmail("");
        });
    }

    @Test
    @DisplayName("Should validate null email for search")
    void shouldValidateNullEmailForSearch() {
        assertThrows(UserException.ValidationException.class, () -> {
            userService.getUserByEmail(null);
        });
    }
}