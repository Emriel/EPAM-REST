package com.epam.springCoreTask.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainee registration request")
public class TraineeRegistrationRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Address", example = "123 Main St, New York, NY")
    private String address;
}
