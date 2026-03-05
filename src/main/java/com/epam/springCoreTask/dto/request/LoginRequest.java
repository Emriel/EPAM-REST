package com.epam.springCoreTask.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request")
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "john.doe")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "password123")
    private String password;
}
