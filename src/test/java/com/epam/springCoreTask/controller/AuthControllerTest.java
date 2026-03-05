package com.epam.springCoreTask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.epam.springCoreTask.config.AuthenticationInterceptor;
import com.epam.springCoreTask.config.LoggingInterceptor;
import com.epam.springCoreTask.exception.AuthenticationException;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.facade.GymFacade;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.User;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymFacade gymFacade;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private LoggingInterceptor loggingInterceptor;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() throws Exception {
        when(loggingInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        
        User traineeUser = new User(1L, "John", "Doe", "john.doe", "password123", true);
        trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User(2L, "Jane", "Smith", "jane.smith", "password123", true);
        trainer = new Trainer();
        trainer.setUser(trainerUser);
    }

    @Test
    void testLogin_AsTrainee_Success() throws Exception {
        when(gymFacade.authenticateTrainee("john.doe", "password123")).thenReturn(trainee);

        mockMvc.perform(get("/api/auth/login")
                .param("username", "john.doe")
                .param("password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).authenticateTrainee("john.doe", "password123");
    }

    @Test
    void testLogin_AsTrainer_Success() throws Exception {
        when(gymFacade.authenticateTrainee("jane.smith", "password123"))
                .thenThrow(new EntityNotFoundException("Trainee not found"));
        when(gymFacade.authenticateTrainer("jane.smith", "password123")).thenReturn(trainer);

        mockMvc.perform(get("/api/auth/login")
                .param("username", "jane.smith")
                .param("password", "password123"))
                .andExpect(status().isOk());

        verify(gymFacade).authenticateTrainer("jane.smith", "password123");
    }

    @Test
    void testLogin_InvalidCredentials_Unauthorized() throws Exception {
        when(gymFacade.authenticateTrainee("invalid", "wrong"))
                .thenThrow(new EntityNotFoundException("User not found"));
        when(gymFacade.authenticateTrainer("invalid", "wrong"))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/api/auth/login")
                .param("username", "invalid")
                .param("password", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_MissingUsername_BadRequest() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                .param("password", "password123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangePassword_AsTrainee_Success() throws Exception {
        doNothing().when(gymFacade).changeTraineePassword("john.doe", "oldPass", "newPass");

        String requestBody = "{\n" +
                "  \"username\": \"john.doe\",\n" +
                "  \"oldPassword\": \"oldPass\",\n" +
                "  \"newPassword\": \"newPass\"\n" +
                "}";

        mockMvc.perform(put("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(gymFacade).changeTraineePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    void testChangePassword_AsTrainer_Success() throws Exception {
        doThrow(new EntityNotFoundException("Trainee not found"))
                .when(gymFacade).changeTraineePassword("jane.smith", "oldPass", "newPass");
        doNothing().when(gymFacade).changeTrainerPassword("jane.smith", "oldPass", "newPass");

        String requestBody = "{\n" +
                "  \"username\": \"jane.smith\",\n" +
                "  \"oldPassword\": \"oldPass\",\n" +
                "  \"newPassword\": \"newPass\"\n" +
                "}";

        mockMvc.perform(put("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(gymFacade).changeTrainerPassword("jane.smith", "oldPass", "newPass");
    }

    @Test
    void testChangePassword_InvalidOldPassword_Unauthorized() throws Exception {
        doThrow(new AuthenticationException("Invalid old password"))
                .when(gymFacade).changeTraineePassword("john.doe", "wrongOld", "newPass");
        doThrow(new AuthenticationException("Invalid old password"))
                .when(gymFacade).changeTrainerPassword("john.doe", "wrongOld", "newPass");

        String requestBody = "{\n" +
                "  \"username\": \"john.doe\",\n" +
                "  \"oldPassword\": \"wrongOld\",\n" +
                "  \"newPassword\": \"newPass\"\n" +
                "}";

        mockMvc.perform(put("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
