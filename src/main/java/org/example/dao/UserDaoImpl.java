package org.example.dao;

import org.example.model.User;
import org.example.exception.UserException;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            logger.info("User saved successfully: {}", user.getEmail());
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to save user: {}", user.getEmail(), e);
            throw new UserException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            logger.debug("User found by id {}: {}", id, user != null);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Failed to find user by id: {}", id, e);
            throw new UserException("Failed to find user by id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            List<User> users = query.list();
            logger.debug("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Failed to find all users", e);
            throw new UserException("Failed to find all users: " + e.getMessage(), e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
            logger.info("User updated successfully: {}", user.getEmail());
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to update user: {}", user.getEmail(), e);
            throw new UserException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                logger.info("User deleted successfully: {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to delete user: {}", id, e);
            throw new UserException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            logger.debug("User found by email {}: {}", email, user != null);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Failed to find user by email: {}", email, e);
            throw new UserException("Failed to find user by email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User where name like :name", User.class);
            query.setParameter("name", "%" + name + "%");
            List<User> users = query.list();
            logger.debug("Found {} users with name containing: {}", users.size(), name);
            return users;
        } catch (Exception e) {
            logger.error("Failed to find users by name: {}", name, e);
            throw new UserException("Failed to find users by name: " + e.getMessage(), e);
        }
    }
}