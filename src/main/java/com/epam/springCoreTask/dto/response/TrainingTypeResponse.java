package com.epam.springCoreTask.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Training type information")
public class TrainingTypeResponse {

    @Schema(description = "Training type ID", example = "1")
    private Long id;

    @Schema(description = "Training type name", example = "Fitness")
    private String name;
}
