package com.epam.springCoreTask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.springCoreTask.dto.request.ChangePasswordRequest;
import com.epam.springCoreTask.facade.GymFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Validated
@Slf4j
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthController {

    @Autowired
    private GymFacade gymFacade;

    @Operation(summary = "User login", description = "Authenticate user with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/login")
    public ResponseEntity<Void> login(
            @Parameter(description = "Username", required = true) @RequestParam String username,
            @Parameter(description = "Password", required = true) @RequestParam String password) {
        log.info("Login attempt for user: {}", username);
        
        gymFacade.authenticateUser(username, password);
        log.info("User login successful: {}", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password", description = "Change user password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", 
                     content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "User not found", 
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("Password change request for user: {}", request.getUsername());
        
        gymFacade.changeUserPassword(request.getUsername(), 
                                     request.getOldPassword(), 
                                     request.getNewPassword());
        log.info("User password changed successfully: {}", request.getUsername());
        return ResponseEntity.ok().build();
    }
}
