package com.epam.springCoreTask.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainee summary information")
public class TraineeSummary {

    @Schema(description = "Trainee username", example = "john.doe")
    private String username;

    @Schema(description = "Trainee first name", example = "John")
    private String firstName;

    @Schema(description = "Trainee last name", example = "Doe")
    private String lastName;
}
