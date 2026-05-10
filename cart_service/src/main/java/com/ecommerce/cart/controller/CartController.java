package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.CartItemRequest;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @PostMapping("/items")
    public void add(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CartItemRequest req
    ) {
        service.add(userId, req);
    }

    @GetMapping
    public CartResponse get(@RequestHeader("X-User-Id") String userId) {
        return service.getCart(userId);
    }

    // INTERNAL (called by order-service after success)
    @DeleteMapping("/internal/clear")
    public void clear(@RequestParam String userId) {
        service.clear(userId);
    }
}