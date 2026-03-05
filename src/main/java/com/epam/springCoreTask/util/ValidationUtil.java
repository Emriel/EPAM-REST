package com.epam.springCoreTask.util;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import com.epam.springCoreTask.exception.InvalidDateException;
import com.epam.springCoreTask.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ValidationUtil {

    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 100;
    private static final int MAX_TRAINING_DURATION_HOURS = 24;

    public void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            log.error("Validation failed: {} is null or blank", fieldName);
            throw new ValidationException(fieldName + " cannot be null or blank");
        }
    }

    public void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            log.error("Validation failed: {} is null", fieldName);
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    public void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            log.error("Validation failed: dateOfBirth is null");
            throw new ValidationException("Date of birth cannot be null");
        }

        LocalDate now = LocalDate.now();

        if (dateOfBirth.isAfter(now)) {
            log.error("Validation failed: dateOfBirth {} is in the future", dateOfBirth);
            throw new InvalidDateException("Date of birth cannot be in the future");
        }

        int age = Period.between(dateOfBirth, now).getYears();

        if (age < MIN_AGE) {
            log.error("Validation failed: age {} is less than minimum age {}", age, MIN_AGE);
            throw new InvalidDateException("Age cannot be less than " + MIN_AGE + " years");
        }

        if (age > MAX_AGE) {
            log.error("Validation failed: age {} exceeds maximum age {}", age, MAX_AGE);
            throw new InvalidDateException("Age cannot exceed " + MAX_AGE + " years. User is too old.");
        }
    }

    public void validateTrainingDate(LocalDate trainingDate) {
        if (trainingDate == null) {
            log.error("Validation failed: trainingDate is null");
            throw new ValidationException("Training date cannot be null");
        }

        LocalDate now = LocalDate.now();

        if (trainingDate.isBefore(now)) {
            log.error("Validation failed: trainingDate {} is in the past", trainingDate);
            throw new InvalidDateException("Training date cannot be in the past");
        }
    }

    public void validateTrainingDuration(int duration) {
        if (duration <= 0) {
            log.error("Validation failed: training duration {} is not positive", duration);
            throw new ValidationException("Training duration must be greater than 0");
        }

        if (duration > MAX_TRAINING_DURATION_HOURS) {
            log.error("Validation failed: training duration {} exceeds maximum of {} hours",
                    duration, MAX_TRAINING_DURATION_HOURS);
            throw new ValidationException("Training duration cannot exceed " + MAX_TRAINING_DURATION_HOURS + " hours");
        }
    }

    public void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            log.error("Validation failed: fromDate {} is after toDate {}", fromDate, toDate);
            throw new InvalidDateException("From date cannot be after to date");
        }
    }

    public void validateReasonableDateRange(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);

        if (fromDate != null && toDate != null) {
            long daysBetween = Period.between(fromDate, toDate).toTotalMonths();

            // Allow up to 10 years
            if (daysBetween > 120) { // 10 years in months
                log.error("Validation failed: date range from {} to {} is too long ({} months)",
                        fromDate, toDate, daysBetween);
                throw new InvalidDateException("Date range cannot exceed 10 years");
            }
        }
    }
}
