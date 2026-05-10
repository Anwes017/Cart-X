package com.ecommerce.cart.client;

import com.ecommerce.cart.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "${services.product}")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductResponse getById(@PathVariable String id);
}