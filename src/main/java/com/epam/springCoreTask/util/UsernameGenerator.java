package com.epam.springCoreTask.util;

import java.util.List;

public interface UsernameGenerator {

    String generateUsername(String firstname, String lastName, List<String> existingUsernames);
}
