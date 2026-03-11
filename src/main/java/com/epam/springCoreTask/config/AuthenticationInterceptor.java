package com.epam.springCoreTask.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.epam.springCoreTask.exception.AuthenticationException;
import com.epam.springCoreTask.facade.GymFacade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private GymFacade gymFacade;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = request.getHeader("Username");
        String password = request.getHeader("Password");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("Authentication failed: Missing credentials");
            throw new AuthenticationException("Username and password are required");
        }

        try {
            gymFacade.authenticateUser(username, password);
            log.debug("User authenticated successfully: {}", username);
            return true;
        } catch (Exception e) {
            log.warn("Authentication failed for user: {}", username);
            throw new AuthenticationException("Invalid username or password");
        }
    }
}
