package com.epam.springCoreTask.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.springCoreTask.dto.AuthenticationDTO;
import com.epam.springCoreTask.exception.ValidationException;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.util.ValidationUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testAuthenticate_Success() {
        // Arrange
        String username = "john.doe";
        String password = "password123";
        Trainee expectedTrainee = new Trainee();
        expectedTrainee.setId(1L);

        @SuppressWarnings("unchecked")
        Function<AuthenticationDTO, Optional<Trainee>> repositoryFinder = mock(Function.class);
        when(repositoryFinder.apply(any(AuthenticationDTO.class))).thenReturn(Optional.of(expectedTrainee));

        // Act
        Trainee result = userService.authenticate(username, password, repositoryFinder, "trainee");

        // Assert
        assertNotNull(result);
        assertEquals(expectedTrainee, result);
        verify(validationUtil).validateNotBlank(username, "Username");
        verify(validationUtil).validateNotBlank(password, "Password");
        verify(repositoryFinder).apply(any(AuthenticationDTO.class));
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        // Arrange
        String username = "john.doe";
        String password = "wrongpassword";

        @SuppressWarnings("unchecked")
        Function<AuthenticationDTO, Optional<Trainee>> repositoryFinder = mock(Function.class);
        when(repositoryFinder.apply(any(AuthenticationDTO.class))).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.authenticate(username, password, repositoryFinder, "trainee"));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(validationUtil).validateNotBlank(username, "Username");
        verify(validationUtil).validateNotBlank(password, "Password");
    }

    @Test
    void testAuthenticate_NullUsername() {
        // Arrange
        doThrow(new ValidationException("Username cannot be null or blank"))
                .when(validationUtil).validateNotBlank(null, "Username");

        @SuppressWarnings("unchecked")
        Function<AuthenticationDTO, Optional<Trainee>> repositoryFinder = mock(Function.class);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> userService.authenticate(null, "password", repositoryFinder, "trainee"));
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        String username = "john.doe";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(oldPassword);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        @SuppressWarnings("unchecked")
        Function<AuthenticationDTO, Optional<Trainee>> authFinder = mock(Function.class);
        when(authFinder.apply(any(AuthenticationDTO.class))).thenReturn(Optional.of(trainee));

        @SuppressWarnings("unchecked")
        Function<Trainee, User> userGetter = mock(Function.class);
        when(userGetter.apply(trainee)).thenReturn(user);

        @SuppressWarnings("unchecked")
        Consumer<Trainee> saver = mock(Consumer.class);

        // Act
        userService.changePassword(username, oldPassword, newPassword, authFinder, userGetter, saver, "trainee");

        // Assert
        assertEquals(newPassword, user.getPassword());
        verify(validationUtil).validateNotBlank(username, "Username");
        verify(validationUtil).validateNotBlank(oldPassword, "Old password");
        verify(validationUtil).validateNotBlank(newPassword, "New password");
        verify(authFinder).apply(any(AuthenticationDTO.class));
        verify(userGetter).apply(trainee);
        verify(saver).accept(trainee);
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        // Arrange
        String username = "john.doe";
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword";

        @SuppressWarnings("unchecked")
        Function<AuthenticationDTO, Optional<Trainee>> authFinder = mock(Function.class);
        when(authFinder.apply(any(AuthenticationDTO.class))).thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        Function<Trainee, User> userGetter = mock(Function.class);

        @SuppressWarnings("unchecked")
        Consumer<Trainee> saver = mock(Consumer.class);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.changePassword(username, oldPassword, newPassword, authFinder, userGetter, saver,
                        "trainee"));

        assertEquals("Invalid username or old password does not match", exception.getMessage());
        verify(saver, never()).accept(any());
    }

    @Test
    void testActivateEntity_Success() {
        // Arrange
        String username = "john.doe";

        User user = new User();
        user.setUsername(username);
        user.setActive(false);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        @SuppressWarnings("unchecked")
        Function<String, Trainee> entityGetter = mock(Function.class);
        when(entityGetter.apply(username)).thenReturn(trainee);

        @SuppressWarnings("unchecked")
        Function<Trainee, User> userGetter = mock(Function.class);
        when(userGetter.apply(trainee)).thenReturn(user);

        @SuppressWarnings("unchecked")
        Consumer<Trainee> saver = mock(Consumer.class);

        // Act
        userService.activateEntity(username, entityGetter, userGetter, saver, "trainee");

        // Assert
        assertTrue(user.isActive());
        verify(validationUtil).validateNotBlank(username, "Username");
        verify(entityGetter).apply(username);
        verify(userGetter).apply(trainee);
        verify(saver).accept(trainee);
    }

    @Test
    void testActivateEntity_NullUsername() {
        // Arrange
        doThrow(new ValidationException("Username cannot be null or blank"))
                .when(validationUtil).validateNotBlank(null, "Username");

        @SuppressWarnings("unchecked")
        Function<String, Trainee> entityGetter = mock(Function.class);
        @SuppressWarnings("unchecked")
        Function<Trainee, User> userGetter = mock(Function.class);
        @SuppressWarnings("unchecked")
        Consumer<Trainee> saver = mock(Consumer.class);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> userService.activateEntity(null, entityGetter, userGetter, saver, "trainee"));

        verify(entityGetter, never()).apply(any());
    }

    @Test
    void testDeactivateEntity_Success() {
        // Arrange
        String username = "john.doe";

        User user = new User();
        user.setUsername(username);
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        @SuppressWarnings("unchecked")
        Function<String, Trainee> entityGetter = mock(Function.class);
        when(entityGetter.apply(username)).thenReturn(trainee);

        @SuppressWarnings("unchecked")
        Function<Trainee, User> userGetter = mock(Function.class);
        when(userGetter.apply(trainee)).thenReturn(user);

        @SuppressWarnings("unchecked")
        Consumer<Trainee> saver = mock(Consumer.class);

        // Act
        userService.deactivateEntity(username, entityGetter, userGetter, saver, "trainee");

        // Assert
        assertFalse(user.isActive());
        verify(validationUtil).validateNotBlank(username, "Username");
        verify(entityGetter).apply(username);
        verify(userGetter).apply(trainee);
        verify(saver).accept(trainee);
    }

    @Test
    void testDeactivateEntity_NullUsername() {
        // Arrange
        doThrow(new ValidationException("Username cannot be null or blank"))
                .when(validationUtil).validateNotBlank(null, "Username");

        @SuppressWarnings("unchecked")
        Function<String, Trainee> entityGetter = mock(Function.class);
        @SuppressWarnings("unchecked")
        Function<Trainee, User> userGetter = mock(Function.class);
        @SuppressWarnings("unchecked")
        Consumer<Trainee> saver = mock(Consumer.class);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> userService.deactivateEntity(null, entityGetter, userGetter, saver, "trainee"));

        verify(entityGetter, never()).apply(any());
    }
}
