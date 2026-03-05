package com.epam.springCoreTask.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer registration request")
public class TrainerRegistrationRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", example = "Smith")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    @Schema(description = "Training type specialization", example = "Fitness")
    private String specialization;
}
