package com.ecommerce.notification.service;

import com.ecommerce.payment.event.OrderPaidEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger log =
            LoggerFactory.getLogger(SmsService.class);

    public void sendOrderPaidSms(OrderPaidEvent event) {

        // MOCK SMS (LOG ONLY)
        log.info(
                "[MOCK SMS] To: {} | OrderId: {} | Amount: {}",
                event.getPhone(),
                event.getOrderId(),
                event.getAmount()
        );
    }
}
