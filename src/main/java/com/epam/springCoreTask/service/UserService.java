package com.epam.springCoreTask.service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.epam.springCoreTask.dto.AuthenticationDTO;
import com.epam.springCoreTask.model.User;

public interface UserService {

        <T> T authenticate(String username, String password,
                        Function<AuthenticationDTO, Optional<T>> repositoryFinder,
                        String entityType);

        <T> void changePassword(String username, String oldPassword, String newPassword,
                        Function<AuthenticationDTO, Optional<T>> authFinder,
                        Function<T, User> userGetter,
                        Consumer<T> saver,
                        String entityType);

        <T> void activateEntity(String username,
                        Function<String, T> entityGetter,
                        Function<T, User> userGetter,
                        Consumer<T> saver,
                        String entityType);

        <T> void deactivateEntity(String username,
                        Function<String, T> entityGetter,
                        Function<T, User> userGetter,
                        Consumer<T> saver,
                        String entityType);
}
