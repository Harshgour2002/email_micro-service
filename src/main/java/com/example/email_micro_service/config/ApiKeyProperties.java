package com.example.email_micro_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.api-key")
public class ApiKeyProperties {
    private boolean enabled;
    private String value;
}
