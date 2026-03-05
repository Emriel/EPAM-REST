package com.epam.springCoreTask.util.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsernameGeneratorImplTest {

    private UsernameGeneratorImpl usernameGenerator;

    @BeforeEach
    void setUp() {
        usernameGenerator = new UsernameGeneratorImpl();
    }

    @Test
    void testGenerateUsername_WithEmptyList_ReturnsBaseUsername() {
        List<String> existingUsernames = new ArrayList<>();
        String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames);

        assertEquals("John.Doe", username, "Should return base username when no conflicts exist");
    }

    @Test
    void testGenerateUsername_WithNoConflict_ReturnsBaseUsername() {
        List<String> existingUsernames = Arrays.asList("Jane.Smith", "Bob.Johnson");
        String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames);

        assertEquals("John.Doe", username, "Should return base username when no conflicts exist");
    }

    @Test
    void testGenerateUsername_WithOneConflict_ReturnsUsernameWithCounter() {
        List<String> existingUsernames = Arrays.asList("John.Doe", "Jane.Smith");
        String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames);

        assertEquals("John.Doe1", username, "Should append 1 when base username already exists");
    }

    @Test
    void testGenerateUsername_WithMultipleConflicts_ReturnsNextAvailableNumber() {
        List<String> existingUsernames = Arrays.asList("John.Doe", "John.Doe1", "John.Doe2");
        String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames);

        assertEquals("John.Doe3", username, "Should append 3 when John.Doe, John.Doe1, and John.Doe2 exist");
    }

    @Test
    void testGenerateUsername_WithNonConsecutiveConflicts_ReturnsFirstAvailableNumber() {
        // Even if John.Doe2 exists, if John.Doe1 doesn't exist, it should return
        // John.Doe1
        List<String> existingUsernames = Arrays.asList("John.Doe", "John.Doe2");
        String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames);

        assertEquals("John.Doe1", username, "Should return first available numbered username");
    }

    @Test
    void testGenerateUsername_WithDifferentNames_ReturnsCorrectFormat() {
        List<String> existingUsernames = new ArrayList<>();
        String username = usernameGenerator.generateUsername("Alice", "Williams", existingUsernames);

        assertEquals("Alice.Williams", username, "Should format username as FirstName.LastName");
    }

    @Test
    void testGenerateUsername_CaseSensitive() {
        List<String> existingUsernames = Arrays.asList("john.doe");
        String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames);

        assertEquals("John.Doe", username, "Username generation should be case-sensitive");
    }

    @Test
    void testGenerateUsername_WithManyConflicts() {
        List<String> existingUsernames = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                existingUsernames.add("John.Doe");
            } else {
                existingUsernames.add("John.Doe" + i);
            }
        }

        String username = usernameGenerator.generateUsername("John", "Doe", existingUsernames);
        assertEquals("John.Doe10", username, "Should handle many conflicts correctly");
    }
}
