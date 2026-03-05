package com.epam.springCoreTask.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Change password request")
public class ChangePasswordRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "john.doe")
    private String username;

    @NotBlank(message = "Old password is required")
    @Schema(description = "Old password", example = "oldPassword123")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "New password", example = "newPassword123")
    private String newPassword;
}
