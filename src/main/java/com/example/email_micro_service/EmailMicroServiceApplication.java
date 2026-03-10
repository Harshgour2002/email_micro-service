package com.example.email_micro_service;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EmailMicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailMicroServiceApplication.class, args);
    }
}
