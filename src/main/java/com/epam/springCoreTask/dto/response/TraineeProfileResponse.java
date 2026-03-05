package com.epam.springCoreTask.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainee profile response")
public class TraineeProfileResponse {

    @Schema(description = "Username", example = "john.doe")
    private String username;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Address", example = "123 Main St, New York, NY")
    private String address;

    @Schema(description = "Active status", example = "true")
    private Boolean isActive;

    @Schema(description = "List of assigned trainers")
    private List<TrainerSummary> trainers;
}
