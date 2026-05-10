package com.ecommerce.cart.service;

import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {

    private final StringRedisTemplate redis;
    private final ProductClient productClient;

    private String key(String userId) {
        return "cart:" + userId;
    }

    public void add(String userId, CartItemRequest req) {

        // ✅ STOCK CHECK (mandatory)
        ProductResponse product = productClient.getById(req.getProductId());

        if (product.getStockQuantity() < req.getQuantity()) {
            throw new RuntimeException("Out of stock for product " + product.getId());
        }

        // store: productId -> qty
        redis.opsForHash().put(key(userId), req.getProductId(), String.valueOf(req.getQuantity()));
    }

    public CartResponse getCart(String userId) {
        Map<Object, Object> map = redis.opsForHash().entries(key(userId));

        List<CartItemResponse> items = new ArrayList<>();
        for (Map.Entry<Object, Object> e : map.entrySet()) {
            items.add(new CartItemResponse(
                    String.valueOf(e.getKey()),
                    Integer.parseInt(String.valueOf(e.getValue()))
            ));
        }
        return new CartResponse(userId, items);
    }

    public void clear(String userId) {
        redis.delete(key(userId));
    }
}