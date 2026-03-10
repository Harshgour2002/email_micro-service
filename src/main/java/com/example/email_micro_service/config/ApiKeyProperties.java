package com.example.email_micro_service.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@ConfigurationProperties(prefix = "security.api-key")
public class ApiKeyProperties {
    public ApiKeyProperties(){}
    
    private boolean enabled;
    private String value;
}
