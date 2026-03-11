package com.epam.springCoreTask.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update trainee's trainers list request")
public class UpdateTrainersListRequest {

    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Trainee username", example = "john.doe")
    private String traineeUsername;

    @NotEmpty(message = "Trainers list cannot be empty")
    @Schema(description = "List of trainer usernames")
    private List<String> trainerUsernames;
}
