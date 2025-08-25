package org.example.dao;

import org.example.model.User;
import org.example.exception.UserException;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.exception.DataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        validateUser(user);
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();

            logger.info("User saved successfully: {}", user.getEmail());
            return user;

        } catch (ConstraintViolationException e) {
            handleConstraintViolation(transaction, "save", user.getEmail(), e);
            throw new UserException.ConstraintViolationException(
                    "User with email '" + user.getEmail() + "' already exists", e);
        } catch (DataException e) {
            handleDataException(transaction, "save", user.getEmail(), e);
            throw new UserException.ValidationException("Invalid data format: " + e.getMessage());
        } catch (Exception e) {
            handleGenericException(transaction, "save", user.getEmail(), e);
            throw new UserException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        validateId(id);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            logger.debug("User found by id {}: {}", id, user != null);
            return Optional.ofNullable(user);

        } catch (Exception e) {
            logger.error("Failed to find user by id: {}", id, e);
            throw new UserException("Failed to find user by id: " + id, e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User order by id", User.class);
            List<User> users = query.list();
            logger.debug("Found {} users", users.size());
            return users;

        } catch (SQLGrammarException e) {
            logger.error("SQL syntax error in findAll", e);
            throw new UserException("Database query error", e);
        } catch (Exception e) {
            logger.error("Failed to find all users", e);
            throw new UserException("Failed to retrieve users: " + e.getMessage(), e);
        }
    }

    @Override
    public User update(User user) {
        validateUser(user);
        validateId(user.getId());
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Проверяем существование пользователя
            User existingUser = session.get(User.class, user.getId());
            if (existingUser == null) {
                throw new UserException.EntityNotFoundException(
                        "User not found with ID: " + user.getId());
            }

            session.update(user);
            transaction.commit();

            logger.info("User updated successfully: {}", user.getEmail());
            return user;

        } catch (UserException.EntityNotFoundException e) {
            rollbackTransaction(transaction, "update");
            throw e;
        } catch (ConstraintViolationException e) {
            handleConstraintViolation(transaction, "update", user.getEmail(), e);
            throw new UserException.ConstraintViolationException(
                    "Email '" + user.getEmail() + "' already exists", e);
        } catch (Exception e) {
            handleGenericException(transaction, "update", user.getEmail(), e);
            throw new UserException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);

            if (user != null) {
                session.delete(user);
                logger.info("User deleted successfully: {}", id);
            } else {
                logger.warn("User not found for deletion: {}", id);
                throw new UserException.EntityNotFoundException("User not found with ID: " + id);
            }

            transaction.commit();

        } catch (UserException.EntityNotFoundException e) {
            rollbackTransaction(transaction, "delete");
            throw e;
        } catch (Exception e) {
            handleGenericException(transaction, "delete", String.valueOf(id), e);
            throw new UserException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        validateEmail(email);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();

            logger.debug("User found by email {}: {}", email, user != null);
            return Optional.ofNullable(user);

        } catch (Exception e) {
            logger.error("Failed to find user by email: {}", email, e);
            throw new UserException("Failed to find user by email: " + email, e);
        }
    }

    @Override
    public List<User> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserException.ValidationException("Name cannot be empty");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "from User where lower(name) like lower(:name) order by name", User.class);
            query.setParameter("name", "%" + name.trim() + "%");

            List<User> users = query.list();
            logger.debug("Found {} users with name containing: {}", users.size(), name);
            return users;

        } catch (Exception e) {
            logger.error("Failed to find users by name: {}", name, e);
            throw new UserException("Failed to find users by name: " + name, e);
        }
    }

    // Валидационные методы
    private void validateUser(User user) {
        if (user == null) {
            throw new UserException.ValidationException("User cannot be null");
        }
        validateEmail(user.getEmail());

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new UserException.ValidationException("User name cannot be empty");
        }

        if (user.getAge() == null || user.getAge() <= 0 || user.getAge() > 150) {
            throw new UserException.ValidationException("Age must be between 1 and 150");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new UserException.ValidationException("Invalid user ID");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException.ValidationException("Email cannot be empty");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new UserException.ValidationException("Invalid email format");
        }
    }

    // Методы обработки исключений
    private void handleConstraintViolation(Transaction transaction, String operation,
                                           String identifier, ConstraintViolationException e) {
        rollbackTransaction(transaction, operation);
        logger.error("Constraint violation during {} operation for {}: {}",
                operation, identifier, e.getConstraintName(), e);
    }

    private void handleDataException(Transaction transaction, String operation,
                                     String identifier, DataException e) {
        rollbackTransaction(transaction, operation);
        logger.error("Data exception during {} operation for {}: {}",
                operation, identifier, e.getMessage(), e);
    }

    private void handleGenericException(Transaction transaction, String operation,
                                        String identifier, Exception e) {
        rollbackTransaction(transaction, operation);
        logger.error("Error during {} operation for {}: {}",
                operation, identifier, e.getMessage(), e);
    }

    private void rollbackTransaction(Transaction transaction, String operation) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
                logger.warn("Transaction rolled back for {} operation", operation);
            } catch (Exception rollbackEx) {
                logger.error("Failed to rollback transaction for {} operation",
                        operation, rollbackEx);
            }
        }
    }

    // Дополнительные методы для управления соединением
    public boolean testConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery("SELECT 1").uniqueResult() != null;
        } catch (Exception e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
}