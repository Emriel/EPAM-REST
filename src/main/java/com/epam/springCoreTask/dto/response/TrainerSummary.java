package com.epam.springCoreTask.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer summary information")
public class TrainerSummary {

    @Schema(description = "Trainer username", example = "jane.smith")
    private String username;

    @Schema(description = "Trainer first name", example = "Jane")
    private String firstName;

    @Schema(description = "Trainer last name", example = "Smith")
    private String lastName;

    @Schema(description = "Trainer specialization", example = "Fitness")
    private String specialization;
}
