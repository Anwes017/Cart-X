package com.ecommerce.order.consumer;

import com.ecommerce.common.event.PaymentSuccessEvent;
import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.entity.OrderEntity;
import com.ecommerce.order.entity.OrderItemEntity;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final OrderRepository repo;
    private final ProductClient productClient;
    private final CartClient cartClient;

    @KafkaListener(topics = "payment-success", groupId = "order-group")
    public void consume(PaymentSuccessEvent event) {

        // 1) Create order
        OrderEntity order = new OrderEntity();
        order.setUserId(event.getUserId());
        order.setPaymentId(event.getPaymentId());
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItemEntity> items = event.getItems().stream().map(i -> {
            OrderItemEntity oi = new OrderItemEntity();
            oi.setProductId(i.getProductId());
            oi.setQuantity(i.getQuantity());
            return oi;
        }).toList();

        order.setItems(items);

        // totalAmount is optional here because payment calculated it already
        order.setTotalAmount(0);

        repo.save(order);

        // 2) Reduce stock
        for (PaymentSuccessEvent.Item item : event.getItems()) {
            productClient.reduceStock(item.getProductId(), item.getQuantity());
        }

        // 3) Clear cart
        cartClient.clear(event.getUserId());
    }
}