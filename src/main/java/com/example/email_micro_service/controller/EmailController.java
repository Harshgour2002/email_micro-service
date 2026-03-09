package com.example.email_micro_service.controller;

import com.example.email_micro_service.dto.ApiResponse;
import com.example.email_micro_service.dto.OrderConfirmationEmailRequest;
import com.example.email_micro_service.service.EmailService;
import com.example.email_micro_service.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/order-confirmation")
    public ResponseEntity<ApiResponse<Void>> sendOrderConfirmation(@Valid @RequestBody OrderConfirmationEmailRequest request) {
        log.info("Received order confirmation email request. orderId={}, email={}", request.getOrderId(), request.getEmail());
        emailService.sendOrderConfirmationEmail(request);
        return ResponseEntity.accepted().body(ResponseUtil.success("Email sent successfully", null));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Email service is running");
    }
}
