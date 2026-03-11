package com.epam.springCoreTask.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.springCoreTask.dto.request.ActivationRequest;
import com.epam.springCoreTask.dto.request.TrainerRegistrationRequest;
import com.epam.springCoreTask.dto.request.TrainerUpdateRequest;
import com.epam.springCoreTask.dto.response.RegistrationResponse;
import com.epam.springCoreTask.dto.response.TrainerProfileResponse;
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
@RequestMapping("/api/trainers")
@Validated
@Slf4j
@Tag(name = "Trainer", description = "Trainer management endpoints")
public class TrainerController {

    @Autowired
    private GymFacade gymFacade;

    @Operation(summary = "Register trainer", description = "Create a new trainer profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trainer registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest request) {
        log.info("Registering new trainer: {} {}", request.getFirstName(), request.getLastName());
        
        RegistrationResponse response = gymFacade.createTrainerProfile(request);
        
        log.info("Trainer registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get trainer profile", description = "Retrieve trainer profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Trainer not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable String username) {
        log.info("Fetching profile for trainer: {}", username);
        
        TrainerProfileResponse response = gymFacade.getTrainerByUsername(username);
        
        log.info("Profile retrieved for trainer: {}", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update trainer profile", description = "Update trainer profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "404", description = "Trainer not found", 
                     content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/username/{username}")
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable String username,
            @Valid @RequestBody TrainerUpdateRequest request) {
        log.info("Updating profile for trainer: {}", username);
        
        // Override username from path variable
        request.setUsername(username);
        TrainerProfileResponse response = gymFacade.updateTrainerProfile(request);
        
        log.info("Profile updated for trainer: {}", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate/Deactivate trainer", description = "Change trainer activation status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status changed successfully"),
        @ApiResponse(responseCode = "404", description = "Trainer not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PatchMapping("/username/{username}/status")
    public ResponseEntity<Void> changeTrainerStatus(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable String username,
            @Valid @RequestBody ActivationRequest request) {
        log.info("Changing status for trainer: {} to {}", username, request.getIsActive());
        
        if (request.getIsActive()) {
            gymFacade.activateTrainer(username);
        } else {
            gymFacade.deactivateTrainer(username);
        }
        
        log.info("Status changed for trainer: {}", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get unassigned trainers", 
               description = "Get trainers not assigned to a specific trainee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/unassigned")
    public ResponseEntity<List<TrainerSummary>> getUnassignedTrainers(
            @Parameter(description = "Username of the trainee", required = true)
            @RequestParam String traineeUsername) {
        log.info("Fetching unassigned trainers for trainee: {}", traineeUsername);
        
        List<TrainerSummary> response = gymFacade.getTrainersNotAssignedToTrainee(traineeUsername);
        
        log.info("Retrieved {} unassigned trainers for trainee: {}", response.size(), traineeUsername);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get trainer trainings", 
               description = "Retrieve trainings for a trainer with optional filters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Trainer not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/username/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable String username,
            @Parameter(description = "Filter by period from date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter by period to date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter by trainee name")
            @RequestParam(required = false) String traineeName) {
        log.info("Fetching trainings for trainer: {}", username);
        
        List<TrainingResponse> response = gymFacade.getTrainerTrainingsWithCriteria(
            username, fromDate, toDate, traineeName
        );
        
        log.info("Retrieved {} trainings for trainer: {}", response.size(), username);
        return ResponseEntity.ok(response);
    }
}
