package com.epam.springCoreTask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import com.epam.springCoreTask.dto.request.TrainerRegistrationRequest;
import com.epam.springCoreTask.dto.request.TrainerUpdateRequest;
import com.epam.springCoreTask.dto.response.RegistrationResponse;
import com.epam.springCoreTask.dto.response.TraineeSummary;
import com.epam.springCoreTask.dto.response.TrainerProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerSummary;
import com.epam.springCoreTask.dto.response.TrainingResponse;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.facade.GymFacade;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymFacade gymFacade;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private LoggingInterceptor loggingInterceptor;

    private TrainerProfileResponse trainerProfileResponse;
    private RegistrationResponse registrationResponse;
    private TraineeSummary traineeSummary;

    @BeforeEach
    void setUp() throws Exception {
        when(loggingInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        traineeSummary = new TraineeSummary("john.doe", "John", "Doe");

        trainerProfileResponse = new TrainerProfileResponse(
                "jane.smith", "Jane", "Smith", "Fitness", true,
                List.of(traineeSummary)
        );

        registrationResponse = new RegistrationResponse("jane.smith", "password123");
    }

    @Test
    void testRegisterTrainer_Success() throws Exception {
        when(gymFacade.createTrainerProfile(any(TrainerRegistrationRequest.class)))
                .thenReturn(registrationResponse);

        String requestBody = "{\n" +
                "  \"firstName\": \"Jane\",\n" +
                "  \"lastName\": \"Smith\",\n" +
                "  \"specialization\": \"Fitness\"\n" +
                "}";

        mockMvc.perform(post("/api/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("jane.smith"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(gymFacade).createTrainerProfile(any(TrainerRegistrationRequest.class));
    }

    @Test
    void testRegisterTrainer_MissingRequiredFields_BadRequest() throws Exception {
        String requestBody = "{\n" +
                "  \"firstName\": \"Jane\"\n" +
                "}";

        mockMvc.perform(post("/api/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTrainerProfile_Success() throws Exception {
        when(gymFacade.getTrainerByUsername("jane.smith")).thenReturn(trainerProfileResponse);

        mockMvc.perform(get("/api/trainers/username/{username}", "jane.smith")
                .header("Username", "jane.smith")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.smith"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization").value("Fitness"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees[0].username").value("john.doe"));

        verify(gymFacade).getTrainerByUsername("jane.smith");
    }

    @Test
    void testGetTrainerProfile_NotFound() throws Exception {
        when(gymFacade.getTrainerByUsername("unknown")).thenThrow(
                new EntityNotFoundException("Trainer not found"));

        mockMvc.perform(get("/api/trainers/username/{username}", "unknown")
                .header("Username", "jane.smith")
                .header("Password", "password123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTrainerProfile_Success() throws Exception {
        when(gymFacade.updateTrainerProfile(any(TrainerUpdateRequest.class)))
                .thenReturn(trainerProfileResponse);

        String requestBody = "{\n" +
                "  \"username\": \"jane.smith\",\n" +
                "  \"firstName\": \"Jane\",\n" +
                "  \"lastName\": \"Smith\",\n" +
                "  \"specialization\": \"Yoga\",\n" +
                "  \"isActive\": true\n" +
                "}";

        mockMvc.perform(put("/api/trainers/username/{username}", "jane.smith")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "jane.smith")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.smith"));

        verify(gymFacade).updateTrainerProfile(any(TrainerUpdateRequest.class));
    }

    @Test
    void testChangeTrainerStatus_Activate_Success() throws Exception {
        doNothing().when(gymFacade).activateTrainer("jane.smith");

        String requestBody = "{\n" +
                "  \"username\": \"jane.smith\",\n" +
                "  \"isActive\": true\n" +
                "}";

        mockMvc.perform(patch("/api/trainers/username/{username}/status", "jane.smith")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "jane.smith")
                .header("Password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).activateTrainer("jane.smith");
    }

    @Test
    void testChangeTrainerStatus_Deactivate_Success() throws Exception {
        doNothing().when(gymFacade).deactivateTrainer("jane.smith");

        String requestBody = "{\n" +
                "  \"username\": \"jane.smith\",\n" +
                "  \"isActive\": false\n" +
                "}";

        mockMvc.perform(patch("/api/trainers/username/{username}/status", "jane.smith")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "jane.smith")
                .header("Password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).deactivateTrainer("jane.smith");
    }

    @Test
    void testGetUnassignedTrainers_Success() throws Exception {
        TrainerSummary trainerSummary = new TrainerSummary("jane.smith", "Jane", "Smith", "Fitness");
        List<TrainerSummary> trainers = List.of(trainerSummary);
        when(gymFacade.getTrainersNotAssignedToTrainee("john.doe")).thenReturn(trainers);

        mockMvc.perform(get("/api/trainers/unassigned")
                .param("traineeUsername", "john.doe")
                .header("Username", "jane.smith")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jane.smith"))
                .andExpect(jsonPath("$[0].specialization").value("Fitness"));

        verify(gymFacade).getTrainersNotAssignedToTrainee("john.doe");
    }

    @Test
    void testGetTrainerTrainings_Success() throws Exception {
        TrainingResponse trainingResponse = new TrainingResponse(
                "Morning Yoga",
                LocalDate.of(2026, 3, 10),
                "Fitness",
                60,
                "Jane Smith",
                "John Doe"
        );

        List<TrainingResponse> trainings = List.of(trainingResponse);
        when(gymFacade.getTrainerTrainingsWithCriteria(anyString(), any(), any(), any()))
                .thenReturn(trainings);

        mockMvc.perform(get("/api/trainers/username/{username}/trainings", "jane.smith")
                .header("Username", "jane.smith")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Yoga"))
                .andExpect(jsonPath("$[0].trainingType").value("Fitness"));

        verify(gymFacade).getTrainerTrainingsWithCriteria(anyString(), any(), any(), any());
    }
}
