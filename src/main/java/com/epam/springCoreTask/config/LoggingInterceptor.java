package com.epam.springCoreTask.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);
        request.setAttribute(START_TIME, System.currentTimeMillis());
        
        log.info("Request started - Method: {}, URI: {}, Transaction ID: {}", 
                request.getMethod(), request.getRequestURI(), transactionId);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - startTime;
        
        log.info("Request completed - Method: {}, URI: {}, Status: {}, Duration: {}ms, Transaction ID: {}", 
                request.getMethod(), request.getRequestURI(), response.getStatus(), 
                duration, MDC.get(TRANSACTION_ID));
        
        if (ex != null) {
            log.error("Request failed with exception: {}", ex.getMessage(), ex);
        }
        
        MDC.clear();
    }
}
