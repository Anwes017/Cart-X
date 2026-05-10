package com.ecommerce.payment.service;

import com.ecommerce.common.event.PaymentSuccessEvent;
import com.ecommerce.payment.client.CartClient;
import com.ecommerce.payment.client.ProductClient;
import com.ecommerce.payment.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CartClient cartClient;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // STEP 1: Checkout
    public CheckoutResponse checkout(String userId) {

        CartResponse cart = cartClient.getCart(userId);

        long totalAmount = 0; // paise

        for (CartItemResponse item : cart.getItems()) {
            ProductResponse product =
                    productClient.getProduct(item.getProductId());

            // price assumed in rupees → convert to paise
            totalAmount += (long) product.getPrice() * 100 * item.getQuantity();
        }

        String paymentId = "MOCK_PAY_" + UUID.randomUUID();

        return new CheckoutResponse(paymentId, totalAmount,"INR","CREATED");
    }

    // STEP 2: Confirm payment
    public void confirm(String userId, ConfirmPaymentRequest req) {

        if (!"SUCCESS".equalsIgnoreCase(req.getStatus())) {
            return; // payment failed → DO NOTHING
        }

        CartResponse cart = cartClient.getCart(userId);

        // 🔁 CONVERT CartItemResponse → PaymentSuccessEvent.Item
        List<PaymentSuccessEvent.Item> items =
                cart.getItems().stream()
                        .map(ci -> new PaymentSuccessEvent.Item(
                                ci.getProductId(),
                                ci.getQuantity()
                        ))
                        .toList();

        PaymentSuccessEvent event = new PaymentSuccessEvent();
        event.setUserId(userId);
        event.setPaymentId(req.getPaymentId());
        event.setAmount(req.getAmount());
        event.setItems(items);

        kafkaTemplate.send("payment-success", event);
    }
}