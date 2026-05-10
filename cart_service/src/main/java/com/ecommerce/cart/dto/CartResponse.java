package com.ecommerce.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CartResponse {
    private String userId;
    private List<CartItemResponse> items;
}