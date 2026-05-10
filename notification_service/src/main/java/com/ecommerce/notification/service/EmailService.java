package com.ecommerce.notification.service;

import com.ecommerce.payment.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderPaidEmail(OrderPaidEvent event) {

        if (event.getEmail() == null) {
            System.out.println("⚠️ Email is NULL, skipping email");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.getEmail());
        message.setSubject("Payment Successful - Order " + event.getOrderId());

        message.setText(
                "Hi,\n\n" +
                        "Your payment of ₹" + event.getAmount() + " was successful.\n\n" +
                        "Order ID: " + event.getOrderId() + "\n\n" +
                        "Thanks,\nEcommerce Team"
        );

        mailSender.send(message);

        System.out.println("📧 Email sent to " + event.getEmail());
    }
}