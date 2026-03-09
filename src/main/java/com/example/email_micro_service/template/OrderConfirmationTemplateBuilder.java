package com.example.email_micro_service.template;

import com.example.email_micro_service.dto.OrderConfirmationEmailRequest;
import com.example.email_micro_service.dto.OrderItemDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderConfirmationTemplateBuilder {

    public String buildOrderConfirmationHtml(OrderConfirmationEmailRequest request) {
        StringBuilder rows = new StringBuilder();
        for (OrderItemDto item : request.getItems()) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            rows.append("""
                    <tr>
                        <td style=\"padding:10px;border-bottom:1px solid #e5e7eb;\">%s</td>
                        <td style=\"padding:10px;border-bottom:1px solid #e5e7eb;text-align:center;\">%d</td>
                        <td style=\"padding:10px;border-bottom:1px solid #e5e7eb;text-align:right;\">₹%s</td>
                        <td style=\"padding:10px;border-bottom:1px solid #e5e7eb;text-align:right;\">₹%s</td>
                    </tr>
                    """.formatted(item.getName(), item.getQuantity(), item.getPrice(), lineTotal));
        }

        return """
                <!doctype html>
                <html lang=\"en\">
                <head><meta charset=\"UTF-8\"><title>Order Confirmation</title></head>
                <body style=\"font-family:Arial,sans-serif;background:#f7fafc;margin:0;padding:0;\">
                  <div style=\"max-width:700px;margin:30px auto;background:#fff;border:1px solid #e5e7eb;border-radius:8px;overflow:hidden;\">
                    <div style=\"background:#111827;color:white;padding:20px 24px;\">
                      <h2 style=\"margin:0;\">E-Commerce Store</h2>
                      <p style=\"margin:6px 0 0;font-size:14px;\">Order Confirmation</p>
                    </div>
                    <div style=\"padding:24px;\">
                      <p>Hi <strong>%s</strong>,</p>
                      <p>Thank you for your purchase. Your order has been confirmed successfully.</p>
                      <p><strong>Order ID:</strong> %s<br/>
                         <strong>Order Date:</strong> %s<br/>
                         <strong>Payment Method:</strong> %s</p>

                      <table style=\"width:100%%;border-collapse:collapse;margin-top:16px;\">
                        <thead>
                          <tr style=\"background:#f3f4f6;\">
                            <th style=\"text-align:left;padding:10px;\">Product</th>
                            <th style=\"text-align:center;padding:10px;\">Qty</th>
                            <th style=\"text-align:right;padding:10px;\">Price</th>
                            <th style=\"text-align:right;padding:10px;\">Line Total</th>
                          </tr>
                        </thead>
                        <tbody>
                          %s
                        </tbody>
                      </table>

                      <p style=\"text-align:right;font-size:18px;margin-top:18px;\"><strong>Total: ₹%s</strong></p>
                      <p style=\"margin-top:24px;\">We appreciate your trust in us and hope to see you again soon.</p>
                    </div>
                    <div style=\"background:#f9fafb;color:#6b7280;padding:14px 24px;font-size:12px;text-align:center;\">
                      © 2026 E-Commerce Store. All rights reserved.
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                request.getCustomerName(),
                request.getOrderId(),
                request.getOrderDate(),
                request.getPaymentMethod(),
                rows,
                request.getOrderAmount()
        );
    }
}
