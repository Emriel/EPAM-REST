package com.epam.springCoreTask.util.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorImplTest {

    private PasswordGeneratorImpl passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGeneratorImpl();
    }

    @Test
    void testGeneratePassword_ReturnsNonNullPassword() {
        String password = passwordGenerator.generatePassword();
        assertNotNull(password, "Generated password should not be null");
    }

    @Test
    void testGeneratePassword_ReturnsCorrectLength() {
        String password = passwordGenerator.generatePassword();
        assertEquals(10, password.length(), "Generated password should have length 10");
    }

    @Test
    void testGeneratePassword_ContainsOnlyValidCharacters() {
        String password = passwordGenerator.generatePassword();
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (char c : password.toCharArray()) {
            assertTrue(validChars.indexOf(c) >= 0,
                    "Password should only contain alphanumeric characters");
        }
    }

    @Test
    void testGeneratePassword_GeneratesUniquePasswords() {
        String password1 = passwordGenerator.generatePassword();
        String password2 = passwordGenerator.generatePassword();

        assertNotEquals(password1, password2,
                "Two consecutive password generations should likely produce different results");
    }

    @Test
    void testGeneratePassword_MultipleCallsReturnValidPasswords() {
        for (int i = 0; i < 100; i++) {
            String password = passwordGenerator.generatePassword();
            assertNotNull(password);
            assertEquals(10, password.length());
        }
    }
}
