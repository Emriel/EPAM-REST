package com.epam.springCoreTask.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Training information response")
public class TrainingResponse {

    @Schema(description = "Training name", example = "Morning Yoga Session")
    private String trainingName;

    @Schema(description = "Training date", example = "2026-03-10")
    private LocalDate trainingDate;

    @Schema(description = "Training type", example = "Fitness")
    private String trainingType;

    @Schema(description = "Training duration in minutes", example = "60")
    private Integer trainingDuration;

    @Schema(description = "Trainer name", example = "Jane Smith")
    private String trainerName;

    @Schema(description = "Trainee name", example = "John Doe")
    private String traineeName;
}
