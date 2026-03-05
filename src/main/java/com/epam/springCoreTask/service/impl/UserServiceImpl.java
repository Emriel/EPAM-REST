package com.epam.springCoreTask.service.impl;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.epam.springCoreTask.dto.AuthenticationDTO;
import com.epam.springCoreTask.exception.ValidationException;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.service.UserService;
import com.epam.springCoreTask.util.ValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

        private final ValidationUtil validationUtil;

        @Override
        public <T> T authenticate(String username, String password,
                        Function<AuthenticationDTO, Optional<T>> repositoryFinder,
                        String entityType) {
                log.debug("Authenticating {}: username={}", entityType, username);

                validationUtil.validateNotBlank(username, "Username");
                validationUtil.validateNotBlank(password, "Password");

                AuthenticationDTO auth = AuthenticationDTO.builder()
                                .username(username)
                                .password(password)
                                .build();

                T entity = repositoryFinder.apply(auth)
                                .orElseThrow(() -> {
                                        log.warn("Authentication failed for {}: username={}", entityType, username);
                                        return new IllegalArgumentException("Invalid username or password");
                                });

                log.info("{} authenticated successfully: username={}",
                                entityType.substring(0, 1).toUpperCase() + entityType.substring(1), username);
                return entity;
        }

        @Override
        public <T> void changePassword(String username, String oldPassword, String newPassword,
                        Function<AuthenticationDTO, Optional<T>> authFinder,
                        Function<T, User> userGetter,
                        Consumer<T> saver,
                        String entityType) {
                log.debug("Changing password for {}: username={}", entityType, username);

                validationUtil.validateNotBlank(username, "Username");
                validationUtil.validateNotBlank(oldPassword, "Old password");
                validationUtil.validateNotBlank(newPassword, "New password");

                AuthenticationDTO auth = AuthenticationDTO.builder()
                                .username(username)
                                .password(oldPassword)
                                .build();

                T entity = authFinder.apply(auth)
                                .orElseThrow(() -> {
                                        log.warn("Password change failed - invalid credentials for {}: username={}",
                                                        entityType, username);
                                        return new ValidationException(
                                                        "Invalid username or old password does not match");
                                });

                User user = userGetter.apply(entity);
                user.setPassword(newPassword);
                saver.accept(entity);

                log.info("Password changed successfully for {}: username={}", entityType, username);
        }

        @Override
        public <T> void activateEntity(String username,
                        Function<String, T> entityGetter,
                        Function<T, User> userGetter,
                        Consumer<T> saver,
                        String entityType) {
                log.debug("Activating {}: username={}", entityType, username);

                validationUtil.validateNotBlank(username, "Username");

                T entity = entityGetter.apply(username);
                User user = userGetter.apply(entity);
                user.setActive(true);
                saver.accept(entity);

                log.info("{} activated successfully: username={}",
                                entityType.substring(0, 1).toUpperCase() + entityType.substring(1), username);
        }

        @Override
        public <T> void deactivateEntity(String username,
                        Function<String, T> entityGetter,
                        Function<T, User> userGetter,
                        Consumer<T> saver,
                        String entityType) {
                log.debug("Deactivating {}: username={}", entityType, username);

                validationUtil.validateNotBlank(username, "Username");

                T entity = entityGetter.apply(username);
                User user = userGetter.apply(entity);
                user.setActive(false);
                saver.accept(entity);

                log.info("{} deactivated successfully: username={}",
                                entityType.substring(0, 1).toUpperCase() + entityType.substring(1), username);
        }
}
