package com.epam.springCoreTask.util.impl;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.epam.springCoreTask.util.PasswordGenerator;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {

    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public String generatePassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC_CHARACTERS.length());
            password.append(ALPHANUMERIC_CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
