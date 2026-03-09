package com.example.email_micro_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final ApiKeyProperties apiKeyProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!apiKeyProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String endpoint = request.getRequestURI();
        if (!endpoint.startsWith("/api/v1/email")) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedApiKey = request.getHeader(API_KEY_HEADER);
        String configuredApiKey = apiKeyProperties.getValue();

        if (!StringUtils.hasText(configuredApiKey) || !configuredApiKey.equals(providedApiKey)) {
            log.warn("Unauthorized API key access attempt for endpoint: {}", endpoint);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Invalid or missing API key\",\"data\":null}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
