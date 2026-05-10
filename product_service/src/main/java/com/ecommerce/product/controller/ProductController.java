package com.ecommerce.product.controller;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping
    public List<Product> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Product one(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Product create(@RequestBody Product p) {
        return service.create(p);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product p) {
        return service.update(id, p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @GetMapping("/{id}/stock")
    public boolean checkStock(
            @PathVariable String id,
            @RequestParam int qty
    ) {
        return service.hasSufficientStock(id, qty);
    }

    @PostMapping("/internal/{id}/reduce-stock")
    public void reduceStock(
            @PathVariable String id,
            @RequestParam int qty
    ) {
        service.reduceStock(id, qty);
    }
}