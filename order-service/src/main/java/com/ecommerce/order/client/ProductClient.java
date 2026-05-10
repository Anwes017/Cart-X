package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "${services.product}")
public interface ProductClient {

    @PostMapping("/products/internal/{id}/reduce")
    void reduceStock(@PathVariable String id, @RequestParam int qty);
}