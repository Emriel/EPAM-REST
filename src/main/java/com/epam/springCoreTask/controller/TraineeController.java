package com.epam.springCoreTask.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.springCoreTask.dto.request.ActivationRequest;
import com.epam.springCoreTask.dto.request.TraineeRegistrationRequest;
import com.epam.springCoreTask.dto.request.TraineeUpdateRequest;
import com.epam.springCoreTask.dto.request.UpdateTrainersListRequest;
import com.epam.springCoreTask.dto.response.RegistrationResponse;
import com.epam.springCoreTask.dto.response.TraineeProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerSummary;
import com.epam.springCoreTask.dto.response.TrainingResponse;
import com.epam.springCoreTask.facade.GymFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/trainees")
@Validated
@Slf4j
@Tag(name = "Trainee", description = "Trainee management endpoints")
public class TraineeController {

    @Autowired
    private GymFacade gymFacade;

    @Operation(summary = "Register trainee", description = "Create a new trainee profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trainee registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest request) {
        log.info("Registering new trainee: {} {}", request.getFirstName(), request.getLastName());
        
        RegistrationResponse response = gymFacade.createTraineeProfile(request);
        
        log.info("Trainee registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get trainee profile", description = "Retrieve trainee profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(
            @Parameter(description = "Username of the trainee", required = true)
            @RequestParam String username) {
        log.info("Fetching profile for trainee: {}", username);
        
        TraineeProfileResponse response = gymFacade.getTraineeByUsername(username);
        
        log.info("Profile retrieved for trainee: {}", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update trainee profile", description = "Update trainee profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee not found", 
                     content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping
    public ResponseEntity<TraineeProfileResponse> updateTraineeProfile(
            @Valid @RequestBody TraineeUpdateRequest request) {
        log.info("Updating profile for trainee: {}", request.getUsername());
        
        TraineeProfileResponse response = gymFacade.updateTraineeProfile(request);
        
        log.info("Profile updated for trainee: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete trainee profile", description = "Delete trainee profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteTraineeProfile(
            @Parameter(description = "Username of the trainee", required = true)
            @RequestParam String username) {
        log.info("Deleting profile for trainee: {}", username);
        
        gymFacade.deleteTraineeByUsername(username);
        
        log.info("Profile deleted for trainee: {}", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate/Deactivate trainee", description = "Change trainee activation status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status changed successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> changeTraineeStatus(
            @Valid @RequestBody ActivationRequest request) {
        log.info("Changing status for trainee: {} to {}", request.getUsername(), request.getIsActive());
        
        if (request.getIsActive()) {
            gymFacade.activateTrainee(request.getUsername());
        } else {
            gymFacade.deactivateTrainee(request.getUsername());
        }
        
        log.info("Status changed for trainee: {}", request.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update trainee's trainers list", 
               description = "Update the list of trainers assigned to a trainee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainers list updated successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee or trainer not found", 
                     content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerSummary>> updateTrainersList(
            @Valid @RequestBody UpdateTrainersListRequest request) {
        log.info("Updating trainers list for trainee: {}", request.getTraineeUsername());
        
        List<TrainerSummary> trainers = gymFacade.updateTraineeTrainersList(
            request.getTraineeUsername(), 
            request.getTrainerUsernames()
        );
        
        log.info("Trainers list updated for trainee: {}", request.getTraineeUsername());
        return ResponseEntity.ok(trainers);
    }

    @Operation(summary = "Get trainee trainings", 
               description = "Retrieve trainings for a trainee with optional filters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainings(
            @Parameter(description = "Username of the trainee", required = true)
            @RequestParam String username,
            @Parameter(description = "Filter by period from date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter by period to date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter by trainer name")
            @RequestParam(required = false) String trainerName,
            @Parameter(description = "Filter by training type")
            @RequestParam(required = false) String trainingType) {
        log.info("Fetching trainings for trainee: {}", username);
        
        List<TrainingResponse> response = gymFacade.getTraineeTrainingsWithCriteria(
            username, fromDate, toDate, trainerName, trainingType
        );
        
        log.info("Retrieved {} trainings for trainee: {}", response.size(), username);
        return ResponseEntity.ok(response);
    }
}
