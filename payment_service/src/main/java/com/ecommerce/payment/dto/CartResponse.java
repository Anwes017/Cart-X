package com.ecommerce.payment.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private String userId;
    private List<CartItemResponse> items;
}