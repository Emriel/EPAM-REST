package com.epam.springCoreTask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym CRM System API")
                        .version("1.0")
                        .description("REST API for Gym Customer Relationship Management System")
                        .contact(new Contact()
                                .name("EPAM Systems")
                                .email("support@epam.com")))
                .components(new Components()
                        .addSecuritySchemes("username", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Username"))
                        .addSecuritySchemes("password", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Password")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("username")
                        .addList("password"));
    }
}
