package com.ecommerce.product.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreatedEvent {
    private String id;
    private String name;
    private String category;
    private Double price;
    private String description;
}