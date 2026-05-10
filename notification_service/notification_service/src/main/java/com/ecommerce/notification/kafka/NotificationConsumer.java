package com.ecommerce.notification.kafka;

import com.ecommerce.payment.event.OrderPaidEvent;
import com.ecommerce.notification.service.EmailService;
import com.ecommerce.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;
    private final SmsService smsService;

    @KafkaListener(
            topics = "payment-events",
            groupId = "notification-group"
    )
    public void onOrderPaid(OrderPaidEvent event) {
        System.out.println("hello");
        // if any of these throws exception -> retry -> then DLQ
        emailService.sendOrderPaidEmail(event);
        smsService.sendOrderPaidSms(event);

        System.out.println("✅ Notification done for orderId: " + event.getOrderId());
    }
}

