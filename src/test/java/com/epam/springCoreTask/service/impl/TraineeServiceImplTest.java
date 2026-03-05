package com.epam.springCoreTask.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.springCoreTask.exception.DuplicateAssignmentException;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TraineeRepository;
import com.epam.springCoreTask.repository.TrainerRepository;
import com.epam.springCoreTask.service.UserService;
import com.epam.springCoreTask.util.PasswordGenerator;
import com.epam.springCoreTask.util.UsernameGenerator;
import com.epam.springCoreTask.util.ValidationUtil;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private UserService userService;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee testTrainee;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("john.doe");
        testUser.setPassword("password123");
        testUser.setActive(true);

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(testUser);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Main St");
        testTrainee.setTrainers(new ArrayList<>());
    }

    @Test
    void testCreateTrainee_Success() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";

        when(traineeRepository.findAllUsernames()).thenReturn(Arrays.asList("other.user"));
        when(usernameGenerator.generateUsername(firstName, lastName, Arrays.asList("other.user")))
                .thenReturn("john.doe");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        // Act
        Trainee result = traineeService.createTrainee(firstName, lastName, dateOfBirth, address);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(validationUtil).validateNotBlank(firstName, "First name");
        verify(validationUtil).validateNotBlank(lastName, "Last name");
        verify(validationUtil).validateDateOfBirth(dateOfBirth);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void testCreateTrainee_NullFirstName() {
        // Arrange
        doThrow(new com.epam.springCoreTask.exception.ValidationException("First name cannot be null or blank"))
                .when(validationUtil).validateNotBlank(null, "First name");

        // Act & Assert
        assertThrows(com.epam.springCoreTask.exception.ValidationException.class,
                () -> traineeService.createTrainee(null, "Doe", LocalDate.of(1990, 1, 1), "Address"));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testCreateTrainee_InvalidDateOfBirth() {
        // Arrange
        LocalDate invalidDate = LocalDate.now().plusDays(1);
        doThrow(new com.epam.springCoreTask.exception.InvalidDateException("Date of birth cannot be in the future"))
                .when(validationUtil).validateDateOfBirth(invalidDate);

        // Act & Assert
        assertThrows(com.epam.springCoreTask.exception.InvalidDateException.class,
                () -> traineeService.createTrainee("John", "Doe", invalidDate, "Address"));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testUpdateTrainee_Success() {
        // Arrange
        when(traineeRepository.existsById(1L)).thenReturn(true);
        when(traineeRepository.save(testTrainee)).thenReturn(testTrainee);

        // Act
        Trainee result = traineeService.updateTrainee(testTrainee);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(validationUtil).validateNotNull(testTrainee, "Trainee");
        verify(validationUtil).validateNotNull(1L, "Trainee ID");
        verify(traineeRepository).existsById(1L);
        verify(traineeRepository).save(testTrainee);
    }

    @Test
    void testUpdateTrainee_NotFound() {
        // Arrange
        when(traineeRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> traineeService.updateTrainee(testTrainee));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testUpdateTrainee_NullTrainee() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> traineeService.updateTrainee(null));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testDeleteTrainee_Success() {
        // Act
        traineeService.deleteTrainee(testTrainee);

        // Assert
        verify(traineeRepository).delete(testTrainee);
    }

    @Test
    void testGetTraineeById_Found() {
        // Arrange
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));

        // Act
        Trainee result = traineeService.getTraineeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeRepository).findById(1L);
    }

    @Test
    void testGetTraineeById_NotFound() {
        // Arrange
        when(traineeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Trainee result = traineeService.getTraineeById(999L);

        // Assert
        assertNull(result);
        verify(traineeRepository).findById(999L);
    }

    @Test
    void testGetAllTrainees_Success() {
        // Arrange
        List<Trainee> trainees = Arrays.asList(testTrainee, new Trainee());
        when(traineeRepository.findAll()).thenReturn(trainees);

        // Act
        List<Trainee> result = traineeService.getAllTrainees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(traineeRepository).findAll();
    }

    @Test
    void testAuthenticateTrainee_Success() {
        // Arrange
        String username = "john.doe";
        String password = "password123";
        when(userService.authenticate(eq(username), eq(password), any(), eq("trainee"))).thenReturn(testTrainee);

        // Act
        Trainee result = traineeService.authenticateTrainee(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(userService).authenticate(eq(username), eq(password), any(), eq("trainee"));
    }

    @Test
    void testGetTraineeByUsername_Found() {
        // Arrange
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(testTrainee));

        // Act
        Trainee result = traineeService.getTraineeByUsername("john.doe");

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeRepository).findByUser_Username("john.doe");
    }

    @Test
    void testGetTraineeByUsername_NotFound() {
        // Arrange
        when(traineeRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> traineeService.getTraineeByUsername("unknown"));

        assertEquals("Trainee not found with username: unknown", exception.getMessage());
    }

    @Test
    void testChangeTraineePassword_Success() {
        // Arrange
        String username = "john.doe";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        // Act
        traineeService.changeTraineePassword(username, oldPassword, newPassword);

        // Assert
        verify(userService).changePassword(eq(username), eq(oldPassword), eq(newPassword), any(), any(), any(),
                eq("trainee"));
    }

    @Test
    void testActivateTrainee_Success() {
        // Arrange
        String username = "john.doe";

        // Act
        traineeService.activateTrainee(username);

        // Assert
        verify(userService).activateEntity(eq(username), any(), any(), any(), eq("trainee"));
    }

    @Test
    void testDeactivateTrainee_Success() {
        // Arrange
        String username = "john.doe";

        // Act
        traineeService.deactivateTrainee(username);

        // Assert
        verify(userService).deactivateEntity(eq(username), any(), any(), any(), eq("trainee"));
    }

    @Test
    void testDeleteTraineeByUsername_Success() {
        // Arrange
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(testTrainee));

        // Act
        traineeService.deleteTraineeByUsername("john.doe");

        // Assert
        verify(traineeRepository).findByUser_Username("john.doe");
        verify(traineeRepository).delete(testTrainee);
    }

    @Test
    void testUpdateTraineeTrainersList_Success() {
        // Arrange
        String traineeUsername = "john.doe";
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");

        Trainer trainer1 = new Trainer();
        User trainerUser1 = new User();
        trainerUser1.setUsername("trainer1");
        trainer1.setUser(trainerUser1);

        Trainer trainer2 = new Trainer();
        User trainerUser2 = new User();
        trainerUser2.setUsername("trainer2");
        trainer2.setUser(trainerUser2);

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUser_Username("trainer1")).thenReturn(Optional.of(trainer1));
        when(trainerRepository.findByUser_Username("trainer2")).thenReturn(Optional.of(trainer2));
        when(traineeRepository.save(testTrainee)).thenReturn(testTrainee);

        // Act
        traineeService.updateTraineeTrainersList(traineeUsername, trainerUsernames);

        // Assert
        verify(validationUtil).validateNotBlank(traineeUsername, "Trainee username");
        verify(traineeRepository).findByUser_Username(traineeUsername);
        verify(trainerRepository).findByUser_Username("trainer1");
        verify(trainerRepository).findByUser_Username("trainer2");
        verify(traineeRepository).save(testTrainee);
        assertEquals(2, testTrainee.getTrainers().size());
    }

    @Test
    void testUpdateTraineeTrainersList_EmptyList() {
        // Arrange
        String traineeUsername = "john.doe";
        List<String> trainerUsernames = new ArrayList<>();

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(testTrainee)).thenReturn(testTrainee);

        // Act
        traineeService.updateTraineeTrainersList(traineeUsername, trainerUsernames);

        // Assert
        verify(traineeRepository).save(testTrainee);
        assertEquals(0, testTrainee.getTrainers().size());
    }

    @Test
    void testUpdateTraineeTrainersList_TrainerNotFound() {
        // Arrange
        String traineeUsername = "john.doe";
        List<String> trainerUsernames = Arrays.asList("unknown.trainer");

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUser_Username("unknown.trainer")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> traineeService.updateTraineeTrainersList(traineeUsername, trainerUsernames));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testUpdateTraineeTrainersList_DuplicateAssignment() {
        // Arrange
        String traineeUsername = "john.doe";
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer1");

        Trainer trainer1 = new Trainer();
        User trainerUser1 = new User();
        trainerUser1.setUsername("trainer1");
        trainer1.setUser(trainerUser1);

        testTrainee.getTrainers().add(trainer1);

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUser_Username("trainer1")).thenReturn(Optional.of(trainer1));

        // Act & Assert
        assertThrows(DuplicateAssignmentException.class,
                () -> traineeService.updateTraineeTrainersList(traineeUsername, trainerUsernames));
    }
}
