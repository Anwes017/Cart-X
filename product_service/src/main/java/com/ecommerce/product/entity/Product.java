package com.ecommerce.product.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private String id;
    private String name;
    private String category;
    private String description;
    private double price;
    private int stockQuantity;
    private ProductStatus status;

    // CDN URLs
    private List<String> imageUrls;
}