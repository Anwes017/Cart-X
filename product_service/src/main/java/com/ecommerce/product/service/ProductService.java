package com.ecommerce.product.service;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductStatus;
import com.ecommerce.product.event.ProductCreatedEvent;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String PRODUCT_CREATED_TOPIC = "product-created";

    private final ProductRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ===============================
    // CREATE PRODUCT
    // ===============================
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product create(Product product) {

        product.setStatus(
                product.getStockQuantity() > 0
                        ? ProductStatus.IN_STOCK
                        : ProductStatus.OUT_OF_STOCK
        );

        Product saved = repository.save(product);

        // 🔔 Kafka event → AI Service (Chroma indexing)
        ProductCreatedEvent event = new ProductCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getCategory(),   // ✅ use real category
                saved.getPrice()
        );

        kafkaTemplate.send(PRODUCT_CREATED_TOPIC, saved.getId(), event);
        System.out.println("📤 Product CREATED event sent: " + saved.getName());

        return saved;
    }

    // ===============================
    // GET ALL PRODUCTS
    // ===============================
    @Cacheable("products")
    public List<Product> getAll() {
        return repository.findAll();
    }

    // ===============================
    // GET PRODUCT BY ID
    // ===============================
    @Cacheable(value = "product", key = "#id")
    public Product getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ===============================
    // UPDATE PRODUCT
    // ===============================
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product update(String id, Product updated) {

        Product existing = getById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setImageUrls(updated.getImageUrls());
        existing.setCategory(updated.getCategory());

        existing.setStatus(
                updated.getStockQuantity() > 0
                        ? ProductStatus.IN_STOCK
                        : ProductStatus.OUT_OF_STOCK
        );

        Product saved = repository.save(existing);

        // 🔁 Re-embed product in Chroma (update = overwrite vector)
        ProductCreatedEvent event = new ProductCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getCategory(),
                saved.getPrice()
        );

        kafkaTemplate.send(PRODUCT_CREATED_TOPIC, saved.getId(), event);
        System.out.println("📤 Product UPDATED event sent: " + saved.getName());

        return saved;
    }

    // ===============================
    // DELETE PRODUCT
    // ===============================
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void delete(String id) {
        repository.deleteById(id);
        // ❌ No Kafka event needed (optional, can be added later)
    }

    // check STOCK (in cart)
    public boolean hasSufficientStock(String productId, int qty) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return product.getStockQuantity() >= qty;
    }

    // ===============================
    // REDUCE STOCK (AFTER PAYMENT)
    // ===============================
    public void reduceStock(String productId, int qty) {

        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < qty) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setStockQuantity(product.getStockQuantity() - qty);

        product.setStatus(
                product.getStockQuantity() > 0
                        ? ProductStatus.IN_STOCK
                        : ProductStatus.OUT_OF_STOCK
        );

        repository.save(product);
    }
}