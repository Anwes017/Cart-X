package com.ecommerce.order.controller;

import com.ecommerce.order.entity.OrderEntity;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository repo;

    @GetMapping("/my")
    public List<OrderEntity> myOrders(@RequestHeader("X-User-Id") String userId) {
        return repo.findByUserId(userId);
    }

    @GetMapping("/admin")
    public List<OrderEntity> all() {
        return repo.findAll();
    }
}