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

import com.epam.springCoreTask.dto.TraineeTrainingCriteriaDTO;
import com.epam.springCoreTask.dto.TrainerTrainingCriteriaDTO;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.exception.TraineeNotAvailableException;
import com.epam.springCoreTask.exception.TrainerNotAssignedException;
import com.epam.springCoreTask.exception.TrainerNotAvailableException;
import com.epam.springCoreTask.exception.ValidationException;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TraineeRepository;
import com.epam.springCoreTask.repository.TrainerRepository;
import com.epam.springCoreTask.repository.TrainingRepository;
import com.epam.springCoreTask.repository.TrainingTypeRepository;
import com.epam.springCoreTask.util.ValidationUtil;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private Training testTraining;
    private TrainingType testTrainingType;
    private User traineeUser;
    private User trainerUser;

    @BeforeEach
    void setUp() {
        traineeUser = new User();
        traineeUser.setUsername("john.doe");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        trainerUser = new User();
        trainerUser.setUsername("jane.smith");
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");

        testTrainingType = new TrainingType();
        testTrainingType.setId(1L);
        testTrainingType.setName("Yoga");

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(traineeUser);
        testTrainee.setTrainers(new ArrayList<>());

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(trainerUser);
        testTrainer.setSpecialization(testTrainingType);

        testTrainee.getTrainers().add(testTrainer);

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
    void testCreateTraining_Success() {
        // Arrange
        Long traineeId = 1L;
        Long trainerId = 1L;
        String trainingName = "Morning Yoga";
        LocalDate trainingDate = LocalDate.now().plusDays(1);
        int duration = 2;

        when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(testTrainingType.getId())).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.findByTrainee_User_Username(traineeUser.getUsername())).thenReturn(new ArrayList<>());
        when(trainingRepository.findByTrainer_User_Username(trainerUser.getUsername())).thenReturn(new ArrayList<>());
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        // Act
        Training result = trainingService.createTraining(traineeId, trainerId, trainingName, testTrainingType,
                trainingDate, duration);

        // Assert
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(validationUtil).validateNotNull(traineeId, "Trainee ID");
        verify(validationUtil).validateNotNull(trainerId, "Trainer ID");
        verify(validationUtil).validateNotBlank(trainingName, "Training name");
        verify(validationUtil).validateTrainingDate(trainingDate);
        verify(validationUtil).validateTrainingDuration(duration);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void testCreateTraining_TraineeNotFound() {
        // Arrange
        when(traineeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> trainingService.createTraining(999L, 1L, "Training", testTrainingType, LocalDate.now(), 2));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCreateTraining_TrainerNotFound() {
        // Arrange
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> trainingService.createTraining(1L, 999L, "Training", testTrainingType, LocalDate.now(), 2));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCreateTraining_TrainerNotAssigned() {
        // Arrange
        testTrainee.getTrainers().clear();

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));

        // Act & Assert
        assertThrows(TrainerNotAssignedException.class,
                () -> trainingService.createTraining(1L, 1L, "Training", testTrainingType, LocalDate.now(), 2));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCreateTraining_TrainingTypeNotFound() {
        // Arrange
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(testTrainingType.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> trainingService.createTraining(1L, 1L, "Training", testTrainingType, LocalDate.now(), 2));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCreateTraining_SpecializationMismatch() {
        // Arrange
        TrainingType differentType = new TrainingType();
        differentType.setId(2L);
        differentType.setName("Pilates");

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(differentType.getId())).thenReturn(Optional.of(differentType));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> trainingService.createTraining(1L, 1L, "Training", differentType, LocalDate.now(), 2));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCreateTraining_TraineeNotAvailable() {
        // Arrange
        LocalDate trainingDate = LocalDate.now().plusDays(1);
        Training existingTraining = new Training();
        existingTraining.setTrainingDate(trainingDate);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(testTrainingType.getId())).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.findByTrainee_User_Username(traineeUser.getUsername()))
                .thenReturn(Arrays.asList(existingTraining));

        // Act & Assert
        assertThrows(TraineeNotAvailableException.class, () -> trainingService.createTraining(1L, 1L, "Training",
                testTrainingType, trainingDate, 2));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testCreateTraining_TrainerNotAvailable() {
        // Arrange
        LocalDate trainingDate = LocalDate.now().plusDays(1);
        Training existingTraining = new Training();
        existingTraining.setTrainingDate(trainingDate);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(testTrainingType.getId())).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.findByTrainee_User_Username(traineeUser.getUsername())).thenReturn(new ArrayList<>());
        when(trainingRepository.findByTrainer_User_Username(trainerUser.getUsername()))
                .thenReturn(Arrays.asList(existingTraining));

        // Act & Assert
        assertThrows(TrainerNotAvailableException.class, () -> trainingService.createTraining(1L, 1L, "Training",
                testTrainingType, trainingDate, 2));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testGetTrainingById_Found() {
        // Arrange
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));

        // Act
        Training result = trainingService.getTrainingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingRepository).findById(1L);
    }

    @Test
    void testGetTrainingById_NotFound() {
        // Arrange
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Training result = trainingService.getTrainingById(999L);

        // Assert
        assertNull(result);
        verify(trainingRepository).findById(999L);
    }

    @Test
    void testGetAllTrainings_Success() {
        // Arrange
        List<Training> trainings = Arrays.asList(testTraining, new Training());
        when(trainingRepository.findAll()).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.getAllTrainings();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingRepository).findAll();
    }

    @Test
    void testGetTraineeTrainingsWithCriteria_Success() {
        // Arrange
        String traineeUsername = "john.doe";
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now().plusDays(7);
        String trainerName = "Jane";
        String trainingTypeName = "Yoga";

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(testTrainee));
        when(trainingTypeRepository.findByName(trainingTypeName)).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.findTraineeTrainings(any(TraineeTrainingCriteriaDTO.class)))
                .thenReturn(Arrays.asList(testTraining));

        // Act
        List<Training> result = trainingService.getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate,
                trainerName, trainingTypeName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validationUtil).validateNotBlank(traineeUsername, "Trainee username");
        verify(validationUtil).validateReasonableDateRange(fromDate, toDate);
        verify(traineeRepository).findByUser_Username(traineeUsername);
        verify(trainingTypeRepository).findByName(trainingTypeName);
        verify(trainingRepository).findTraineeTrainings(any(TraineeTrainingCriteriaDTO.class));
    }

    @Test
    void testGetTraineeTrainingsWithCriteria_TraineeNotFound() {
        // Arrange
        String traineeUsername = "unknown";

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> trainingService.getTraineeTrainingsWithCriteria(
                traineeUsername, null, null, null, null));

        verify(trainingRepository, never()).findTraineeTrainings(any());
    }

    @Test
    void testGetTraineeTrainingsWithCriteria_TrainingTypeNotFound() {
        // Arrange
        String traineeUsername = "john.doe";
        String trainingTypeName = "InvalidType";

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(testTrainee));
        when(trainingTypeRepository.findByName(trainingTypeName)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> trainingService.getTraineeTrainingsWithCriteria(
                traineeUsername, null, null, null, trainingTypeName));

        verify(trainingRepository, never()).findTraineeTrainings(any());
    }

    @Test
    void testGetTraineeTrainingsWithCriteria_NullOptionalParams() {
        // Arrange
        String traineeUsername = "john.doe";

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(testTrainee));
        when(trainingRepository.findTraineeTrainings(any(TraineeTrainingCriteriaDTO.class)))
                .thenReturn(Arrays.asList(testTraining));

        // Act
        List<Training> result = trainingService.getTraineeTrainingsWithCriteria(traineeUsername, null, null, null,
                null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingRepository).findTraineeTrainings(any(TraineeTrainingCriteriaDTO.class));
    }

    @Test
    void testGetTrainerTrainingsWithCriteria_Success() {
        // Arrange
        String trainerUsername = "jane.smith";
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now().plusDays(7);
        String traineeName = "John";

        when(trainerRepository.findByUser_Username(trainerUsername)).thenReturn(Optional.of(testTrainer));
        when(trainingRepository.findTrainerTrainings(any(TrainerTrainingCriteriaDTO.class)))
                .thenReturn(Arrays.asList(testTraining));

        // Act
        List<Training> result = trainingService.getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate,
                traineeName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validationUtil).validateNotBlank(trainerUsername, "Trainer username");
        verify(validationUtil).validateReasonableDateRange(fromDate, toDate);
        verify(trainerRepository).findByUser_Username(trainerUsername);
        verify(trainingRepository).findTrainerTrainings(any(TrainerTrainingCriteriaDTO.class));
    }

    @Test
    void testGetTrainerTrainingsWithCriteria_TrainerNotFound() {
        // Arrange
        String trainerUsername = "unknown";

        when(trainerRepository.findByUser_Username(trainerUsername)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> trainingService.getTrainerTrainingsWithCriteria(trainerUsername, null, null, null));

        verify(trainingRepository, never()).findTrainerTrainings(any());
    }

    @Test
    void testGetTrainerTrainingsWithCriteria_NullOptionalParams() {
        // Arrange
        String trainerUsername = "jane.smith";

        when(trainerRepository.findByUser_Username(trainerUsername)).thenReturn(Optional.of(testTrainer));
        when(trainingRepository.findTrainerTrainings(any(TrainerTrainingCriteriaDTO.class)))
                .thenReturn(Arrays.asList(testTraining));

        // Act
        List<Training> result = trainingService.getTrainerTrainingsWithCriteria(trainerUsername, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingRepository).findTrainerTrainings(any(TrainerTrainingCriteriaDTO.class));
    }
}
