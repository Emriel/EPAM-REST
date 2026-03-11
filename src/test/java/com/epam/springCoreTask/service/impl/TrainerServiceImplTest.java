package com.epam.springCoreTask.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TrainerRepository;
import com.epam.springCoreTask.repository.TrainingTypeRepository;
import com.epam.springCoreTask.service.UserService;
import com.epam.springCoreTask.util.PasswordGenerator;
import com.epam.springCoreTask.util.UsernameGenerator;
import com.epam.springCoreTask.util.ValidationUtil;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private com.epam.springCoreTask.repository.TraineeRepository traineeRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private UserService userService;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;
    private User testUser;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Jane");
        testUser.setLastName("Smith");
        testUser.setUsername("jane.smith");
        testUser.setPassword("password123");
        testUser.setActive(true);

        testTrainingType = new TrainingType();
        testTrainingType.setId(1L);
        testTrainingType.setName("Yoga");

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(testUser);
        testTrainer.setSpecialization(testTrainingType);
        testTrainer.setTrainings(new ArrayList<>());
        testTrainer.setTrainees(new ArrayList<>());
    }

    @Test
    void testCreateTrainer_Success() {
        // Arrange
        String firstName = "Jane";
        String lastName = "Smith";
        String specialization = "Yoga";

        when(usernameGenerator.generateUsername(eq(firstName), eq(lastName), any()))
                .thenAnswer(invocation -> {
                    java.util.function.Predicate<String> checker = invocation.getArgument(2);
                    return "jane.smith";
                });
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(trainingTypeRepository.findByName(specialization)).thenReturn(Optional.of(testTrainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        // Act
        Trainer result = trainerService.createTrainer(firstName, lastName, specialization);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(validationUtil).validateNotBlank(firstName, "First name");
        verify(validationUtil).validateNotBlank(lastName, "Last name");
        verify(validationUtil).validateNotBlank(specialization, "Specialization");
        verify(trainingTypeRepository).findByName(specialization);
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testCreateTrainer_NullFirstName() {
        // Arrange
        doThrow(new com.epam.springCoreTask.exception.ValidationException("First name cannot be null or blank"))
                .when(validationUtil).validateNotBlank(null, "First name");

        // Act & Assert
        assertThrows(com.epam.springCoreTask.exception.ValidationException.class,
                () -> trainerService.createTrainer(null, "Smith", "Yoga"));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testCreateTrainer_InvalidSpecialization() {
        // Arrange
        String firstName = "Jane";
        String lastName = "Smith";
        String specialization = "InvalidType";

        // Only stub what's actually called before the exception
        when(usernameGenerator.generateUsername(eq(firstName), eq(lastName), any())).thenReturn("jane.smith");
        when(passwordGenerator.generatePassword()).thenReturn("password123");
        when(trainingTypeRepository.findByName(specialization)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> trainerService.createTrainer(firstName, lastName, specialization));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testUpdateTrainer_Success() {
        // Arrange
        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);

        // Act
        Trainer result = trainerService.updateTrainer(testTrainer);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(validationUtil).validateNotNull(testTrainer, "Trainer");
        verify(validationUtil).validateNotNull(1L, "Trainer ID");
        verify(trainerRepository).existsById(1L);
        verify(trainerRepository).save(testTrainer);
    }

    @Test
    void testUpdateTrainer_NotFound() {
        // Arrange
        when(trainerRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> trainerService.updateTrainer(testTrainer));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testUpdateTrainer_NullTrainer() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> trainerService.updateTrainer(null));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testGetTrainerById_Found() {
        // Arrange
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));

        // Act
        Trainer result = trainerService.getTrainerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerRepository).findById(1L);
    }

    @Test
    void testGetTrainerById_NotFound() {
        // Arrange
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Trainer result = trainerService.getTrainerById(999L);

        // Assert
        assertNull(result);
        verify(trainerRepository).findById(999L);
    }

    @Test
    void testGetAllTrainers_Success() {
        // Arrange
        List<Trainer> trainers = Arrays.asList(testTrainer, new Trainer());
        when(trainerRepository.findAll()).thenReturn(trainers);

        // Act
        List<Trainer> result = trainerService.getAllTrainers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainerRepository).findAll();
    }

    @Test
    void testAuthenticateTrainer_Success() {
        // Arrange
        String username = "jane.smith";
        String password = "password123";
        when(userService.authenticate(eq(username), eq(password), any(), eq("trainer"))).thenReturn(testTrainer);

        // Act
        Trainer result = trainerService.authenticateTrainer(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(userService).authenticate(eq(username), eq(password), any(), eq("trainer"));
    }

    @Test
    void testGetTrainerByUsername_Found() {
        // Arrange
        when(trainerRepository.findByUser_Username("jane.smith")).thenReturn(Optional.of(testTrainer));

        // Act
        Trainer result = trainerService.getTrainerByUsername("jane.smith");

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerRepository).findByUser_Username("jane.smith");
    }

    @Test
    void testGetTrainerByUsername_NotFound() {
        // Arrange
        when(trainerRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.getTrainerByUsername("unknown"));

        assertEquals("Trainer not found with username: unknown", exception.getMessage());
    }

    @Test
    void testChangeTrainerPassword_Success() {
        // Arrange
        String username = "jane.smith";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        // Act
        trainerService.changeTrainerPassword(username, oldPassword, newPassword);

        // Assert
        verify(userService).changePassword(eq(username), eq(oldPassword), eq(newPassword), any(), any(), any(),
                eq("trainer"));
    }

    @Test
    void testActivateTrainer_Success() {
        // Arrange
        String username = "jane.smith";

        // Act
        trainerService.activateTrainer(username);

        // Assert
        verify(userService).activateEntity(eq(username), any(), any(), any(), eq("trainer"));
    }

    @Test
    void testDeactivateTrainer_Success() {
        // Arrange
        String username = "jane.smith";

        // Act
        trainerService.deactivateTrainer(username);

        // Assert
        verify(userService).deactivateEntity(eq(username), any(), any(), any(), eq("trainer"));
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_Success() {
        // Arrange
        String traineeUsername = "john.doe";
        List<Trainer> unassignedTrainers = Arrays.asList(testTrainer);

        when(traineeRepository.existsByUser_Username(traineeUsername)).thenReturn(true);
        when(trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(unassignedTrainers);

        // Act
        List<Trainer> result = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTrainer, result.get(0));
        verify(validationUtil).validateNotBlank(traineeUsername, "Trainee username");
        verify(traineeRepository).existsByUser_Username(traineeUsername);
        verify(trainerRepository).findTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_EmptyList() {
        // Arrange
        String traineeUsername = "john.doe";

        when(traineeRepository.existsByUser_Username(traineeUsername)).thenReturn(true);
        when(trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(new ArrayList<>());

        // Act
        List<Trainer> result = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(traineeRepository).existsByUser_Username(traineeUsername);
        verify(trainerRepository).findTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_NullUsername() {
        // Arrange
        doThrow(new com.epam.springCoreTask.exception.ValidationException("Trainee username cannot be null or blank"))
                .when(validationUtil).validateNotBlank(null, "Trainee username");

        // Act & Assert
        assertThrows(com.epam.springCoreTask.exception.ValidationException.class,
                () -> trainerService.getTrainersNotAssignedToTrainee(null));

        verify(trainerRepository, never()).findTrainersNotAssignedToTrainee(any());
    }
}
