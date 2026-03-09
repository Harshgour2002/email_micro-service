package com.example.email_micro_service.service;

import com.example.email_micro_service.dto.OrderConfirmationEmailRequest;

public interface EmailService {
    void sendOrderConfirmationEmail(OrderConfirmationEmailRequest request);
}
