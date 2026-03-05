package com.epam.springCoreTask.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.epam.springCoreTask.exception.InvalidDateException;
import com.epam.springCoreTask.exception.ValidationException;

class ValidationUtilTest {

    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        validationUtil = new ValidationUtil();
    }

    // validateNotBlank tests
    @Test
    void testValidateNotBlank_ValidString() {
        assertDoesNotThrow(() -> validationUtil.validateNotBlank("valid", "Field"));
    }

    @Test
    void testValidateNotBlank_NullValue() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateNotBlank(null, "Field"));
        assertEquals("Field cannot be null or blank", exception.getMessage());
    }

    @Test
    void testValidateNotBlank_EmptyString() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateNotBlank("", "Field"));
        assertEquals("Field cannot be null or blank", exception.getMessage());
    }

    @Test
    void testValidateNotBlank_BlankString() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateNotBlank("   ", "Field"));
        assertEquals("Field cannot be null or blank", exception.getMessage());
    }

    // validateNotNull tests
    @Test
    void testValidateNotNull_ValidObject() {
        assertDoesNotThrow(() -> validationUtil.validateNotNull(new Object(), "Field"));
    }

    @Test
    void testValidateNotNull_NullValue() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateNotNull(null, "Field"));
        assertEquals("Field cannot be null", exception.getMessage());
    }

    // validateDateOfBirth tests
    @Test
    void testValidateDateOfBirth_ValidDate() {
        LocalDate validDate = LocalDate.now().minusYears(25);
        assertDoesNotThrow(() -> validationUtil.validateDateOfBirth(validDate));
    }

    @Test
    void testValidateDateOfBirth_Null() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateDateOfBirth(null));
        assertEquals("Date of birth cannot be null", exception.getMessage());
    }

    @Test
    void testValidateDateOfBirth_FutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> validationUtil.validateDateOfBirth(futureDate));
        assertEquals("Date of birth cannot be in the future", exception.getMessage());
    }

    @Test
    void testValidateDateOfBirth_TooOld() {
        LocalDate tooOld = LocalDate.now().minusYears(101);
        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> validationUtil.validateDateOfBirth(tooOld));
        assertEquals("Age cannot exceed 100 years. User is too old.", exception.getMessage());
    }

    @Test
    void testValidateDateOfBirth_ExactlyMaxAge() {
        LocalDate maxAge = LocalDate.now().minusYears(100);
        assertDoesNotThrow(() -> validationUtil.validateDateOfBirth(maxAge));
    }

    @Test
    void testValidateDateOfBirth_Today() {
        LocalDate today = LocalDate.now();
        assertDoesNotThrow(() -> validationUtil.validateDateOfBirth(today));
    }

    // validateTrainingDate tests
    @Test
    void testValidateTrainingDate_FutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        assertDoesNotThrow(() -> validationUtil.validateTrainingDate(futureDate));
    }

    @Test
    void testValidateTrainingDate_Today() {
        LocalDate today = LocalDate.now();
        assertDoesNotThrow(() -> validationUtil.validateTrainingDate(today));
    }

    @Test
    void testValidateTrainingDate_Null() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateTrainingDate(null));
        assertEquals("Training date cannot be null", exception.getMessage());
    }

    @Test
    void testValidateTrainingDate_PastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> validationUtil.validateTrainingDate(pastDate));
        assertEquals("Training date cannot be in the past", exception.getMessage());
    }

    // validateTrainingDuration tests
    @Test
    void testValidateTrainingDuration_ValidDuration() {
        assertDoesNotThrow(() -> validationUtil.validateTrainingDuration(2));
    }

    @Test
    void testValidateTrainingDuration_MaxDuration() {
        assertDoesNotThrow(() -> validationUtil.validateTrainingDuration(24));
    }

    @Test
    void testValidateTrainingDuration_Zero() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateTrainingDuration(0));
        assertEquals("Training duration must be greater than 0", exception.getMessage());
    }

    @Test
    void testValidateTrainingDuration_Negative() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateTrainingDuration(-5));
        assertEquals("Training duration must be greater than 0", exception.getMessage());
    }

    @Test
    void testValidateTrainingDuration_ExceedsMaximum() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationUtil.validateTrainingDuration(25));
        assertEquals("Training duration cannot exceed 24 hours", exception.getMessage());
    }

    // validateDateRange tests
    @Test
    void testValidateDateRange_ValidRange() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(7);
        assertDoesNotThrow(() -> validationUtil.validateDateRange(fromDate, toDate));
    }

    @Test
    void testValidateDateRange_SameDate() {
        LocalDate date = LocalDate.now();
        assertDoesNotThrow(() -> validationUtil.validateDateRange(date, date));
    }

    @Test
    void testValidateDateRange_InvalidRange() {
        LocalDate fromDate = LocalDate.now().plusDays(7);
        LocalDate toDate = LocalDate.now();
        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> validationUtil.validateDateRange(fromDate, toDate));
        assertEquals("From date cannot be after to date", exception.getMessage());
    }

    @Test
    void testValidateDateRange_NullFromDate() {
        LocalDate toDate = LocalDate.now();
        assertDoesNotThrow(() -> validationUtil.validateDateRange(null, toDate));
    }

    @Test
    void testValidateDateRange_NullToDate() {
        LocalDate fromDate = LocalDate.now();
        assertDoesNotThrow(() -> validationUtil.validateDateRange(fromDate, null));
    }

    @Test
    void testValidateDateRange_BothNull() {
        assertDoesNotThrow(() -> validationUtil.validateDateRange(null, null));
    }

    // validateReasonableDateRange tests
    @Test
    void testValidateReasonableDateRange_ValidRange() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusMonths(6);
        assertDoesNotThrow(() -> validationUtil.validateReasonableDateRange(fromDate, toDate));
    }

    @Test
    void testValidateReasonableDateRange_TenYearRange() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusYears(10);
        assertDoesNotThrow(() -> validationUtil.validateReasonableDateRange(fromDate, toDate));
    }

    @Test
    void testValidateReasonableDateRange_ExceedsTenYears() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusYears(11);
        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> validationUtil.validateReasonableDateRange(fromDate, toDate));
        assertEquals("Date range cannot exceed 10 years", exception.getMessage());
    }

    @Test
    void testValidateReasonableDateRange_InvalidOrder() {
        LocalDate fromDate = LocalDate.now().plusDays(7);
        LocalDate toDate = LocalDate.now();
        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> validationUtil.validateReasonableDateRange(fromDate, toDate));
        assertEquals("From date cannot be after to date", exception.getMessage());
    }

    @Test
    void testValidateReasonableDateRange_NullDates() {
        assertDoesNotThrow(() -> validationUtil.validateReasonableDateRange(null, null));
    }

    @Test
    void testValidateReasonableDateRange_OnlyFromDateNull() {
        LocalDate toDate = LocalDate.now();
        assertDoesNotThrow(() -> validationUtil.validateReasonableDateRange(null, toDate));
    }

    @Test
    void testValidateReasonableDateRange_OnlyToDateNull() {
        LocalDate fromDate = LocalDate.now();
        assertDoesNotThrow(() -> validationUtil.validateReasonableDateRange(fromDate, null));
    }
}
