package com.epam.springCoreTask.facade.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.service.TraineeService;
import com.epam.springCoreTask.service.TrainerService;
import com.epam.springCoreTask.service.TrainingService;

@ExtendWith(MockitoExtension.class)
class GymFacadeImplTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacadeImpl gymFacade;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private Training testTraining;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        User traineeUser = new User();
        traineeUser.setUsername("john.doe");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        User trainerUser = new User();
        trainerUser.setUsername("jane.smith");
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");

        testTrainingType = new TrainingType();
        testTrainingType.setId(1L);
        testTrainingType.setName("Yoga");

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(traineeUser);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Main St");

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(trainerUser);
        testTrainer.setSpecialization(testTrainingType);

        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setTrainee(testTrainee);
        testTraining.setTrainer(testTrainer);
        testTraining.setTrainingName("Morning Yoga");
        testTraining.setTrainingType(testTrainingType);
        testTraining.setTrainingDate(LocalDate.now().plusDays(1));
        testTraining.setTrainingDuration(2);
    }

    @Test
    void testCreateTraineeProfile() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";

        when(traineeService.createTrainee(firstName, lastName, dateOfBirth, address)).thenReturn(testTrainee);

        // Act
        Trainee result = gymFacade.createTraineeProfile(firstName, lastName, dateOfBirth, address);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeService).createTrainee(firstName, lastName, dateOfBirth, address);
    }

    @Test
    void testCreateTrainerProfile() {
        // Arrange
        String firstName = "Jane";
        String lastName = "Smith";
        String specialization = "Yoga";

        when(trainerService.createTrainer(firstName, lastName, specialization)).thenReturn(testTrainer);

        // Act
        Trainer result = gymFacade.createTrainerProfile(firstName, lastName, specialization);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerService).createTrainer(firstName, lastName, specialization);
    }

    @Test
    void testCreateTrainingSession_Success() {
        // Arrange
        Long traineeId = 1L;
        Long trainerId = 1L;
        String trainingName = "Morning Yoga";
        LocalDate trainingDate = LocalDate.now().plusDays(1);
        int duration = 2;

        when(traineeService.getTraineeById(traineeId)).thenReturn(testTrainee);
        when(trainerService.getTrainerById(trainerId)).thenReturn(testTrainer);
        when(trainingService.createTraining(traineeId, trainerId, trainingName, testTrainingType, trainingDate,
                duration)).thenReturn(testTraining);

        // Act
        Training result = gymFacade.createTrainingSession(traineeId, trainerId, trainingName, testTrainingType,
                trainingDate, duration);

        // Assert
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(traineeService).getTraineeById(traineeId);
        verify(trainerService).getTrainerById(trainerId);
        verify(trainingService).createTraining(traineeId, trainerId, trainingName, testTrainingType, trainingDate,
                duration);
    }

    @Test
    void testCreateTrainingSession_TraineeNotFound() {
        // Arrange
        when(traineeService.getTraineeById(999L)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gymFacade.createTrainingSession(999L, 1L, "Training", testTrainingType, LocalDate.now(), 2));

        assertEquals("Trainee not found with id: 999", exception.getMessage());
        verify(trainingService, never()).createTraining(any(), any(), any(), any(), any(), anyInt());
    }

    @Test
    void testCreateTrainingSession_TrainerNotFound() {
        // Arrange
        when(traineeService.getTraineeById(1L)).thenReturn(testTrainee);
        when(trainerService.getTrainerById(999L)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gymFacade.createTrainingSession(1L, 999L, "Training", testTrainingType, LocalDate.now(), 2));

        assertEquals("Trainer not found with id: 999", exception.getMessage());
        verify(trainingService, never()).createTraining(any(), any(), any(), any(), any(), anyInt());
    }

    @Test
    void testGetTraineeTrainings() {
        // Arrange
        Long traineeId = 1L;
        testTraining.setTrainee(testTrainee);
        List<Training> allTrainings = Arrays.asList(testTraining);

        when(trainingService.getAllTrainings()).thenReturn(allTrainings);

        // Act
        List<Training> result = gymFacade.getTraineeTrainings(traineeId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingService).getAllTrainings();
    }

    @Test
    void testGetTrainerTrainings() {
        // Arrange
        Long trainerId = 1L;
        testTraining.setTrainer(testTrainer);
        List<Training> allTrainings = Arrays.asList(testTraining);

        when(trainingService.getAllTrainings()).thenReturn(allTrainings);

        // Act
        List<Training> result = gymFacade.getTrainerTrainings(trainerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining, result.get(0));
        verify(trainingService).getAllTrainings();
    }

    @Test
    void testUpdateTraineeProfile() {
        // Arrange
        when(traineeService.updateTrainee(testTrainee)).thenReturn(testTrainee);

        // Act
        Trainee result = gymFacade.updateTraineeProfile(testTrainee);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeService).updateTrainee(testTrainee);
    }

    @Test
    void testUpdateTrainerProfile() {
        // Arrange
        when(trainerService.updateTrainer(testTrainer)).thenReturn(testTrainer);

        // Act
        Trainer result = gymFacade.updateTrainerProfile(testTrainer);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerService).updateTrainer(testTrainer);
    }

    @Test
    void testDeleteTraineeProfile_Success() {
        // Arrange
        Long traineeId = 1L;
        when(traineeService.getTraineeById(traineeId)).thenReturn(testTrainee);

        // Act
        gymFacade.deleteTraineeProfile(traineeId);

        // Assert
        verify(traineeService).getTraineeById(traineeId);
        verify(traineeService).deleteTrainee(testTrainee);
    }

    @Test
    void testDeleteTraineeProfile_NotFound() {
        // Arrange
        Long traineeId = 999L;
        when(traineeService.getTraineeById(traineeId)).thenReturn(null);

        // Act
        gymFacade.deleteTraineeProfile(traineeId);

        // Assert
        verify(traineeService).getTraineeById(traineeId);
        verify(traineeService, never()).deleteTrainee(any());
    }

    @Test
    void testGetTraineeById() {
        // Arrange
        when(traineeService.getTraineeById(1L)).thenReturn(testTrainee);

        // Act
        Trainee result = gymFacade.getTraineeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeService).getTraineeById(1L);
    }

    @Test
    void testGetTrainerById() {
        // Arrange
        when(trainerService.getTrainerById(1L)).thenReturn(testTrainer);

        // Act
        Trainer result = gymFacade.getTrainerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerService).getTrainerById(1L);
    }

    @Test
    void testGetTrainingById() {
        // Arrange
        when(trainingService.getTrainingById(1L)).thenReturn(testTraining);

        // Act
        Training result = gymFacade.getTrainingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingService).getTrainingById(1L);
    }

    @Test
    void testGetAllTrainees() {
        // Arrange
        List<Trainee> trainees = Arrays.asList(testTrainee);
        when(traineeService.getAllTrainees()).thenReturn(trainees);

        // Act
        List<Trainee> result = gymFacade.getAllTrainees();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(traineeService).getAllTrainees();
    }

    @Test
    void testGetAllTrainers() {
        // Arrange
        List<Trainer> trainers = Arrays.asList(testTrainer);
        when(trainerService.getAllTrainers()).thenReturn(trainers);

        // Act
        List<Trainer> result = gymFacade.getAllTrainers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainerService).getAllTrainers();
    }

    @Test
    void testGetAllTrainings() {
        // Arrange
        List<Training> trainings = Arrays.asList(testTraining);
        when(trainingService.getAllTrainings()).thenReturn(trainings);

        // Act
        List<Training> result = gymFacade.getAllTrainings();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingService).getAllTrainings();
    }

    @Test
    void testAuthenticateTrainee() {
        // Arrange
        String username = "john.doe";
        String password = "password123";
        when(traineeService.authenticateTrainee(username, password)).thenReturn(testTrainee);

        // Act
        Trainee result = gymFacade.authenticateTrainee(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeService).authenticateTrainee(username, password);
    }

    @Test
    void testAuthenticateTrainer() {
        // Arrange
        String username = "jane.smith";
        String password = "password123";
        when(trainerService.authenticateTrainer(username, password)).thenReturn(testTrainer);

        // Act
        Trainer result = gymFacade.authenticateTrainer(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerService).authenticateTrainer(username, password);
    }

    @Test
    void testGetTraineeByUsername() {
        // Arrange
        String username = "john.doe";
        when(traineeService.getTraineeByUsername(username)).thenReturn(testTrainee);

        // Act
        Trainee result = gymFacade.getTraineeByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainee, result);
        verify(traineeService).getTraineeByUsername(username);
    }

    @Test
    void testGetTrainerByUsername() {
        // Arrange
        String username = "jane.smith";
        when(trainerService.getTrainerByUsername(username)).thenReturn(testTrainer);

        // Act
        Trainer result = gymFacade.getTrainerByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(testTrainer, result);
        verify(trainerService).getTrainerByUsername(username);
    }

    @Test
    void testChangeTraineePassword() {
        // Arrange
        String username = "john.doe";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        // Act
        gymFacade.changeTraineePassword(username, oldPassword, newPassword);

        // Assert
        verify(traineeService).changeTraineePassword(username, oldPassword, newPassword);
    }

    @Test
    void testChangeTrainerPassword() {
        // Arrange
        String username = "jane.smith";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        // Act
        gymFacade.changeTrainerPassword(username, oldPassword, newPassword);

        // Assert
        verify(trainerService).changeTrainerPassword(username, oldPassword, newPassword);
    }

    @Test
    void testActivateTrainee() {
        // Arrange
        String username = "john.doe";

        // Act
        gymFacade.activateTrainee(username);

        // Assert
        verify(traineeService).activateTrainee(username);
    }

    @Test
    void testDeactivateTrainee() {
        // Arrange
        String username = "john.doe";

        // Act
        gymFacade.deactivateTrainee(username);

        // Assert
        verify(traineeService).deactivateTrainee(username);
    }

    @Test
    void testActivateTrainer() {
        // Arrange
        String username = "jane.smith";

        // Act
        gymFacade.activateTrainer(username);

        // Assert
        verify(trainerService).activateTrainer(username);
    }

    @Test
    void testDeactivateTrainer() {
        // Arrange
        String username = "jane.smith";

        // Act
        gymFacade.deactivateTrainer(username);

        // Assert
        verify(trainerService).deactivateTrainer(username);
    }

    @Test
    void testDeleteTraineeByUsername() {
        // Arrange
        String username = "john.doe";

        // Act
        gymFacade.deleteTraineeByUsername(username);

        // Assert
        verify(traineeService).deleteTraineeByUsername(username);
    }

    @Test
    void testGetTraineeTrainingsWithCriteria() {
        // Arrange
        String traineeUsername = "john.doe";
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now().plusDays(7);
        String trainerName = "Jane";
        String trainingTypeName = "Yoga";
        List<Training> trainings = Arrays.asList(testTraining);

        when(trainingService.getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate, trainerName,
                trainingTypeName)).thenReturn(trainings);

        // Act
        List<Training> result = gymFacade.getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate,
                trainerName, trainingTypeName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingService).getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate, trainerName,
                trainingTypeName);
    }

    @Test
    void testGetTrainerTrainingsWithCriteria() {
        // Arrange
        String trainerUsername = "jane.smith";
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now().plusDays(7);
        String traineeName = "John";
        List<Training> trainings = Arrays.asList(testTraining);

        when(trainingService.getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate, traineeName))
                .thenReturn(trainings);

        // Act
        List<Training> result = gymFacade.getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate,
                traineeName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingService).getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate, traineeName);
    }

    @Test
    void testGetTrainersNotAssignedToTrainee() {
        // Arrange
        String traineeUsername = "john.doe";
        List<Trainer> trainers = Arrays.asList(testTrainer);

        when(trainerService.getTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);

        // Act
        List<Trainer> result = gymFacade.getTrainersNotAssignedToTrainee(traineeUsername);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainerService).getTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void testUpdateTraineeTrainersList() {
        // Arrange
        String traineeUsername = "john.doe";
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");

        // Act
        gymFacade.updateTraineeTrainersList(traineeUsername, trainerUsernames);

        // Assert
        verify(traineeService).updateTraineeTrainersList(traineeUsername, trainerUsernames);
    }
}
