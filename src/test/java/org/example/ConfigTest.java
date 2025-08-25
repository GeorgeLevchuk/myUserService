package org.example;

import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigTest {

    private static final Logger logger = LogManager.getLogger(ConfigTest.class);

    public static void main(String[] args) {
        try {
            logger.info("Testing Hibernate configuration...");

            // Получаем SessionFactory
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            logger.info("SessionFactory created successfully!");

            // Пробуем открыть сессию
            try (Session session = sessionFactory.openSession()) {
                logger.info("Session opened successfully!");

                // Простая проверка - выполняем простой SQL запрос
                Object result = session.createNativeQuery("SELECT 1").getSingleResult();
                logger.info("Database connection test passed: {}", result);

                logger.info("All tests passed! Configuration is correct.");
            }

        } catch (Exception e) {
            logger.error("Configuration test failed", e);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}