package com.ecommerce.ai.service;

import com.ecommerce.product.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final VectorStore vectorStore;

    public void storeProduct(ProductCreatedEvent event) {

        String content = """
                Product Id: %s
                Product Name: %s
                Category: %s
                Price: %s
                Description: %s
                """.formatted(
                safe(event.getId()),
                safe(event.getName()),
                safe(event.getCategory()),
                event.getPrice() == null ? "N/A" : event.getPrice(),
                safe(event.getDescription())
        );

        // Metadata helps later for filtering/debugging
        Map<String, Object> metadata = Map.of(
                "productId", safe(event.getId()),
                "name", safe(event.getName()),
                "category", safe(event.getCategory()),
                "price", event.getPrice() == null ? 0 : event.getPrice()
        );

        Document doc = new Document(content, metadata);
        vectorStore.add(List.of(doc));
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}