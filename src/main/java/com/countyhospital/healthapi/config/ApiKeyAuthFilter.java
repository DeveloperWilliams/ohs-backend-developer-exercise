package com.countyhospital.healthapi.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final List<SimpleGrantedAuthority> API_USER_AUTHORITIES = 
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_USER"));

    private final String validApiKey;

    public ApiKeyAuthFilter(String validApiKey) {
        this.validApiKey = validApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestApiKey = extractApiKey(request);

        if (validApiKey == null || validApiKey.trim().isEmpty()) {
            logger.warn("API key authentication is enabled but no valid API key is configured");
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "API key authentication misconfigured");
            return;
        }

        if (requestApiKey == null) {
            LOGGER.warn("API key missing for request: {}", request.getRequestURI());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                            "API key required");
            return;
        }

        if (!isValidApiKey(requestApiKey)) {
            LOGGER.warn("Invalid API key provided for request: {}", request.getRequestURI());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                            "Invalid API key");
            return;
        }

        // API key is valid, set up security context
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken("api-user", null, API_USER_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LOGGER.debug("API key authentication successful for request: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        // Check header first
        String apiKey = request.getHeader(API_KEY_HEADER);
        
        // Fallback to query parameter (less secure, but sometimes needed)
        if (apiKey == null) {
            apiKey = request.getParameter("apiKey");
        }
        
        return apiKey;
    }

    private boolean isValidApiKey(String apiKey) {
        return validApiKey.equals(apiKey);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
            java.time.LocalDateTime.now(),
            status,
            HttpServletResponse.SC_UNAUTHORIZED == status ? "Unauthorized" : "Internal Server Error",
            message
        );
        
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip authentication for certain paths
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator/health") ||
               path.equals("/error");
    }
}