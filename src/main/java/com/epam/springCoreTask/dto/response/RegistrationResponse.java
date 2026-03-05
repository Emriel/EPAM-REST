package com.epam.springCoreTask.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registration response with credentials")
public class RegistrationResponse {

    @Schema(description = "Generated username", example = "john.doe")
    private String username;

    @Schema(description = "Generated password", example = "aBc123XyZ")
    private String password;
}
