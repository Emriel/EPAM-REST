package com.epam.springCoreTask.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.springCoreTask.dto.request.TrainingRequest;
import com.epam.springCoreTask.dto.response.TrainingTypeResponse;
import com.epam.springCoreTask.facade.GymFacade;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.repository.TrainingTypeRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/trainings")
@Validated
@Slf4j
@Tag(name = "Training", description = "Training management endpoints")
public class TrainingController {

    @Autowired
    private GymFacade gymFacade;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Operation(summary = "Add training", description = "Create a new training session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Training created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request", 
                     content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Trainee or trainer not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingRequest request) {
        log.info("Creating training: {} for trainee: {} with trainer: {}", 
                 request.getTrainingName(), request.getTraineeUsername(), request.getTrainerUsername());
        
        gymFacade.createTrainingSession(request);
        
        log.info("Training created successfully: {}", request.getTrainingName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get training types", description = "Retrieve all available training types")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    })
    @GetMapping("/types")
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        log.info("Fetching all training types");
        
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        
        List<TrainingTypeResponse> response = trainingTypes.stream()
            .map(type -> new TrainingTypeResponse(type.getId(), type.getName()))
            .collect(Collectors.toList());
        
        log.info("Retrieved {} training types", trainingTypes.size());
        return ResponseEntity.ok(response);
    }
}
