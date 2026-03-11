package com.epam.springCoreTask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.epam.springCoreTask.dto.request.TraineeRegistrationRequest;
import com.epam.springCoreTask.dto.request.TraineeUpdateRequest;
import com.epam.springCoreTask.dto.response.RegistrationResponse;
import com.epam.springCoreTask.dto.response.TraineeProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerSummary;
import com.epam.springCoreTask.dto.response.TrainingResponse;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.facade.GymFacade;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymFacade gymFacade;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private LoggingInterceptor loggingInterceptor;

    private TraineeProfileResponse traineeProfileResponse;
    private RegistrationResponse registrationResponse;
    private TrainerSummary trainerSummary;

    @BeforeEach
    void setUp() throws Exception {
        when(loggingInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        trainerSummary = new TrainerSummary("jane.smith", "Jane", "Smith", "Fitness");

        traineeProfileResponse = new TraineeProfileResponse(
                "john.doe", "John", "Doe",
                LocalDate.of(1990, 1, 15), "123 Main St", true,
                List.of(trainerSummary)
        );

        registrationResponse = new RegistrationResponse("john.doe", "password123");
    }

    @Test
    void testRegisterTrainee_Success() throws Exception {
        when(gymFacade.createTraineeProfile(any(TraineeRegistrationRequest.class)))
                .thenReturn(registrationResponse);

        String requestBody = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"dateOfBirth\": \"1990-01-15\",\n" +
                "  \"address\": \"123 Main St\"\n" +
                "}";

        mockMvc.perform(post("/api/trainees/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(gymFacade).createTraineeProfile(any(TraineeRegistrationRequest.class));
    }

    @Test
    void testRegisterTrainee_MissingRequiredFields_BadRequest() throws Exception {
        String requestBody = "{\n" +
                "  \"firstName\": \"John\"\n" +
                "}";

        mockMvc.perform(post("/api/trainees/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterTrainee_WithoutOptionalFields_Success() throws Exception {
        when(gymFacade.createTraineeProfile(any(TraineeRegistrationRequest.class)))
                .thenReturn(registrationResponse);

        String requestBody = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\"\n" +
                "}";

        mockMvc.perform(post("/api/trainees/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(gymFacade).createTraineeProfile(any(TraineeRegistrationRequest.class));
    }

    @Test
    void testGetTraineeProfile_Success() throws Exception {
        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(traineeProfileResponse);

        mockMvc.perform(get("/api/trainees/username/{username}", "john.doe")
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers[0].username").value("jane.smith"));

        verify(gymFacade).getTraineeByUsername("john.doe");
    }

    @Test
    void testGetTraineeProfile_NotFound() throws Exception {
        when(gymFacade.getTraineeByUsername("unknown")).thenThrow(
                new EntityNotFoundException("Trainee not found"));

        mockMvc.perform(get("/api/trainees/username/{username}", "unknown")
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTraineeProfile_Success() throws Exception {
        when(gymFacade.updateTraineeProfile(any(TraineeUpdateRequest.class)))
                .thenReturn(traineeProfileResponse);

        String requestBody = "{\n" +
                "  \"username\": \"john.doe\",\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"dateOfBirth\": \"1990-01-15\",\n" +
                "  \"address\": \"456 New St\",\n" +
                "  \"isActive\": true\n" +
                "}";

        mockMvc.perform(put("/api/trainees/username/{username}", "john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));

        verify(gymFacade).updateTraineeProfile(any(TraineeUpdateRequest.class));
    }

    @Test
    void testDeleteTraineeProfile_Success() throws Exception {
        doNothing().when(gymFacade).deleteTraineeByUsername("john.doe");

        mockMvc.perform(delete("/api/trainees/username/{username}", "john.doe")
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).deleteTraineeByUsername("john.doe");
    }

    @Test
    void testChangeTraineeStatus_Activate_Success() throws Exception {
        doNothing().when(gymFacade).activateTrainee("john.doe");

        String requestBody = "{\n" +
                "  \"username\": \"john.doe\",\n" +
                "  \"isActive\": true\n" +
                "}";

        mockMvc.perform(patch("/api/trainees/username/{username}/status", "john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).activateTrainee("john.doe");
    }

    @Test
    void testChangeTraineeStatus_Deactivate_Success() throws Exception {
        doNothing().when(gymFacade).deactivateTrainee("john.doe");

        String requestBody = "{\n" +
                "  \"username\": \"john.doe\",\n" +
                "  \"isActive\": false\n" +
                "}";

        mockMvc.perform(patch("/api/trainees/username/{username}/status", "john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).deactivateTrainee("john.doe");
    }

    @Test
    void testUpdateTrainersList_Success() throws Exception {
        List<TrainerSummary> updatedTrainers = List.of(trainerSummary);
        when(gymFacade.updateTraineeTrainersList(anyString(), anyList())).thenReturn(updatedTrainers);

        String requestBody = "{\n" +
                "  \"traineeUsername\": \"john.doe\",\n" +
                "  \"trainerUsernames\": [\"jane.smith\"]\n" +
                "}";

        mockMvc.perform(put("/api/trainees/username/{username}/trainers", "john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jane.smith"));

        verify(gymFacade).updateTraineeTrainersList(anyString(), anyList());
    }

    @Test
    void testGetTraineeTrainings_Success() throws Exception {
        TrainingResponse trainingResponse = new TrainingResponse(
                "Morning Yoga",
                LocalDate.of(2026, 3, 10),
                "Fitness",
                60,
                "Jane Smith",
                "John Doe"
        );

        List<TrainingResponse> trainings = List.of(trainingResponse);
        when(gymFacade.getTraineeTrainingsWithCriteria(anyString(), any(), any(), any(), any()))
                .thenReturn(trainings);

        mockMvc.perform(get("/api/trainees/username/{username}/trainings", "john.doe")
                .header("Username", "john.doe")
                .header("Password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Yoga"))
                .andExpect(jsonPath("$[0].trainingType").value("Fitness"));

        verify(gymFacade).getTraineeTrainingsWithCriteria(anyString(), any(), any(), any(), any());
    }
}
