package com.example.email_micro_service.service.impl;

import com.example.email_micro_service.config.EmailProperties;
import com.example.email_micro_service.dto.OrderConfirmationEmailRequest;
import com.example.email_micro_service.exception.EmailSendingException;
import com.example.email_micro_service.service.EmailService;
import com.example.email_micro_service.template.OrderConfirmationTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final OrderConfirmationTemplateBuilder templateBuilder;
    private final EmailProperties emailProperties;

    @Override
    public void sendOrderConfirmationEmail(OrderConfirmationEmailRequest request) {
        int maxAttempts = emailProperties.getRetry().getMaxAttempts();
        long delayMs = emailProperties.getRetry().getDelayMs();

        validateEmailConfiguration();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Attempt {} to send order confirmation email. orderId={}, recipient={}",
                        attempt, request.getOrderId(), request.getEmail());
                sendEmail(request);
                log.info("Order confirmation email sent successfully. orderId={}, recipient={}",
                        request.getOrderId(), request.getEmail());
                return;
            } catch (MailException | MessagingException ex) {
                log.error("Email sending attempt {} failed. orderId={}, recipient={}. reason={}",
                        attempt, request.getOrderId(), request.getEmail(), ex.getMessage(), ex);
                if (attempt == maxAttempts) {
                    throw new EmailSendingException("Failed to send email after " + maxAttempts + " attempts", ex);
                }

                sleep(delayMs);
            }
        }
    }

    private void validateEmailConfiguration() {
        if (!StringUtils.hasText(emailProperties.getFrom())) {
            throw new EmailSendingException("Email sender address is not configured. Set 'email.from' or EMAIL_FROM.");
        }
    }

    private void sendEmail(OrderConfirmationEmailRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(emailProperties.getFrom());
        helper.setTo(request.getEmail());
        helper.setSubject("Order Confirmation - " + request.getOrderId());
        helper.setText(templateBuilder.buildOrderConfirmationHtml(request), true);
        mailSender.send(mimeMessage);
    }

    private void sleep(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new EmailSendingException("Retry delay was interrupted", interruptedException);
        }
    }
}
