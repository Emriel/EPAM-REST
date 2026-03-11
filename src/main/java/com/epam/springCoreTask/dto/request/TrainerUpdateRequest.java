package com.epam.springCoreTask.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer profile update request")
public class TrainerUpdateRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "jane.smith")
    private String username;

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", example = "Smith")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    @Schema(description = "Specialization", example = "Yoga")
    private String specialization;

    @NotNull(message = "Active status is required")
    @Schema(description = "Active status", example = "true")
    private Boolean isActive;
}
