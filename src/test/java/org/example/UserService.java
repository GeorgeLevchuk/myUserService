package org.example;


import org.example.dao.UserDao;
import org.example.exception.UserException;
import org.example.model.User;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        validateUserData(name, email, age);

        User user = new User(name, email, age);
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new UserException.ValidationException("Invalid user ID");
        }
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        if (id == null || id <= 0) {
            throw new UserException.ValidationException("Invalid user ID");
        }

        validateUserData(name, email, age);

        User user = userDao.findById(id)
                .orElseThrow(() -> new UserException.EntityNotFoundException("User not found with ID: " + id));

        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        return userDao.update(user);
    }

    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new UserException.ValidationException("Invalid user ID");
        }
        userDao.delete(id);
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException.ValidationException("Email cannot be empty");
        }
        return userDao.findByEmail(email);
    }

    public List<User> searchUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserException.ValidationException("Name cannot be empty");
        }
        return userDao.findByName(name);
    }

    private void validateUserData(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserException.ValidationException("User name cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new UserException.ValidationException("Email cannot be empty");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new UserException.ValidationException("Invalid email format");
        }

        if (age == null || age <= 0 || age > 150) {
            throw new UserException.ValidationException("Age must be between 1 and 150");
        }
    }
}