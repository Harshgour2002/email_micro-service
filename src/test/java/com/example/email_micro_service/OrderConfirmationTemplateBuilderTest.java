package com.example.email_micro_service;

import com.example.email_micro_service.dto.OrderConfirmationEmailRequest;
import com.example.email_micro_service.dto.OrderItemDto;
import com.example.email_micro_service.template.OrderConfirmationTemplateBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderConfirmationTemplateBuilderTest {

    private final OrderConfirmationTemplateBuilder builder = new OrderConfirmationTemplateBuilder();

    @Test
    void shouldGenerateHtmlContainingOrderDetails() {
        OrderConfirmationEmailRequest request = OrderConfirmationEmailRequest.builder()
                .email("customer@email.com")
                .customerName("John Doe")
                .orderId("ORD12345")
                .orderAmount(new BigDecimal("799.00"))
                .paymentMethod("UPI")
                .orderDate(LocalDate.parse("2026-03-09"))
                .items(List.of(
                        OrderItemDto.builder().name("Product 1").quantity(1).price(new BigDecimal("499.00")).build(),
                        OrderItemDto.builder().name("Product 2").quantity(1).price(new BigDecimal("300.00")).build()
                ))
                .build();

        String html = builder.buildOrderConfirmationHtml(request);

        assertThat(html).contains("John Doe");
        assertThat(html).contains("ORD12345");
        assertThat(html).contains("Product 1");
        assertThat(html).contains("₹799.00");
    }
}
