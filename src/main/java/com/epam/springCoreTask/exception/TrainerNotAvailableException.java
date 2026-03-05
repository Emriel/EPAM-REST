package com.epam.springCoreTask.exception;

public class TrainerNotAvailableException extends RuntimeException {
    public TrainerNotAvailableException(String message) {
        super(message);
    }
}
