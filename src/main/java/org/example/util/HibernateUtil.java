package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateUtil {

    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try {
            logger.info("Initializing Hibernate from hibernate.cfg.xml...");

            // Создаем StandardServiceRegistry из конфигурационного файла
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml") // Ищет в classpath
                    .build();

            Metadata metadata = new MetadataSources(standardRegistry)
                    .getMetadataBuilder()
                    .build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();
            logger.info("Hibernate SessionFactory created successfully from hibernate.cfg.xml");

        } catch (Exception e) {
            logger.error("Failed to create Hibernate SessionFactory from hibernate.cfg.xml", e);
            System.err.println("Error: Could not load hibernate.cfg.xml from classpath");
            System.err.println("Make sure the file is in src/main/resources/ directory");
            throw new ExceptionInInitializerError("Could not initialize Hibernate: " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("Hibernate SessionFactory is not initialized");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }

    // Метод для проверки, что конфигурационный файл загружается
    public static void testConfigLoading() {
        try {
            // Попытка загрузить ресурс напрямую
            ClassLoader classLoader = HibernateUtil.class.getClassLoader();
            java.net.URL configUrl = classLoader.getResource("hibernate.cfg.xml");

            if (configUrl != null) {
                logger.info("Found hibernate.cfg.xml at: {}", configUrl.getPath());
            } else {
                logger.error("hibernate.cfg.xml not found in classpath!");
                throw new RuntimeException("hibernate.cfg.xml not found in classpath");
            }

        } catch (Exception e) {
            logger.error("Error testing config loading", e);
            throw e;
        }
    }
}