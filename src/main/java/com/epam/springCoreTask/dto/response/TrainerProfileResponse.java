package com.epam.springCoreTask.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer profile response")
public class TrainerProfileResponse {

    @Schema(description = "Username", example = "jane.smith")
    private String username;

    @Schema(description = "First name", example = "Jane")
    private String firstName;

    @Schema(description = "Last name", example = "Smith")
    private String lastName;

    @Schema(description = "Specialization", example = "Fitness")
    private String specialization;

    @Schema(description = "Active status", example = "true")
    private Boolean isActive;

    @Schema(description = "List of assigned trainees")
    private List<TraineeSummary> trainees;
}
