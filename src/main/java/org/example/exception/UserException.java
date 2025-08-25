package org.example.exception;

public class UserException extends RuntimeException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    // Специфичные исключения для разных сценариев
    public static class DatabaseConnectionException extends UserException {
        public DatabaseConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ConstraintViolationException extends UserException {
        public ConstraintViolationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class EntityNotFoundException extends UserException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    public static class TransactionException extends UserException {
        public TransactionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ValidationException extends UserException {
        public ValidationException(String message) {
            super(message);
        }
    }
}