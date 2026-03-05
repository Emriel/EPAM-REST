package com.epam.springCoreTask.exception;

public class DuplicateAssignmentException extends RuntimeException {
    public DuplicateAssignmentException(String message) {
        super(message);
    }
}
