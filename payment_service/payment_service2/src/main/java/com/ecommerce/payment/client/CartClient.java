package com.ecommerce.payment.client;

import com.ecommerce.payment.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "cart-service", url = "${services.cart}")
public interface CartClient {

    @GetMapping("/cart")
    CartResponse getCart(@RequestHeader("X-User-Id") String userId);
}