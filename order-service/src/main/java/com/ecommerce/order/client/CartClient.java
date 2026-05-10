package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "cart-service", url = "${services.cart}")
public interface CartClient {

    @DeleteMapping("/cart/internal/clear")
    void clear(@RequestParam String userId);
}