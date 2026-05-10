package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.CheckoutResponse;
import com.ecommerce.payment.dto.ConfirmPaymentRequest;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestHeader("X-User-Id") String userId) throws Exception {
        return service.checkout(userId);
    }

    @PostMapping("/confirm")
    public void confirm(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ConfirmPaymentRequest req
    ) throws Exception {
        service.confirm(userId, req);
    }
}