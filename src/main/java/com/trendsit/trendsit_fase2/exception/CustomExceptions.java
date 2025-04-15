package com.trendsit.trendsit_fase2.exception;

public class CustomExceptions {
    public static class InvalidRoleException extends RuntimeException {
        public InvalidRoleException(String message) {
            super(message);
        }
    }

    public class DirectoryNotEmptyException extends RuntimeException {
        public DirectoryNotEmptyException(String message) {
            super(message);
        }
    }

    public class ProfessorNotFoundException extends EntityNotFoundException {
        public ProfessorNotFoundException(String message) {
            super(message);
        }
    }

    public class StudentNotInDirectoryException extends RuntimeException {
        public StudentNotInDirectoryException(String message) {
            super(message);
        }
    }

    public class InvalidDirectoryOperationException extends RuntimeException {
        public InvalidDirectoryOperationException(String message) {
            super(message);
        }
    }
}
