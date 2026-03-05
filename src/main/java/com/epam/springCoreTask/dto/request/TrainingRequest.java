package com.epam.springCoreTask.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Training creation request")
public class TrainingRequest {

    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Trainee username", example = "john.doe")
    private String traineeUsername;

    @NotBlank(message = "Trainer username is required")
    @Schema(description = "Trainer username", example = "jane.smith")
    private String trainerUsername;

    @NotBlank(message = "Training name is required")
    @Schema(description = "Training name", example = "Morning Yoga Session")
    private String trainingName;

    @NotNull(message = "Training date is required")
    @Schema(description = "Training date", example = "2026-03-10")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Positive(message = "Training duration must be positive")
    @Schema(description = "Training duration in minutes", example = "60")
    private Integer trainingDuration;
}
