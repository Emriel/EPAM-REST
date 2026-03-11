package com.epam.springCoreTask.util.impl;

import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.epam.springCoreTask.util.UsernameGenerator;

@Component
public class UsernameGeneratorImpl implements UsernameGenerator {

    public String generateUsername(String firstName, String lastName, Predicate<String> usernameExistsChecker) {

        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int counter = 1;

        while (usernameExistsChecker.test(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
}
