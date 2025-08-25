package org.example.util;

import org.example.exception.UserException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateUtil {

    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        initializeSessionFactory();
    }

    private static void initializeSessionFactory() {
        try {
            logger.info("Initializing Hibernate SessionFactory...");

            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();

            Metadata metadata = new MetadataSources(standardRegistry)
                    .getMetadataBuilder()
                    .build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();
            logger.info("Hibernate SessionFactory created successfully");

        } catch (ServiceException e) {
            handleServiceException(e);
        } catch (Exception e) {
            handleGenericException(e);
        }
    }

    private static void handleServiceException(ServiceException e) {
        String errorMessage = "Hibernate service configuration error: " + e.getMessage();
        logger.error(errorMessage, e);

        // Проверяем специфичные ошибки подключения к БД
        if (e.getMessage().contains("Connection") || e.getMessage().contains("JDBC")) {
            throw new UserException.DatabaseConnectionException(
                    "Cannot connect to database. Please check: " +
                            "\n1. Is PostgreSQL running?" +
                            "\n2. Is database 'userdb' created?" +
                            "\n3. Are connection settings correct in hibernate.cfg.xml?", e);
        }

        throw new UserException(errorMessage, e);
    }

    private static void handleGenericException(Exception e) {
        String errorMessage = "Failed to initialize Hibernate: " + e.getMessage();
        logger.error(errorMessage, e);

        // Обработка специфичных ошибок конфигурации
        if (e.getMessage().contains("cfg.xml")) {
            throw new UserException(
                    "Hibernate configuration file not found. " +
                            "Make sure hibernate.cfg.xml is in src/main/resources/", e);
        }

        throw new UserException(errorMessage, e);
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new UserException("Hibernate SessionFactory is not initialized or closed");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        try {
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
                logger.info("Hibernate SessionFactory closed successfully");
            }
        } catch (Exception e) {
            logger.error("Error closing Hibernate SessionFactory", e);
            throw new UserException("Failed to shutdown Hibernate", e);
        }
    }

    public static boolean isConnected() {
        try {
            return sessionFactory != null &&
                    !sessionFactory.isClosed() &&
                    sessionFactory.openSession().isConnected();
        } catch (Exception e) {
            logger.warn("Database connection check failed", e);
            return false;
        }
    }
}