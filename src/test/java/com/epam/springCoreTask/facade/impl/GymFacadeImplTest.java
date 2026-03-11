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

import com.epam.springCoreTask.dto.request.TraineeRegistrationRequest;
import com.epam.springCoreTask.dto.request.TraineeUpdateRequest;
import com.epam.springCoreTask.dto.request.TrainerRegistrationRequest;
import com.epam.springCoreTask.dto.request.TrainerUpdateRequest;
import com.epam.springCoreTask.dto.request.TrainingRequest;
import com.epam.springCoreTask.dto.response.RegistrationResponse;
import com.epam.springCoreTask.dto.response.TraineeProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerSummary;
import com.epam.springCoreTask.dto.response.TrainingResponse;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.mapper.TraineeMapper;
import com.epam.springCoreTask.mapper.TrainerMapper;
import com.epam.springCoreTask.mapper.TrainingMapper;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TrainingTypeRepository;
import com.epam.springCoreTask.service.TraineeService;
import com.epam.springCoreTask.service.TrainerService;
import com.epam.springCoreTask.service.TrainingService;
import com.epam.springCoreTask.service.UserService;

@ExtendWith(MockitoExtension.class)
class GymFacadeImplTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private UserService userService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private GymFacadeImpl gymFacade;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private Training testTraining;
    private TrainingType testTrainingType;
    private TraineeProfileResponse traineeProfileResponse;
    private TrainerProfileResponse trainerProfileResponse;
    private TrainingResponse trainingResponse;

    @BeforeEach
    void setUp() {
        User traineeUser = new User();
        traineeUser.setUsername("john.doe");
        traineeUser.setPassword("password123");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        User trainerUser = new User();
        trainerUser.setUsername("jane.smith");
        trainerUser.setPassword("password123");
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

        traineeProfileResponse = new TraineeProfileResponse("john.doe", "John", "Doe",
                LocalDate.of(1990, 1, 1), "123 Main St", true, List.of());

        trainerProfileResponse = new TrainerProfileResponse("jane.smith", "Jane", "Smith",
                "Yoga", true, List.of());

        trainingResponse = new TrainingResponse("Morning Yoga", LocalDate.now().plusDays(1),
                "Yoga", 2, "Jane Smith", "John Doe");
    }

    @Test
    void testCreateTraineeProfile() {
        // Arrange
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St");

        when(traineeService.createTrainee("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St"))
                .thenReturn(testTrainee);

        // Act
        RegistrationResponse result = gymFacade.createTraineeProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(traineeService).createTrainee("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St");
    }

    @Test
    void testCreateTrainerProfile() {
        // Arrange
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Jane", "Smith", "Yoga");

        when(trainerService.createTrainer("Jane", "Smith", "Yoga")).thenReturn(testTrainer);

        // Act
        RegistrationResponse result = gymFacade.createTrainerProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(trainerService).createTrainer("Jane", "Smith", "Yoga");
    }

    @Test
    void testCreateTrainingSession_Success() {
        // Arrange
        TrainingRequest request = new TrainingRequest("john.doe", "jane.smith",
                "Morning Yoga", LocalDate.now().plusDays(1), 2);

        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(testTrainee);
        when(trainerService.getTrainerByUsername("jane.smith")).thenReturn(testTrainer);

        // Act
        gymFacade.createTrainingSession(request);

        // Assert
        verify(traineeService).getTraineeByUsername("john.doe");
        verify(trainerService).getTrainerByUsername("jane.smith");
        verify(trainingService).createTraining(eq(1L), eq(1L), eq("Morning Yoga"),
                eq(testTrainingType), eq(LocalDate.now().plusDays(1)), eq(2));
    }

    @Test
    void testCreateTrainingSession_TraineeNotFound() {
        // Arrange
        TrainingRequest request = new TrainingRequest("unknown", "jane.smith",
                "Morning Yoga", LocalDate.now(), 2);
        
        when(traineeService.getTraineeByUsername("unknown"))
                .thenThrow(new EntityNotFoundException("Trainee not found with username: unknown"));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> gymFacade.createTrainingSession(request));

        assertEquals("Trainee not found with username: unknown", exception.getMessage());
        verify(trainingService, never()).createTraining(any(), any(), any(), any(), any(), anyInt());
    }

    @Test
    void testCreateTrainingSession_TrainerNotFound() {
        // Arrange
        TrainingRequest request = new TrainingRequest("john.doe", "unknown",
                "Morning Yoga", LocalDate.now(), 2);
        
        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(testTrainee);
        when(trainerService.getTrainerByUsername("unknown"))
                .thenThrow(new EntityNotFoundException("Trainer not found with username: unknown"));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> gymFacade.createTrainingSession(request));

        assertEquals("Trainer not found with username: unknown", exception.getMessage());
        verify(trainingService, never()).createTraining(any(), any(), any(), any(), any(), anyInt());
    }

    @Test
    void testUpdateTraineeProfile() {
        // Arrange
        TraineeUpdateRequest request = new TraineeUpdateRequest("john.doe", "John Updated",
                "Doe", LocalDate.of(1990, 1, 1), "456 New St", true);

        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(testTrainee);
        when(traineeService.updateTrainee(testTrainee)).thenReturn(testTrainee);
        when(traineeMapper.toProfileResponse(testTrainee)).thenReturn(traineeProfileResponse);

        // Act
        TraineeProfileResponse result = gymFacade.updateTraineeProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        verify(traineeService).getTraineeByUsername("john.doe");
        verify(traineeMapper).updateTraineeFromRequest(eq(request), eq(testTrainee));
        verify(traineeService).updateTrainee(testTrainee);
        verify(traineeMapper).toProfileResponse(testTrainee);
    }

    @Test
    void testUpdateTrainerProfile() {
        // Arrange
        TrainerUpdateRequest request = new TrainerUpdateRequest("jane.smith", "Jane Updated",
                "Smith", "Yoga", true);

        when(trainerService.getTrainerByUsername("jane.smith")).thenReturn(testTrainer);
        when(trainingTypeRepository.findByName("Yoga")).thenReturn(java.util.Optional.of(testTrainingType));
        when(trainerService.updateTrainer(testTrainer)).thenReturn(testTrainer);
        when(trainerMapper.toProfileResponse(testTrainer)).thenReturn(trainerProfileResponse);

        // Act
        TrainerProfileResponse result = gymFacade.updateTrainerProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        verify(trainerService).getTrainerByUsername("jane.smith");
        verify(trainerMapper).updateTrainerFromRequest(eq(request), eq(testTrainer));
        verify(trainingTypeRepository).findByName("Yoga");
        verify(trainerService).updateTrainer(testTrainer);
        verify(trainerMapper).toProfileResponse(testTrainer);
    }

    @Test
    void testGetTraineeByUsername() {
        // Arrange
        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(testTrainee);
        when(traineeMapper.toProfileResponse(testTrainee)).thenReturn(traineeProfileResponse);

        // Act
        TraineeProfileResponse result = gymFacade.getTraineeByUsername("john.doe");

        // Assert
        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals("John", result.getFirstName());
        verify(traineeService).getTraineeByUsername("john.doe");
        verify(traineeMapper).toProfileResponse(testTrainee);
    }

    @Test
    void testGetTrainerByUsername() {
        // Arrange
        when(trainerService.getTrainerByUsername("jane.smith")).thenReturn(testTrainer);
        when(trainerMapper.toProfileResponse(testTrainer)).thenReturn(trainerProfileResponse);

        // Act
        TrainerProfileResponse result = gymFacade.getTrainerByUsername("jane.smith");

        // Assert
        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        assertEquals("Jane", result.getFirstName());
        verify(trainerService).getTrainerByUsername("jane.smith");
        verify(trainerMapper).toProfileResponse(testTrainer);
    }

    @Test
    void testAuthenticateUser() {
        // Arrange
        User user = testTrainee.getUser();
        when(userService.authenticateUser("john.doe", "password123")).thenReturn(user);

        // Act
        User result = gymFacade.authenticateUser("john.doe", "password123");

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        verify(userService).authenticateUser("john.doe", "password123");
    }

    @Test
    void testChangeUserPassword() {
        // Arrange
        String username = "john.doe";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        // Act
        gymFacade.changeUserPassword(username, oldPassword, newPassword);

        // Assert
        verify(userService).changeUserPassword(username, oldPassword, newPassword);
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
        List<TrainingResponse> trainingResponses = Arrays.asList(trainingResponse);

        when(trainingService.getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate, trainerName,
                trainingTypeName)).thenReturn(trainings);
        when(trainingMapper.toTrainingResponseList(trainings)).thenReturn(trainingResponses);

        // Act
        List<TrainingResponse> result = gymFacade.getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate,
                trainerName, trainingTypeName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
        verify(trainingService).getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate, trainerName,
                trainingTypeName);
        verify(trainingMapper).toTrainingResponseList(trainings);
    }

    @Test
    void testGetTrainerTrainingsWithCriteria() {
        // Arrange
        String trainerUsername = "jane.smith";
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now().plusDays(7);
        String traineeName = "John";
        List<Training> trainings = Arrays.asList(testTraining);
        List<TrainingResponse> trainingResponses = Arrays.asList(trainingResponse);

        when(trainingService.getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate, traineeName))
                .thenReturn(trainings);
        when(trainingMapper.toTrainingResponseList(trainings)).thenReturn(trainingResponses);

        // Act
        List<TrainingResponse> result = gymFacade.getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate,
                traineeName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
        verify(trainingService).getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate, traineeName);
        verify(trainingMapper).toTrainingResponseList(trainings);
    }

    @Test
    void testGetTrainersNotAssignedToTrainee() {
        // Arrange
        String traineeUsername = "john.doe";
        List<Trainer> trainers = Arrays.asList(testTrainer);
        TrainerSummary trainerSummary = new TrainerSummary("jane.smith", "Jane", "Smith", "Yoga");
        List<TrainerSummary> trainerSummaries = Arrays.asList(trainerSummary);

        when(trainerService.getTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);
        when(trainerMapper.toTrainerSummaryList(trainers)).thenReturn(trainerSummaries);

        // Act
        List<TrainerSummary> result = gymFacade.getTrainersNotAssignedToTrainee(traineeUsername);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("jane.smith", result.get(0).getUsername());
        verify(trainerService).getTrainersNotAssignedToTrainee(traineeUsername);
        verify(trainerMapper).toTrainerSummaryList(trainers);
    }

    @Test
    void testUpdateTraineeTrainersList() {
        // Arrange
        String traineeUsername = "john.doe";
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");
        TrainerSummary trainerSummary = new TrainerSummary("jane.smith", "Jane", "Smith", "Yoga");
        List<TrainerSummary> trainerSummaries = List.of(trainerSummary);
        
        testTrainee.getTrainers().add(testTrainer);

        doNothing().when(traineeService).updateTraineeTrainersList(traineeUsername, trainerUsernames);
        when(traineeService.getTraineeByUsername(traineeUsername)).thenReturn(testTrainee);
        when(trainerMapper.toTrainerSummaryList(testTrainee.getTrainers())).thenReturn(trainerSummaries);

        // Act
        List<TrainerSummary> result = gymFacade.updateTraineeTrainersList(traineeUsername, trainerUsernames);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("jane.smith", result.get(0).getUsername());
        verify(traineeService).updateTraineeTrainersList(traineeUsername, trainerUsernames);
        verify(traineeService).getTraineeByUsername(traineeUsername);
        verify(trainerMapper).toTrainerSummaryList(testTrainee.getTrainers());
    }
}
