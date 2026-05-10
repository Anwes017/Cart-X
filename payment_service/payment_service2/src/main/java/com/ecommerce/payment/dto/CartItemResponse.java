package com.ecommerce.payment.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private String productId;
    private int quantity;
}