package org.example.dao;

import org.example.util.HibernateUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.example.model.User;

@Testcontainers
public abstract class BaseDaoTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    protected UserDao userDao;

    @BeforeAll
    static void beforeAll() {
        // Override Hibernate configuration for tests
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
        System.setProperty("hibernate.hbm2ddl.auto", "create");
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
        clearDatabase();
    }

    @AfterAll
    static void afterAll() {
        HibernateUtil.shutdown();
    }

    protected void clearDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            // Ignore if table doesn't exist yet
        }
    }

    protected User createTestUser(String email) {
        User user = new User("Test User", email, 25);
        return userDao.save(user);
    }
}