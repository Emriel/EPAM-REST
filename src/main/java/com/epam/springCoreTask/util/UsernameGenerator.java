package com.epam.springCoreTask.util;

import java.util.function.Predicate;

public interface UsernameGenerator {

    String generateUsername(String firstname, String lastName, Predicate<String> usernameExistsChecker);
}
