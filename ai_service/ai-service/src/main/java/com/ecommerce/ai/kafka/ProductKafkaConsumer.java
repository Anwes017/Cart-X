package com.ecommerce.ai.kafka;

import com.ecommerce.ai.service.ProductEmbeddingService;
import com.ecommerce.product.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductKafkaConsumer {

    private final ProductEmbeddingService embeddingService;

    @KafkaListener(
            topics = "product-created",
            groupId = "ai-service-group"
    )
    public void consume(ProductCreatedEvent event) {
        embeddingService.storeProduct(event);
        System.out.println("✅ Stored product in Chroma: " + event.getName());
    }
}