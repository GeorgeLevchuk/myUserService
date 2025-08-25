package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.example.exception.UserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;
import java.util.Optional;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static UserDao userDao;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            logger.info("Starting User Service Application");

            // Проверяем подключение к базе данных
            if (!checkDatabaseConnection()) {
                System.exit(1);
            }

            // Инициализируем DAO
            userDao = new UserDaoImpl();

            showMenu();

        } catch (UserException.DatabaseConnectionException e) {
            handleDatabaseConnectionError(e);
        } catch (Exception e) {
            handleUnexpectedError(e);
        } finally {
            shutdownApplication();
        }
    }

    private static boolean checkDatabaseConnection() {
        try {
            logger.info("Checking database connection...");
            userDao = new UserDaoImpl();

            // Простая проверка подключения
            userDao.findAll();
            logger.info("Database connection established successfully");
            return true;

        } catch (UserException.DatabaseConnectionException e) {
            logger.error("Database connection failed", e);
            System.err.println("❌ Database Connection Error:");
            System.err.println(e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during connection test", e);
            System.err.println("❌ Unexpected error: " + e.getMessage());
            return false;
        }
    }

    private static void showMenu() {
        boolean running = true;

        while (running) {
            try {
                System.out.println("\n=== User Service Menu ===");
                System.out.println("1. Create User");
                System.out.println("2. Find User by ID");
                System.out.println("3. Find All Users");
                System.out.println("4. Update User");
                System.out.println("5. Delete User");
                System.out.println("6. Find User by Email");
                System.out.println("7. Find Users by Name");
                System.out.println("8. Test Database Connection");
                System.out.println("9. Exit");
                System.out.print("Choose an option: ");

                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        findUserById();
                        break;
                    case 3:
                        findAllUsers();
                        break;
                    case 4:
                        updateUser();
                        break;
                    case 5:
                        deleteUser();
                        break;
                    case 6:
                        findUserByEmail();
                        break;
                    case 7:
                        findUsersByName();
                        break;
                    case 8:
                        testDatabaseConnection();
                        break;
                    case 9:
                        running = false;
                        System.out.println("👋 Goodbye!");
                        break;
                    default:
                        System.out.println("❌ Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            } catch (UserException e) {
                handleUserException(e);
            } catch (Exception e) {
                handleUnexpectedError(e);
            }
        }
    }

    private static void createUser() {
        try {
            System.out.println("\n--- Create User ---");
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter age: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = new User(name, email, age);
            User savedUser = userDao.save(user);

            System.out.println("✅ User created successfully: " + savedUser);

        } catch (UserException.ValidationException e) {
            System.out.println("❌ Validation error: " + e.getMessage());
        } catch (UserException.ConstraintViolationException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number for age.");
        }
    }

    private static void findUserById() {
        try {
            System.out.println("\n--- Find User by ID ---");
            System.out.print("Enter user ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                System.out.println("✅ User found: " + user.get());
            } else {
                System.out.println("❌ User not found with ID: " + id);
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number for ID.");
        } catch (UserException.ValidationException e) {
            System.out.println("❌ Validation error: " + e.getMessage());
        }
    }

    private static void findAllUsers() {
        try {
            System.out.println("\n--- All Users ---");
            List<User> users = userDao.findAll();

            if (users.isEmpty()) {
                System.out.println("ℹ️ No users found.");
            } else {
                users.forEach(user -> System.out.println("👤 " + user));
                System.out.println("📊 Total users: " + users.size());
            }

        } catch (UserException e) {
            System.out.println("❌ Error retrieving users: " + e.getMessage());
        }
    }

    private static void updateUser() {
        try {
            System.out.println("\n--- Update User ---");
            System.out.print("Enter user ID to update: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> userOpt = userDao.findById(id);
            if (userOpt.isEmpty()) {
                System.out.println("❌ User not found with ID: " + id);
                return;
            }

            User user = userOpt.get();
            System.out.println("Current user: " + user);

            System.out.print("Enter new name (current: " + user.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                user.setName(name);
            }

            System.out.print("Enter new email (current: " + user.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.trim().isEmpty()) {
                user.setEmail(email);
            }

            System.out.print("Enter new age (current: " + user.getAge() + "): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.trim().isEmpty()) {
                user.setAge(Integer.parseInt(ageInput));
            }

            User updatedUser = userDao.update(user);
            System.out.println("✅ User updated successfully: " + updatedUser);

        } catch (UserException.EntityNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (UserException.ConstraintViolationException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private static void deleteUser() {
        try {
            System.out.println("\n--- Delete User ---");
            System.out.print("Enter user ID to delete: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Are you sure you want to delete user with ID " + id + "? (yes/no): ");
            String confirmation = scanner.nextLine();

            if ("yes".equalsIgnoreCase(confirmation)) {
                userDao.delete(id);
                System.out.println("✅ User deleted successfully");
            } else {
                System.out.println("❌ Deletion cancelled");
            }

        } catch (UserException.EntityNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number for ID.");
        }
    }

    private static void findUserByEmail() {
        try {
            System.out.println("\n--- Find User by Email ---");
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            Optional<User> user = userDao.findByEmail(email);
            if (user.isPresent()) {
                System.out.println("✅ User found: " + user.get());
            } else {
                System.out.println("❌ User not found with email: " + email);
            }

        } catch (UserException.ValidationException e) {
            System.out.println("❌ Validation error: " + e.getMessage());
        }
    }

    private static void findUsersByName() {
        try {
            System.out.println("\n--- Find Users by Name ---");
            System.out.print("Enter name (or part of name): ");
            String name = scanner.nextLine().trim();

            List<User> users = userDao.findByName(name);
            if (users.isEmpty()) {
                System.out.println("❌ No users found with name containing: " + name);
            } else {
                users.forEach(user -> System.out.println("👤 " + user));
                System.out.println("📊 Total users found: " + users.size());
            }

        } catch (UserException.ValidationException e) {
            System.out.println("❌ Validation error: " + e.getMessage());
        }
    }

    private static void testDatabaseConnection() {
        try {
            System.out.println("\n--- Test Database Connection ---");
            boolean isConnected = ((UserDaoImpl) userDao).testConnection();

            if (isConnected) {
                System.out.println("✅ Database connection is active and working");
            } else {
                System.out.println("❌ Database connection test failed");
            }

        } catch (Exception e) {
            System.out.println("❌ Connection test error: " + e.getMessage());
        }
    }

    private static void handleUserException(UserException e) {
        logger.error("User operation failed", e);
        System.out.println("❌ Operation failed: " + e.getMessage());
    }

    private static void handleDatabaseConnectionError(UserException.DatabaseConnectionException e) {
        logger.error("Database connection error", e);
        System.err.println("\n❌ CRITICAL: Database Connection Failed");
        System.err.println("Please check:");
        System.err.println("1. 📋 Is PostgreSQL running?");
        System.err.println("2. 🗄️ Is database 'userdb' created?");
        System.err.println("3. 🔧 Are connection settings correct in hibernate.cfg.xml?");
        System.err.println("4. 👤 Are username and password correct?");
        System.err.println("\nError details: " + e.getMessage());
    }

    private static void handleUnexpectedError(Exception e) {
        logger.error("Unexpected application error", e);
        System.err.println("❌ Unexpected error occurred: " + e.getMessage());
        System.err.println("Please check the logs for more details.");
    }

    private static void shutdownApplication() {
        try {
            HibernateUtil.shutdown();
            scanner.close();
            logger.info("Application stopped gracefully");
        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
    }
}