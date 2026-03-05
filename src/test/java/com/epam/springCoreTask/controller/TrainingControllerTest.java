package com.epam.springCoreTask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.epam.springCoreTask.config.AuthenticationInterceptor;
import com.epam.springCoreTask.config.LoggingInterceptor;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.facade.GymFacade;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;import com.epam.springCoreTask.repository.TrainingTypeRepository;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymFacade gymFacade;

    @MockBean
    private TrainingTypeRepository trainingTypeRepository;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private LoggingInterceptor loggingInterceptor;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;

    @BeforeEach
    void setUp() throws Exception {
        when(loggingInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        trainingType = new TrainingType(1L, "Fitness");

        User traineeUser = new User(1L, "John", "Doe", "john.doe", "password123", true);
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 15));
        trainee.setAddress("123 Main St");
        trainee.setTrainers(new ArrayList<>());

        User trainerUser = new User(2L, "Jane", "Smith", "jane.smith", "password123", true);
        trainer = new Trainer();
        trainer.setId(2L);
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new ArrayList<>());

        training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.of(2026, 3, 10));
        training.setTrainingDuration(60);
        training.setTrainingType(trainingType);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
    }

    @Test
    void testAddTraining_Success() throws Exception {
        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(trainee);
        when(gymFacade.getTrainerByUsername("jane.smith")).thenReturn(trainer);
        when(gymFacade.createTrainingSession(anyLong(), anyLong(), anyString(), any(), any(), anyInt()))
                .thenReturn(training);

        String requestBody = "{\n" +
                "  \"traineeUsername\": \"john.doe\",\n" +
                "  \"trainerUsername\": \"jane.smith\",\n" +
                "  \"trainingName\": \"Morning Yoga\",\n" +
                "  \"trainingDate\": \"2026-03-10\",\n" +
                "  \"trainingDuration\": 60\n" +
                "}";

        mockMvc.perform(post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).getTraineeByUsername("john.doe");
        verify(gymFacade).getTrainerByUsername("jane.smith");
        verify(gymFacade).createTrainingSession(anyLong(), anyLong(), anyString(), any(), any(), anyInt());
    }

    @Test
    void testAddTraining_TraineeNotFound_NotFound() throws Exception {
        when(gymFacade.getTraineeByUsername("unknown")).thenThrow(
                new EntityNotFoundException("Trainee not found"));

        String requestBody = "{\n" +
                "  \"traineeUsername\": \"unknown\",\n" +
                "  \"trainerUsername\": \"jane.smith\",\n" +
                "  \"trainingName\": \"Morning Yoga\",\n" +
                "  \"trainingDate\": \"2026-03-10\",\n" +
                "  \"trainingDuration\": 60\n" +
                "}";

        mockMvc.perform(post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isNotFound());

        verify(gymFacade).getTraineeByUsername("unknown");
    }

    @Test
    void testAddTraining_TrainerNotFound_NotFound() throws Exception {
        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(trainee);
        when(gymFacade.getTrainerByUsername("unknown")).thenThrow(
                new EntityNotFoundException("Trainer not found"));

        String requestBody = "{\n" +
                "  \"traineeUsername\": \"john.doe\",\n" +
                "  \"trainerUsername\": \"unknown\",\n" +
                "  \"trainingName\": \"Morning Yoga\",\n" +
                "  \"trainingDate\": \"2026-03-10\",\n" +
                "  \"trainingDuration\": 60\n" +
                "}";

        mockMvc.perform(post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isNotFound());

        verify(gymFacade).getTrainerByUsername("unknown");
    }

    @Test
    void testAddTraining_MissingRequiredFields_BadRequest() throws Exception {
        String requestBody = "{\n" +
                "  \"traineeUsername\": \"john.doe\",\n" +
                "  \"trainingName\": \"Morning Yoga\"\n" +
                "}";

        mockMvc.perform(post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddTraining_NegativeDuration_BadRequest() throws Exception {
        String requestBody = "{\n" +
                "  \"traineeUsername\": \"john.doe\",\n" +
                "  \"trainerUsername\": \"jane.smith\",\n" +
                "  \"trainingName\": \"Morning Yoga\",\n" +
                "  \"trainingDate\": \"2026-03-10\",\n" +
                "  \"trainingDuration\": -30\n" +
                "}";

        mockMvc.perform(post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTrainingTypes_Success() throws Exception {
        List<TrainingType> trainingTypes = List.of(
            new TrainingType(1L, "Fitness"),
            new TrainingType(2L, "Yoga"),
            new TrainingType(3L, "Cardio")
        );
        when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);

        mockMvc.perform(get("/api/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fitness"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Yoga"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Cardio"));

        verify(trainingTypeRepository).findAll();
    }

    @Test
    void testGetTrainingTypes_EmptyList_Success() throws Exception {
        when(trainingTypeRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(trainingTypeRepository).findAll();
    }
}
