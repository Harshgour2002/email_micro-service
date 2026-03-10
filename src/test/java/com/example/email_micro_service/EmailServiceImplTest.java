package com.example.email_micro_service;

import com.example.email_micro_service.config.EmailProperties;
import com.example.email_micro_service.dto.OrderConfirmationEmailRequest;
import com.example.email_micro_service.dto.OrderItemDto;
import com.example.email_micro_service.exception.EmailSendingException;
import com.example.email_micro_service.service.impl.EmailServiceImpl;
import com.example.email_micro_service.template.OrderConfirmationTemplateBuilder;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceImplTest {

    private JavaMailSender mailSender;
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);

        EmailProperties properties = new EmailProperties();
        properties.setFrom("sender@example.com");
        properties.getRetry().setMaxAttempts(2);
        properties.getRetry().setDelayMs(0);

        emailService = new EmailServiceImpl(mailSender, new OrderConfirmationTemplateBuilder(), properties);
    }

    @Test
    void shouldRetryAndThrowWhenSendingFails() {
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(mailSender.send(mimeMessage)).thenThrow(new MailSendException("SMTP down"));

        assertThatThrownBy(() -> emailService.sendOrderConfirmationEmail(validRequest()))
                .isInstanceOf(EmailSendingException.class)
                .hasMessageContaining("Failed to send email after 2 attempts");

        verify(mailSender, times(2)).send(mimeMessage);
    }

    @Test
    void shouldFailFastWhenFromAddressMissing() {
        EmailProperties brokenProperties = new EmailProperties();
        brokenProperties.setFrom(" ");
        EmailServiceImpl serviceWithInvalidConfig = new EmailServiceImpl(mailSender, new OrderConfirmationTemplateBuilder(), brokenProperties);

        assertThatThrownBy(() -> serviceWithInvalidConfig.sendOrderConfirmationEmail(validRequest()))
                .isInstanceOf(EmailSendingException.class)
                .hasMessageContaining("Email sender address is not configured");
    }

    private OrderConfirmationEmailRequest validRequest() {
        return OrderConfirmationEmailRequest.builder()
                .email("customer@example.com")
                .customerName("Test User")
                .orderId("ORD-1001")
                .orderAmount(new BigDecimal("120.50"))
                .paymentMethod("CARD")
                .orderDate(LocalDate.now())
                .items(List.of(OrderItemDto.builder()
                        .name("Book")
                        .quantity(1)
                        .price(new BigDecimal("120.50"))
                        .build()))
                .build();
    }
}
