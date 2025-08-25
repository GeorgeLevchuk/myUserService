package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;
import java.util.Optional;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            logger.info("Starting User Service Application");
            showMenu();
        } catch (Exception e) {
            logger.error("Application error", e);
            System.err.println("Application error: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
            logger.info("Application stopped");
        }
    }

    private static void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== User Service Menu ===");
            System.out.println("1. Create User");
            System.out.println("2. Find User by ID");
            System.out.println("3. Find All Users");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Find User by Email");
            System.out.println("7. Find Users by Name");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            try {
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
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                logger.error("Menu operation error", e);
            }
        }
    }

    private static void createUser() {
        System.out.println("\n--- Create User ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = new User(name, email, age);
        User savedUser = userDao.save(user);

        System.out.println("User created successfully: " + savedUser);
    }

    private static void findUserById() {
        System.out.println("\n--- Find User by ID ---");
        System.out.print("Enter user ID: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> user = userDao.findById(id);
        if (user.isPresent()) {
            System.out.println("User found: " + user.get());
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    private static void findAllUsers() {
        System.out.println("\n--- All Users ---");
        List<User> users = userDao.findAll();

        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(System.out::println);
            System.out.println("Total users: " + users.size());
        }
    }

    private static void updateUser() {
        System.out.println("\n--- Update User ---");
        System.out.print("Enter user ID to update: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isEmpty()) {
            System.out.println("User not found with ID: " + id);
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
        System.out.println("User updated successfully: " + updatedUser);
    }

    private static void deleteUser() {
        System.out.println("\n--- Delete User ---");
        System.out.print("Enter user ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> user = userDao.findById(id);
        if (user.isPresent()) {
            userDao.delete(id);
            System.out.println("User deleted successfully: " + user.get());
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    private static void findUserByEmail() {
        System.out.println("\n--- Find User by Email ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        Optional<User> user = userDao.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("User found: " + user.get());
        } else {
            System.out.println("User not found with email: " + email);
        }
    }

    private static void findUsersByName() {
        System.out.println("\n--- Find Users by Name ---");
        System.out.print("Enter name (or part of name): ");
        String name = scanner.nextLine();

        List<User> users = userDao.findByName(name);
        if (users.isEmpty()) {
            System.out.println("No users found with name containing: " + name);
        } else {
            users.forEach(System.out::println);
            System.out.println("Total users found: " + users.size());
        }
    }
}