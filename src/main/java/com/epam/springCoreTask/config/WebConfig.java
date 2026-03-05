package com.epam.springCoreTask.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add logging interceptor for all requests
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**");
        
        // Add authentication interceptor for all requests except registration
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/trainees/register",
                    "/api/trainers/register",
                    "/api/training-types",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                );
    }
}
