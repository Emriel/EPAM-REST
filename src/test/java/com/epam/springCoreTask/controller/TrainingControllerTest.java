package com.epam.springCoreTask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.epam.springCoreTask.dto.request.TrainingRequest;
import com.epam.springCoreTask.facade.GymFacade;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.repository.TrainingTypeRepository;

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

    private TrainingType trainingType;

    @BeforeEach
    void setUp() throws Exception {
        when(loggingInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        trainingType = new TrainingType(1L, "Fitness");
    }

    @Test
    void testAddTraining_Success() throws Exception {
        doNothing().when(gymFacade).createTrainingSession(any(TrainingRequest.class));

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

        verify(gymFacade).createTrainingSession(any(TrainingRequest.class));
    }

    @Test
    void testAddTraining_TraineeNotFound_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Trainee not found with username: unknown"))
                .when(gymFacade).createTrainingSession(any(TrainingRequest.class));

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
                .andExpect(status().isBadRequest());

        verify(gymFacade).createTrainingSession(any(TrainingRequest.class));
    }

    @Test
    void testAddTraining_TrainerNotFound_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Trainer not found with username: unknown"))
                .when(gymFacade).createTrainingSession(any(TrainingRequest.class));

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
                .andExpect(status().isBadRequest());

        verify(gymFacade).createTrainingSession(any(TrainingRequest.class));
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
